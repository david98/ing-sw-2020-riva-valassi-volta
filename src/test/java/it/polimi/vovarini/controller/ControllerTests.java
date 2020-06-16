package it.polimi.vovarini.controller;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Controller Tests")
public class ControllerTests {

  private static Controller controller;
  private static Game game;

  private GameEvent lastEvent;

  private static List<Phase> provideAllPhases(){
    return Arrays.asList(Phase.values());
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
    lastEvent = null;
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

      GodCard cardOne = GodCardFactory.create(GodName.Nobody);
      cardOne.setGameData(game);
      GodCard cardTwo = GodCardFactory.create(GodName.Nobody);
      cardTwo.setGameData(game);

      game.getPlayers()[0].setGodCard(cardOne);
      game.getPlayers()[1].setGodCard(cardTwo);

      controller = new Controller(game);

    } catch (InvalidNumberOfPlayersException ignored) {
    }
  }

  @Test
  @DisplayName("Test card selection part")
  void cardChoiceTest() {

    var evtCardsNotSelected = new CardChoiceEvent(game.getCurrentPlayer(), GodName.Artemis);
    assertThrows(CardsNotSelectedException.class, () -> { controller.update(evtCardsNotSelected); });

    game.drawElectedPlayer();

    Player electedPlayer = game.getCurrentPlayer();
    Player otherPlayer;

    if(game.getPlayers()[0].equals(electedPlayer))
      otherPlayer = game.getPlayers()[1];
    else
      otherPlayer = game.getPlayers()[0];

    assertEquals(electedPlayer, game.getCurrentPlayer());

    GodName[] selectedGods = new GodName[]{GodName.Artemis, GodName.Hephaestus};
    var evtWrongPlayer = new AvailableCardsEvent(otherPlayer, selectedGods);
    assertThrows(WrongPlayerException.class, () -> { controller.update(evtWrongPlayer); });

    selectedGods = new GodName[]{GodName.Artemis};
    AvailableCardsEvent evtInvalidNumberOfGodCards = new AvailableCardsEvent(electedPlayer, selectedGods);
    assertThrows(InvalidNumberOfGodCardsException.class, () -> { controller.update(evtInvalidNumberOfGodCards); });

    selectedGods = new GodName[]{GodName.Artemis, null};
    AvailableCardsEvent evtInvalidGodCard = new AvailableCardsEvent(electedPlayer, selectedGods);
    assertThrows(InvalidCardException.class, () -> { controller.update(evtInvalidGodCard); });

    selectedGods = new GodName[]{GodName.Artemis, GodName.Artemis};
    AvailableCardsEvent evtDuplicateGodCard = new AvailableCardsEvent(electedPlayer, selectedGods);
    assertThrows(InvalidCardException.class, () -> { controller.update(evtDuplicateGodCard); });

    selectedGods = new GodName[]{GodName.Artemis, GodName.Hephaestus};
    AvailableCardsEvent evt = new AvailableCardsEvent(electedPlayer, selectedGods);

    try {
      controller.update(evt);
    } catch (WrongPlayerException e) {
      e.printStackTrace();
    } catch (InvalidCardException e) {
      e.printStackTrace();
    } catch (InvalidNumberOfGodCardsException e) {
      e.printStackTrace();
    } catch (AvailableCardsAlreadySetException e) {
      e.printStackTrace();
    }

    assertTrue(game.isAvailableCardsAlreadySet());
    assertEquals(selectedGods, game.getAvailableGodCards());
    assertEquals(otherPlayer, game.getCurrentPlayer());

    assertThrows(AvailableCardsAlreadySetException.class, () -> { controller.update(evt); });


    // scelta della propria carta
    GodName choice = GodName.Artemis;
    CardChoiceEvent evtInvalidPlayer = new CardChoiceEvent(electedPlayer, choice);
    assertThrows(WrongPlayerException.class, () -> { controller.update(evtInvalidPlayer); });

    choice = GodName.Minotaur;
    CardChoiceEvent evtInvalidCard = new CardChoiceEvent(otherPlayer, choice);
    assertThrows(InvalidCardException.class, () -> { controller.update(evtInvalidCard); });

    choice = GodName.Artemis;
    CardChoiceEvent evtValid = new CardChoiceEvent(otherPlayer, choice);

    try {
      controller.update(evtValid);
    } catch (CardsNotSelectedException e) {
      e.printStackTrace();
    } catch (InvalidCardException e) {
      e.printStackTrace();
    } catch (WrongPlayerException e) {
      e.printStackTrace();
    }

    assertTrue(game.isAvailableCardsAlreadySet());
    assertEquals(0, game.getAvailableGodCards().length);
    assertEquals(choice, otherPlayer.getGodCard().getName());
    assertEquals(GodName.Hephaestus, electedPlayer.getGodCard().getName());
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
  @Disabled
  /*
   * This test is waiting for a fix due to recent changes in Game.
   */
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
    game.setCurrentPhase(Phase.Movement);

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
  @Disabled
  /*
   * This test is waiting for a fix due to recent changes in Game.
   */
  @DisplayName("Player builds due to a ConstructionEvent. Tests sequence of calls")
  void constructionTest() {
    game.getCurrentPlayer().setCurrentSex(Sex.Female);

    Point femalePos = new Point(0, 1);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), femalePos);
    } catch (BoxFullException ignored) {
    }

    game.getCurrentPlayer().setCurrentSex(Sex.Male);

    Point malePos = new Point(0, 0);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), malePos);
    } catch (BoxFullException ignored) {
    }

    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));

    assertEquals(Phase.Construction, game.getCurrentPhase());

    Point target = new Point(1, 1);
    int level = 1;

    BuildEvent evt = new BuildEvent(game.getCurrentPlayer(), target, level);

    try {
      controller.update(evt);
    } catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    } catch (InvalidMoveException ignored) {
    }


    assertEquals(game.getBoard().getItems(target).peek(), Block.blocks[level - 1]);


    assertFalse(game.getCurrentPlayer().getConstructionList().isEmpty());
    game.getCurrentPlayer().getConstructionList().clear();
    game.setCurrentPhase(Phase.Construction);

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
    } catch (WrongPlayerException e) {
      e.printStackTrace();
    } catch (InvalidPositionException e) {
      e.printStackTrace();
    } catch (OverwrittenWorkerException e) {
      e.printStackTrace();
    }

    game.getCurrentPlayer().setCurrentSex(Sex.Female);

    Point target_two = new Point(3,3);
    SpawnWorkerEvent evt_second = new SpawnWorkerEvent(game.getCurrentPlayer(), target_two);
    try{
      controller.update(evt_second);
    }
    catch (WrongPlayerException e) {
      e.printStackTrace();
    } catch (InvalidPositionException e) {
      e.printStackTrace();
    } catch (OverwrittenWorkerException e) {
      e.printStackTrace();
    }

    try {
      game.nextPlayer();
      game.getCurrentPlayer().setCurrentSex(Sex.Male);
      assertEquals(game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker()), evt.getTarget());
      game.getCurrentPlayer().setCurrentSex(Sex.Female);
      assertEquals(game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker()), evt_second.getTarget());
    } catch (ItemNotFoundException e) {
      e.printStackTrace();
    }

    game.getCurrentPlayer().setCurrentSex(Sex.Male);

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
    assertThrows(WorkerAlreadySpawnedException.class, () -> {
      controller.update(evtWorkerAlreadySpawned);
    });

    game.nextPlayer();

    target = new Point(0, 0);
    game.getCurrentPlayer().setCurrentSex(Sex.Female);
    SpawnWorkerEvent evtOverwrittenWorker = new SpawnWorkerEvent(game.getCurrentPlayer(), target);
    assertThrows(OverwrittenWorkerException.class, () -> {
      controller.update(evtOverwrittenWorker);
    });

    assertEquals(game.getCurrentPlayer(), game.getPlayers()[1]);

    game.getCurrentPlayer().setCurrentSex(Sex.Male);

    Point player_two_target = new Point(1, 1);

    SpawnWorkerEvent p2_evt = new SpawnWorkerEvent(game.getCurrentPlayer(), player_two_target);
    try {
      controller.update(p2_evt);
    } catch (WrongPlayerException e) {
      e.printStackTrace();
    } catch (InvalidPositionException e) {
      e.printStackTrace();
    } catch (OverwrittenWorkerException e) {
      e.printStackTrace();
    }

    game.getCurrentPlayer().setCurrentSex(Sex.Female);

    Point player_two_target_two = new Point(4,4);
    SpawnWorkerEvent p2_evt_second = new SpawnWorkerEvent(game.getCurrentPlayer(), player_two_target_two);
    try{
      controller.update(p2_evt_second);
    }
    catch (WrongPlayerException e) {
      e.printStackTrace();
    } catch (InvalidPositionException e) {
      e.printStackTrace();
    } catch (OverwrittenWorkerException e) {
      e.printStackTrace();
    }

    try {
      game.nextPlayer();
      game.getCurrentPlayer().setCurrentSex(Sex.Male);
      assertEquals(game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker()), p2_evt.getTarget());
      game.getCurrentPlayer().setCurrentSex(Sex.Female);
      assertEquals(game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker()), p2_evt_second.getTarget());
    } catch (ItemNotFoundException e) {
      e.printStackTrace();
    }

    assertTrue(game.isSetupComplete());
  }

  @Test
  @Disabled
  /*
   * This test is waiting for a fix due to recent changes in Game.
   */
  @DisplayName("Tests that the game skips the Start phase only if a worker has been selected")
  void skipStartTest(){
    game.setCurrentPhase(Phase.Start);

    WorkerSelectionEvent wse_evt = new WorkerSelectionEvent(game.getCurrentPlayer(), Sex.Female);
    try{
      controller.update(wse_evt);
    }catch (InvalidPhaseException ignored){}
    catch (WrongPlayerException ignored){}

    SkipEvent evt = new SkipEvent(game.getCurrentPlayer());
    try{
      controller.update(evt);
    }catch (WrongPlayerException ignored){}
    catch (UnskippablePhaseException ignored){}

    assertTrue(game.getCurrentPhase().equals(Phase.Movement));
    game.getCurrentPlayer().setWorkerSelected(false);

    game.setCurrentPhase(Phase.Start);
    SkipEvent invalidStartEvt = new SkipEvent(game.getCurrentPlayer());
    assertThrows(UnskippablePhaseException.class, () ->
      controller.update(invalidStartEvt));



  }

  @Test
  @DisplayName("Tests that the game skips the Movement phase only if a movement has been performed")
  void skipMovementTest(){
    game.setCurrentPhase(Phase.Start);
    Point point = new Point(0, 1);


    game.getCurrentPlayer().setCurrentSex(Sex.Male);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), new Point(0, 0));
    } catch (InvalidPositionException ignored) {
    } catch (BoxFullException ignored) {
    }

    game.setCurrentPhase(Phase.Movement);
    MovementEvent mv_evt = new MovementEvent(game.getCurrentPlayer(), point);
    try{
      controller.update(mv_evt);
    }catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    } catch (InvalidPositionException ignored) {
    } catch (InvalidMoveException ignored) {
    }

    SkipEvent evt = new SkipEvent(game.getCurrentPlayer());
    try{
      controller.update(evt);
    }catch (WrongPlayerException ignored){}
    catch (UnskippablePhaseException ignored){}

    assertTrue(game.getCurrentPhase().equals(Phase.Construction));
    game.getCurrentPlayer().getMovementList().clear();

    game.setCurrentPhase(Phase.Movement);
    SkipEvent evtMovementListEmpty = new SkipEvent(game.getCurrentPlayer());
    assertThrows(UnskippablePhaseException.class, () -> {
      controller.update(evtMovementListEmpty);
    });




  }

  @Test
  @DisplayName("Tests that the game skips the Construction phase only if a construction has been performed")
  void skipConstructionTest(){
    game.setCurrentPhase(Phase.Start);

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

    BuildEvent bd_evt = new BuildEvent(game.getCurrentPlayer(), target, level);

    try {
      controller.update(bd_evt);
    } catch (InvalidPositionException ignored) {
    } catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    } catch (InvalidMoveException ignored) {
    }

    game.setCurrentPhase(Phase.Construction);

    SkipEvent evt = new SkipEvent(game.getCurrentPlayer());
    try{
      controller.update(evt);
    }catch (WrongPlayerException ignored){}
    catch (UnskippablePhaseException ignored){}

    assertTrue(game.getCurrentPhase().equals(Phase.End));
    game.getCurrentPlayer().getConstructionList().clear();

    game.setCurrentPhase(Phase.Construction);
    SkipEvent evtConstructionNotPerformed = new SkipEvent(game.getCurrentPlayer());

    assertThrows(UnskippablePhaseException.class, () -> {
      controller.update(evtConstructionNotPerformed);
    });


  }

  @ParameterizedTest
  @MethodSource("provideAllPhases")
  @DisplayName("Tests that for every phase, if another player tries to skip the controller will always throw a WrongPlayerException")
  void skipWrongPlayerTest(Phase current){
    SkipEvent evt = new SkipEvent(game.getPlayers()[1]);

    assertThrows(WrongPlayerException.class, () -> {
      controller.update(evt);
    });
  }

  @GameEventListener
  public void handleVictory(VictoryEvent e){
    lastEvent = e;
  }

  @GameEventListener
  public void handleLoss(LossEvent e){
    lastEvent = e;
  }

  @Test
  @DisplayName("Tests that a Victory is thrown when only one player remains")
  void victoryDueToLoss() {
    Board board = game.getBoard();
    GameEventManager.bindListeners(this);

    board.place(Block.blocks[0], new Point(1, 0));
    board.place(Block.blocks[1], new Point(1, 0));
    board.place(Block.blocks[2], new Point(1, 0));
    board.place(Block.blocks[0], new Point(1, 1));
    board.place(Block.blocks[1], new Point(1, 1));
    board.place(Block.blocks[0], new Point(0, 1));

    board.place(Block.blocks[0], new Point(3, 0));
    board.place(Block.blocks[1], new Point(3, 0));
    board.place(Block.blocks[2], new Point(3, 0));

    board.place(Block.blocks[0], new Point(3, 1));
    board.place(Block.blocks[1], new Point(3, 1));
    board.place(Block.blocks[2], new Point(3, 1));
    board.place(Block.blocks[0], new Point(4, 1));
    board.place(Block.blocks[1], new Point(4, 1));
    board.place(Block.blocks[0], new Point(4, 2));
    board.place(Block.blocks[0], new Point(4, 3));
    board.place(Block.blocks[1], new Point(4, 3));

    board.place(Block.blocks[0], new Point(3, 3));
    board.place(Block.blocks[1], new Point(3, 3));
    board.place(Block.blocks[2], new Point(3, 3));

    board.place(Block.blocks[0], new Point(3, 4));
    board.place(Block.blocks[1], new Point(3, 4));
    board.place(Block.blocks[2], new Point(3, 4));

    board.place(Block.blocks[0], new Point(1, 3));
    board.place(Block.blocks[1], new Point(1, 3));
    board.place(Block.blocks[2], new Point(1, 3));

    board.place(Block.blocks[0], new Point(1, 4));
    board.place(Block.blocks[1], new Point(1, 4));
    board.place(Block.blocks[2], new Point(1, 4));

    board.place(game.getCurrentPlayer().getWorkers().get(Sex.Male), new Point(4, 0));
    board.place(game.getCurrentPlayer().getWorkers().get(Sex.Female), new Point(4, 4));

    game.setCurrentPhase(Phase.Start);

    assertTrue(lastEvent instanceof VictoryEvent);
    VictoryEvent e = (VictoryEvent) lastEvent;
    assertEquals(game.getCurrentPlayer(), e.getWinningPlayer());
  }

  @Test
  @DisplayName("Tests that a LossEvent is thrown when the first player in a 3 players" +
          " match loses due to no movement possible")
  void lossDueToNoMovement() {
    game.setPlayers(new Player[]{new Player("playerOne"),
            new Player("playerTwo"),
            new Player("playerThree")});
    for (Player p: game.getPlayers()) {
      p.setGodCard(GodCardFactory.create(GodName.Nobody));
      p.getGodCard().setGameData(game);
    }
    Board board = game.getBoard();
    GameEventManager.bindListeners(this);

    board.place(Block.blocks[0], new Point(1, 0));
    board.place(Block.blocks[1], new Point(1, 0));
    board.place(Block.blocks[2], new Point(1, 0));
    board.place(Block.blocks[0], new Point(1, 1));
    board.place(Block.blocks[1], new Point(1, 1));
    board.place(Block.blocks[0], new Point(0, 1));

    board.place(Block.blocks[0], new Point(3, 0));
    board.place(Block.blocks[1], new Point(3, 0));
    board.place(Block.blocks[2], new Point(3, 0));

    board.place(Block.blocks[0], new Point(3, 1));
    board.place(Block.blocks[1], new Point(3, 1));
    board.place(Block.blocks[2], new Point(3, 1));
    board.place(Block.blocks[0], new Point(4, 1));
    board.place(Block.blocks[1], new Point(4, 1));
    board.place(Block.blocks[0], new Point(4, 2));
    board.place(Block.blocks[0], new Point(4, 3));
    board.place(Block.blocks[1], new Point(4, 3));

    board.place(Block.blocks[0], new Point(3, 3));
    board.place(Block.blocks[1], new Point(3, 3));
    board.place(Block.blocks[2], new Point(3, 3));

    board.place(Block.blocks[0], new Point(3, 4));
    board.place(Block.blocks[1], new Point(3, 4));
    board.place(Block.blocks[2], new Point(3, 4));

    board.place(Block.blocks[0], new Point(1, 3));
    board.place(Block.blocks[1], new Point(1, 3));
    board.place(Block.blocks[2], new Point(1, 3));

    board.place(Block.blocks[0], new Point(1, 4));
    board.place(Block.blocks[1], new Point(1, 4));
    board.place(Block.blocks[2], new Point(1, 4));

    board.place(game.getCurrentPlayer().getWorkers().get(Sex.Male), new Point(4, 0));
    board.place(game.getCurrentPlayer().getWorkers().get(Sex.Female), new Point(4, 4));

    game.setCurrentPhase(Phase.Start);

    assertTrue(lastEvent instanceof LossEvent);
    LossEvent e = (LossEvent) lastEvent;
    Arrays.stream(game.getPlayers()).forEach(p -> assertNotEquals(e.getLosingPlayer(), p));
  }

  @Test
  @DisplayName("Tests that a LossEvent is thrown when the first player in a 3 players" +
          " match loses due to no construction possible")
  void lossDueToNoConstruction() {
    game.setPlayers(new Player[]{new Player("playerOne"),
            new Player("playerTwo"),
            new Player("playerThree")});
    for (Player p: game.getPlayers()) {
      p.setGodCard(GodCardFactory.create(GodName.Nobody));
      p.getGodCard().setGameData(game);
    }
    Board board = game.getBoard();
    GameEventManager.bindListeners(this);

    board.place(Block.blocks[0], new Point(1, 0));
    board.place(Block.blocks[1], new Point(1, 0));
    board.place(Block.blocks[2], new Point(1, 0));
    board.place(Block.blocks[0], new Point(1, 1));
    board.place(Block.blocks[1], new Point(1, 1));
    board.place(Block.blocks[0], new Point(0, 1));

    board.place(Block.blocks[0], new Point(3, 0));
    board.place(Block.blocks[1], new Point(3, 0));
    board.place(Block.blocks[2], new Point(3, 0));
    board.place(Block.blocks[3], new Point(3, 0));

    board.place(Block.blocks[0], new Point(3, 1));
    board.place(Block.blocks[1], new Point(3, 1));
    board.place(Block.blocks[2], new Point(3, 1));
    board.place(Block.blocks[3], new Point(3, 1));
    board.place(Block.blocks[0], new Point(4, 1));
    board.place(Block.blocks[1], new Point(4, 1));
    board.place(Block.blocks[2], new Point(4, 1));
    board.place(Block.blocks[3], new Point(4, 1));
    board.place(Block.blocks[0], new Point(4, 2));
    board.place(Block.blocks[0], new Point(4, 3));
    board.place(Block.blocks[1], new Point(4, 3));

    board.place(Block.blocks[0], new Point(3, 3));
    board.place(Block.blocks[1], new Point(3, 3));
    board.place(Block.blocks[2], new Point(3, 3));

    board.place(Block.blocks[0], new Point(3, 4));
    board.place(Block.blocks[1], new Point(3, 4));
    board.place(Block.blocks[2], new Point(3, 4));

    board.place(Block.blocks[0], new Point(1, 3));
    board.place(Block.blocks[1], new Point(1, 3));
    board.place(Block.blocks[2], new Point(1, 3));

    board.place(Block.blocks[0], new Point(1, 4));
    board.place(Block.blocks[1], new Point(1, 4));
    board.place(Block.blocks[2], new Point(1, 4));

    board.place(game.getCurrentPlayer().getWorkers().get(Sex.Male), new Point(4, 0));
    board.place(game.getCurrentPlayer().getWorkers().get(Sex.Female), new Point(4, 4));

    game.setCurrentPhase(Phase.Construction);

    assertTrue(lastEvent instanceof LossEvent);
    LossEvent e = (LossEvent) lastEvent;
    Arrays.stream(game.getPlayers()).forEach(p -> assertNotEquals(e.getLosingPlayer(), p));
  }

}

