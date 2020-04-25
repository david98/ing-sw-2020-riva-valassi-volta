package it.polimi.vovarini.controller;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.OverwrittenWorkerException;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Controller Tests")
public class ControllerTests {

  private static Controller controller;
  private static Game game;

  private static List<Phase> provideAllPhases(){
    LinkedList<Phase> phases = new LinkedList<>();

    for(Phase phase : Phase.values()){
      phases.add(phase);
    }
    return phases;
  }

  @Test
  @DisplayName("Controller instantiation")
  void controllerCreation() {
    try {
      game = new Game(2);
      controller = new Controller(game);
      assertEquals(game, controller.getGame());
    } catch (InvalidNumberOfPlayersException ignored) {
    }
  }

  @BeforeEach
  public void init() {
    try {
      game = new Game(2);

      try {
        game.addPlayer("playerOne");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length - 1]);
        return;
      }
      try {
        game.addPlayer("playerTwo");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length - 1]);
        return;
      }

      GodCard cardOne = new GodCard(GodName.Nobody, game);
      GodCard cardTwo = new GodCard(GodName.Nobody, game);

      game.getPlayers()[0].setGodCard(cardOne);
      game.getPlayers()[1].setGodCard(cardTwo);

      controller = new Controller(game);

    } catch (InvalidNumberOfPlayersException ignored) {
    }
  }

  @Test
  @DisplayName("Worker Selection due to a WorkerSelectionEvent. Tests associated sequence of calls")
  void workerSelectionTest() {

    WorkerSelectionEvent evtF =
            new WorkerSelectionEvent(game.getCurrentPlayer(), Sex.Female);
    try {
      controller.update(evtF);
    } catch (InvalidPhaseException e) {
      assertNotEquals(game.getCurrentPhase(), Phase.Start);
      return;
    } catch (WrongPlayerException ignored) {
    }
    assertEquals(game.getCurrentPlayer().getCurrentWorker().getSex(), Sex.Female);
    assertTrue(game.getCurrentPlayer().isWorkerSelected());

    WorkerSelectionEvent evtM = new WorkerSelectionEvent(game.getCurrentPlayer(), Sex.Male);
    try {
      controller.update(evtM);
    } catch (InvalidPhaseException e) {
      assertNotEquals(game.getCurrentPhase(), Phase.Start);
      return;
    } catch (WrongPlayerException ignored) {
    }
    assertEquals(game.getCurrentPlayer().getCurrentWorker().getSex(), Sex.Male);
    assertTrue(game.getCurrentPlayer().isWorkerSelected());

    game.getCurrentPlayer().setWorkerSelected(false);
    WorkerSelectionEvent evtWrongPlayer =
            new WorkerSelectionEvent(game.getPlayers()[1], Sex.Male);
    assertThrows(
            WrongPlayerException.class,
            () -> {
              controller.update(evtWrongPlayer);
            });
    assertFalse(game.getCurrentPlayer().isWorkerSelected());

    game.getCurrentPlayer().setWorkerSelected(false);
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    WorkerSelectionEvent evtNextPhase =
            new WorkerSelectionEvent(game.getCurrentPlayer(), Sex.Female);
    assertThrows(
            InvalidPhaseException.class,
            () -> {
              controller.update(evtNextPhase);
            });
    assertFalse(game.getCurrentPlayer().isWorkerSelected());
  }

  @Test
  @DisplayName("Player moves due to a MovementEvent. Tests sequence of calls")
  void movementTest() {

    Point point = new Point(0, 1);


    game.getCurrentPlayer().setCurrentSex(Sex.Male);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), new Point(0, 0));
    } catch (InvalidPositionException ignored) {
    } catch (BoxFullException ignored) {
    }

    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));

    MovementEvent evt = new MovementEvent(game.getCurrentPlayer(), point);
    try {
      controller.update(evt);
      try {
        assertTrue(
                point.equals(
                        game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker())));
      } catch (ItemNotFoundException ignored) {
      }
    } catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    } catch (InvalidPositionException ignored) {
    } catch (InvalidMoveException ignored) {
    }
    assertFalse(game.getCurrentPlayer().getMovementList().isEmpty());
    game.getCurrentPlayer().getMovementList().clear();

    Point invalidMovePoint = new Point(3, 3);
    MovementEvent evtInvalidMove = new MovementEvent(game.getCurrentPlayer(), invalidMovePoint);

    assertThrows(InvalidMoveException.class, () -> {
      controller.update(evtInvalidMove);
    });

    Block newBlockLevelOne = Block.blocks[0];
    Block newBlockLevelTwo = Block.blocks[1];
    Point buildPoint = new Point(1, 1);
    try {
      game.getBoard().place(newBlockLevelOne, buildPoint);
      game.getBoard().place(newBlockLevelTwo, buildPoint);
    } catch (BoxFullException ignored) {
    } catch (InvalidPositionException ignored) {
    }

    MovementEvent evtInvalidLevel = new MovementEvent(game.getCurrentPlayer(), buildPoint);

    assertThrows(InvalidMoveException.class, () -> {
      controller.update(evtInvalidLevel);
    });
    assertTrue(game.getCurrentPlayer().getMovementList().isEmpty());

    Point negativePoint = new Point(-1, -1);
    MovementEvent evtInvalidPos = new MovementEvent(game.getCurrentPlayer(), negativePoint);

    assertThrows(InvalidPositionException.class, () -> {
      controller.update(evtInvalidPos);
    });
    assertTrue(game.getCurrentPlayer().getMovementList().isEmpty());

    game.setCurrentPhase(Phase.End);
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));

    MovementEvent evtInvalidPlayer = new MovementEvent(game.getPlayers()[0], point);

    assertThrows(WrongPlayerException.class, () -> {
      controller.update(evtInvalidPlayer);
    });
    assertTrue(game.getCurrentPlayer().getMovementList().isEmpty());

    game.setCurrentPhase(Phase.End);
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));


    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    Point newPoint = new Point(0, 2);
    MovementEvent evtInvalidPhase = new MovementEvent(game.getCurrentPlayer(), newPoint);

    assertThrows(InvalidPhaseException.class, () -> {
      controller.update(evtInvalidPhase);
    });
    assertTrue(game.getCurrentPlayer().getMovementList().isEmpty());

  }

  @Test
  @DisplayName("Player builds due to a ConstructionEvent. Tests sequence of calls")
  void constructionTest() {


    game.getCurrentPlayer().setCurrentSex(Sex.Female);

    Point femalePos = new Point(0, 1);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), femalePos);
    } catch (InvalidPositionException ignored) {
    } catch (BoxFullException ignored) {
    }

    game.getCurrentPlayer().setCurrentSex(Sex.Male);

    Point malePos = new Point(0, 0);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), malePos);
    } catch (InvalidPositionException ignored) {
    } catch (BoxFullException ignored) {
    }

    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));

    assertEquals(game.getCurrentPhase(), Phase.Construction);

    Point target = new Point(1, 1);
    int level = 1;

    BuildEvent evt = new BuildEvent(game.getCurrentPlayer(), target, level);

    try {
      controller.update(evt);
    } catch (InvalidPositionException ignored) {
    } catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    } catch (InvalidMoveException ignored) {
    }

    try {
      assertEquals(game.getBoard().getItems(target).peek(), Block.blocks[level - 1]);
    } catch (BoxEmptyException e) {
      System.out.println("Construction has not been performed");
    } catch (InvalidPositionException ignored) {
    }

    assertFalse(game.getCurrentPlayer().getConstructionList().isEmpty());
    game.getCurrentPlayer().getConstructionList().clear();

    target = new Point(1, 0);

    Point notAdjacentPoint = new Point(3, 3);
    BuildEvent evtInvalidMove = new BuildEvent(game.getCurrentPlayer(), notAdjacentPoint, level);
    assertThrows(InvalidMoveException.class, () -> {
      controller.update(evtInvalidMove);
    });
    assertTrue(game.getCurrentPlayer().getConstructionList().isEmpty());

    BuildEvent evtInvalidMove2 = new BuildEvent(game.getCurrentPlayer(), femalePos, level);
    assertThrows(InvalidMoveException.class, () -> {
      controller.update(evtInvalidMove);
    });
    assertTrue(game.getCurrentPlayer().getConstructionList().isEmpty());

    BuildEvent evtInvalidLevel = new BuildEvent(game.getCurrentPlayer(), target, 3);
    assertThrows(InvalidMoveException.class, () -> {
      controller.update(evtInvalidLevel);
    });
    assertTrue(game.getCurrentPlayer().getConstructionList().isEmpty());

    Point negativePoint = new Point(-1, -1);
    BuildEvent evtInvalidPos = new BuildEvent(game.getCurrentPlayer(), negativePoint, level);
    assertThrows(InvalidPositionException.class, () -> {
      controller.update(evtInvalidPos);
    });
    assertTrue(game.getCurrentPlayer().getConstructionList().isEmpty());

    BuildEvent evtInvalidPlayer = new BuildEvent(game.getPlayers()[1], target, level);
    assertThrows(WrongPlayerException.class, () -> {
      controller.update(evtInvalidPlayer);
    });
    assertTrue(game.getCurrentPlayer().getConstructionList().isEmpty());

    game.setCurrentPhase(Phase.End);
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    BuildEvent evtInvalidPhase = new BuildEvent(game.getCurrentPlayer(), target, level);
    assertThrows(InvalidPhaseException.class, () -> {
      controller.update(evtInvalidPhase);
    });
    assertTrue(game.getCurrentPlayer().getConstructionList().isEmpty());
  }

  @Test
  @DisplayName("Tests the undo function that undoes the last performed move.")
  void undoTest() {

    UndoEvent evt = new UndoEvent(game.getCurrentPlayer());
    try {
      controller.update(evt);
    } catch (WrongPlayerException ignored) {
    }

    UndoEvent evtWrongPlayer = new UndoEvent(game.getPlayers()[1]);
    assertThrows(WrongPlayerException.class, () -> {
      controller.update(evtWrongPlayer);
    });
  }

  @Test
  @DisplayName("Tests that a correct SpwanWorkerEvent places the currentWorker in the right place, which is the target parameter")
  void spawnWorkerTest() {
    game.getCurrentPlayer().setCurrentSex(Sex.Male);

    Point target = new Point(0, 0);

    SpawnWorkerEvent evt = new SpawnWorkerEvent(game.getCurrentPlayer(), target);
    try {
      controller.update(evt);
    } catch (WrongPlayerException ignored) {
    } catch (InvalidPositionException ignored) {
    } catch (OverwrittenWorkerException ignored) {
    }

    try {
      assertEquals(game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker()), evt.getTarget());
    } catch (ItemNotFoundException e) {
      e.printStackTrace();
    }

    Point badTarget = new Point(-1, -1);
    SpawnWorkerEvent evtInvalidPos = new SpawnWorkerEvent(game.getCurrentPlayer(), badTarget);
    assertThrows(InvalidPositionException.class, () -> {
      controller.update(evtInvalidPos);
    });

    SpawnWorkerEvent evtWrongPlayer = new SpawnWorkerEvent(game.getPlayers()[1], target);
    assertThrows(WrongPlayerException.class, () -> {
      controller.update(evtWrongPlayer);
    });

    target = new Point(1, 0);
    SpawnWorkerEvent evtWorkerAlreadySpawned = new SpawnWorkerEvent(game.getCurrentPlayer(), target);
    assertThrows(OverwrittenWorkerException.class, () -> {
      controller.update(evtWorkerAlreadySpawned);
    });

    target = new Point(0, 0);
    game.getCurrentPlayer().setCurrentSex(Sex.Female);
    SpawnWorkerEvent evtOverwrittenWorker = new SpawnWorkerEvent(game.getCurrentPlayer(), target);
    assertThrows(OverwrittenWorkerException.class, () -> {
      controller.update(evtOverwrittenWorker);
    });

  }

  @ParameterizedTest
  @MethodSource("provideAllPhases")
  @DisplayName("Tests that for each currentPhase, the nextPhase is correctly computed and updated")
  void skipTest(Phase currentPhase){

    SkipEvent evt = new SkipEvent(game.getCurrentPlayer());
    game.setCurrentPhase(currentPhase);
    try {
      controller.update(evt);
    } catch (WrongPlayerException ignored){}
    catch (UnskippablePhaseException ignored){}

    assertTrue(game.getCurrentPhase().equals(currentPhase.next()));

    if (currentPhase.equals(Phase.End)){
      assertTrue(game.getCurrentPlayer().equals(game.getPlayers()[1]));
    }
  }


}

