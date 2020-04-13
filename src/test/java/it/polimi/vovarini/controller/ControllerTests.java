package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.*;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.BoxFullException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Controller Tests")
public class ControllerTests {

  private static Controller controller;

  @Test
  @DisplayName("Controller Instance")
  void controllerCreation() {
    Game game = null;
    try {
      game = new Game(2);
    } catch (InvalidNumberOfPlayersException ignored) {

    }
    controller = new Controller(game);
    assertEquals(game, controller.getGame());
  }

  @Test
  @DisplayName("Worker Selection due to a WorkerSelectionEvent. Tests associated sequence of calls")
  void workerSelectionTest() {

    try {
      Game game = new Game(2);

      try {
        game.addPlayer("playerOne");
      } catch (InvalidNumberOfPlayersException e) {
        assertTrue(game.getPlayers().length == 2);
        return;
      }
      try {
        game.addPlayer("playerTwo");
      } catch (InvalidNumberOfPlayersException e) {
        assertTrue(game.getPlayers().length == 2);
        return;
      }

      controller = new Controller(game);
      WorkerSelectionEvent evtF =
          new WorkerSelectionEvent(this, game.getCurrentPlayer(), Sex.Female);
      try {
        controller.update(evtF);
      } catch (InvalidPhaseException e) {
        assertNotEquals(game.getCurrentPhase(), Phase.Start);
        return;
      }
      catch (WrongPlayerException ignored){}
      assertEquals(game.getCurrentPlayer().getCurrentWorker().getSex(), Sex.Female);

      WorkerSelectionEvent evtM = new WorkerSelectionEvent(this, game.getCurrentPlayer(), Sex.Male);
      try {
        controller.update(evtM);
      } catch (InvalidPhaseException e) {
        assertNotEquals(game.getCurrentPhase(), Phase.Start);
        return;
      }
      catch (WrongPlayerException ignored){}
      assertEquals(game.getCurrentPlayer().getCurrentWorker().getSex(), Sex.Male);

      WorkerSelectionEvent evtWrongPlayer =
          new WorkerSelectionEvent(this, game.getPlayers()[1], Sex.Male);
      assertThrows(
          WrongPlayerException.class,
          () -> {
            controller.update(evtWrongPlayer);
          });

      game.nextPhase();
      WorkerSelectionEvent evtNextPhase =
          new WorkerSelectionEvent(this, game.getCurrentPlayer(), Sex.Female);
      assertThrows(
          InvalidPhaseException.class,
          () -> {
            controller.update(evtNextPhase);
          });

    } catch (InvalidNumberOfPlayersException ignored) {

    }
  }

  @Test
  @DisplayName("Player moves due to a MovementEvent. Tests sequence of calls")
  void movementTest() {
    try {
      Game game = new Game(2);
      Point point = new Point(0, 1);

      try {
        game.addPlayer("playerOne");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
        return;
      }
      try {
        game.addPlayer("playerTwo");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
        return;
      }

      GodCard cardOne = new GodCard(GodName.Nobody, game);
      GodCard cardTwo = new GodCard(GodName.Nobody, game);

      game.getPlayers()[0].setGodCard(cardOne);
      game.getPlayers()[1].setGodCard(cardTwo);

      game.getCurrentPlayer().setCurrentSex(Sex.Male);
      try {
        game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), new Point(0, 0));
      } catch (InvalidPositionException ignored) {
      } catch (BoxFullException ignored) {
      }
      controller = new Controller(game);

      game.nextPhase();

      MovementEvent evt = new MovementEvent(this, game.getCurrentPlayer(), point);
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
      } catch (CurrentPlayerLosesException ignored){
      }

      Point invalidMovePoint = new Point (3,3);
      MovementEvent evtInvalidMove = new MovementEvent(this, game.getCurrentPlayer(), invalidMovePoint);

      assertThrows(InvalidMoveException.class, ()-> {controller.update(evtInvalidMove);});

      Block newBlockLevelOne = Block.blocks[0];
      Block newBlockLevelTwo = Block.blocks[1];
      Point buildPoint = new Point (1,1);
      try {
        game.getBoard().place(newBlockLevelOne, buildPoint);
        game.getBoard().place(newBlockLevelTwo, buildPoint);
      }
      catch (BoxFullException ignored){}
      catch (InvalidPositionException ignored){}

      MovementEvent evtInvalidLevel = new MovementEvent(this, game.getCurrentPlayer(), buildPoint);

      assertThrows(InvalidMoveException.class, ()->{controller.update(evtInvalidLevel);});

      Point negativePoint = new Point (-1, -1);
      MovementEvent evtInvalidPos = new MovementEvent(this, game.getCurrentPlayer(), negativePoint);

      assertThrows(InvalidPositionException.class, ()->{controller.update(evtInvalidPos);});

      game.nextPlayer();
      MovementEvent evtInvalidPlayer = new MovementEvent(this, game.getPlayers()[0], point);

      assertThrows(WrongPlayerException.class, ()->{controller.update(evtInvalidPlayer);});

      game.nextPlayer();

      game.nextPhase();
      game.nextPhase();
      Point newPoint = new Point(0,2);
      MovementEvent evtInvalidPhase = new MovementEvent(this, game.getCurrentPlayer(), newPoint);

      assertThrows(InvalidPhaseException.class, ()->{controller.update(evtInvalidPhase);});

    } catch (InvalidNumberOfPlayersException ignored) {

    }
  }

  @Test
  @DisplayName("Player builds due to a ConstructionEvent. Tests sequence of calls")
  void constructionTest() {

    Game game = null;
    try {
      game = new Game(2);
    } catch (InvalidNumberOfPlayersException ignored) {
    }

    try {
      game.addPlayer("playerOne");
      game.addPlayer("playerTwo");
    } catch (InvalidNumberOfPlayersException e) {
      assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
      return;
    }

    GodCard cardOne = new GodCard(GodName.Nobody, game);
    GodCard cardTwo = new GodCard(GodName.Nobody, game);

    game.getPlayers()[0].setGodCard(cardOne);
    game.getPlayers()[1].setGodCard(cardTwo);

    game.getCurrentPlayer().setCurrentSex(Sex.Female);

    Point femalePos = new Point(0,1);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), femalePos);
    } catch (InvalidPositionException ignored) {
    } catch (BoxFullException ignored) {
    }

    game.getCurrentPlayer().setCurrentSex(Sex.Male);

    Point malePos = new Point(0,0);
    try {
      game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), malePos);
    } catch (InvalidPositionException ignored) {
    } catch (BoxFullException ignored) {
    }

    controller = new Controller(game);

    // Move in Construction phase
    game.nextPhase();
    game.nextPhase();

    assertEquals(game.getCurrentPhase(), Phase.Construction);

    // Ok, now let's test on Construction

    // Remember: FemaleWorker is on the point (0,1)
    //           MaleWorker (=currentWorker) is on the point (0,0)

    Point target = new Point(1,1);
    int level = 1;

    BuildEvent evt = new BuildEvent(this, game.getCurrentPlayer(), target, level);

    try {
      controller.update(evt);
    } catch (InvalidPositionException ignored) {
    } catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    } catch (InvalidMoveException ignored) {
    }

    try {
      assertEquals(game.getBoard().getItems(target).peek(), Block.blocks[level-1]);
      } catch (BoxEmptyException e) {
      System.out.println("Construction has not been performed");
      } catch (InvalidPositionException ignored) {
    }

    // Now level of (1,1) is 1

    target = new Point(1,0);

    // invalidMove: target not adjacent
    Point notAdjacentPoint = new Point (3,3);
    BuildEvent evtInvalidMove = new BuildEvent(this, game.getCurrentPlayer(), notAdjacentPoint, level);
    assertThrows(InvalidMoveException.class, ()-> {controller.update(evtInvalidMove);});

    // invalidMove: femaleWorker on the top of target (target = femalePosition)
    BuildEvent evtInvalidMove2 = new BuildEvent(this, game.getCurrentPlayer(), femalePos, level);
    assertThrows(InvalidMoveException.class, ()-> {controller.update(evtInvalidMove);});

    // invalidMove: invalidLevel
    BuildEvent evtInvalidLevel = new BuildEvent(this, game.getCurrentPlayer(), target, level+1);
    assertThrows(InvalidMoveException.class, ()->{controller.update(evtInvalidLevel);});

    // invalidPosition: negative target
    Point negativePoint = new Point (-1, -1);
    BuildEvent evtInvalidPos = new BuildEvent(this, game.getCurrentPlayer(), negativePoint, level);
    assertThrows(InvalidPositionException.class, ()->{controller.update(evtInvalidPos);});

    // invalidPlayer: wrong player
    BuildEvent evtInvalidPlayer = new BuildEvent(this, game.getPlayers()[1], target, level);
    assertThrows(WrongPlayerException.class, ()->{controller.update(evtInvalidPlayer);});

    // invalidPhase: Start phase
    game.nextPlayer();
    BuildEvent evtInvalidPhase = new BuildEvent(this, game.getCurrentPlayer(), target, level);
    assertThrows(InvalidPhaseException.class, ()->{controller.update(evtInvalidPhase);});
  }


  @Test
  @DisplayName("Player builds due to a ConstructionEvent. Tests sequence of calls")
    void registrationTest() {

    Game game = null;
    try {
      game = new Game(2);
    } catch (InvalidNumberOfPlayersException ignored) {
    }

    controller = new Controller(game);

    String nickname = "Mengi_97";
    RegistrationEvent evt = new RegistrationEvent(this, null, nickname);

    try {
      controller.update(evt);
    } catch (InvalidNicknameException ignored) {
    } catch (InvalidNumberOfPlayersException ignored) {
    }

    assertEquals(game.getPlayers()[0].getNickname(), nickname);

    InvalidNicknameException e;

    // invalidNickname: null nickname
    nickname = null;
    RegistrationEvent evtNullNickname = new RegistrationEvent(this, null, nickname);
    e = assertThrows(InvalidNicknameException.class, ()-> {controller.update(evtNullNickname);});
    assertEquals(e.getErrorCode(), e.ERROR_INVALID);

    // invalidNickname: length < 4
    nickname = "o_o";
    RegistrationEvent evtInvalidLength = new RegistrationEvent(this, null, nickname);
    e = assertThrows(InvalidNicknameException.class, ()-> {controller.update(evtInvalidLength);});
    assertEquals(e.getErrorCode(), e.ERROR_INVALID);

    // invalidNickname: length > 16
    nickname = "0123456789ABCDEF_ZZZZ";
    RegistrationEvent evtInvalidLength2 = new RegistrationEvent(this, null, nickname);
    e = assertThrows(InvalidNicknameException.class, ()-> {controller.update(evtInvalidLength2);});
    assertEquals(e.getErrorCode(), e.ERROR_INVALID);

    // invalidNickname: special character
    nickname = "Mengi-97";
    RegistrationEvent evtInvalidNickname = new RegistrationEvent(this, null, nickname);
    e = assertThrows(InvalidNicknameException.class, ()-> {controller.update(evtInvalidNickname);});
    assertEquals(e.getErrorCode(), e.ERROR_INVALID);

    // invalidNickname: blank character
    nickname = "Mengi 97";
    RegistrationEvent evtInvalidNickname2 = new RegistrationEvent(this, null, nickname);
    e = assertThrows(InvalidNicknameException.class, ()-> {controller.update(evtInvalidNickname2);});
    assertEquals(e.getErrorCode(), e.ERROR_INVALID);

    // invalidNickname: duplicate
    nickname = "mEnGi_97";
    RegistrationEvent evtDuplicateNickname = new RegistrationEvent(this, null, nickname);
    e = assertThrows(InvalidNicknameException.class, ()-> {controller.update(evtDuplicateNickname);});
    assertEquals(e.getErrorCode(), e.ERROR_DUPLICATE);

    nickname = "Valas511";
    evt = new RegistrationEvent(this, null, nickname);

    try {
      controller.update(evt);
    } catch (InvalidNicknameException ignored) {
    } catch (InvalidNumberOfPlayersException ignored) {
    }

    assertEquals(game.getPlayers()[1].getNickname(), nickname);

    // There is no place for you in this Game
    nickname = "xXBEN00BXx";
    RegistrationEvent evtInvalidNumberOfPlayers = new RegistrationEvent(this, null, nickname);

    assertThrows(InvalidNumberOfPlayersException.class, ()-> {controller.update(evtInvalidNumberOfPlayers);});

    assertEquals(game.getPlayers()[0].getNickname(), "Mengi_97");
    assertEquals(game.getPlayers()[1].getNickname(), "Valas511");
    assertEquals(game.getPlayers().length, 2);
  }

  @Test
  @DisplayName("To change current player")
  void nextPlayerTest() {

    Game game = null;
    try {
      game = new Game(3);
    } catch (InvalidNumberOfPlayersException ignored) {
    }

    try {
      game.addPlayer("playerOne");
      game.addPlayer("playerTwo");
      game.addPlayer("playerThree");
    } catch (InvalidNumberOfPlayersException e) {
      assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
      return;
    }

    controller = new Controller(game);

    game.nextPhase();
    game.nextPhase();
    game.nextPhase();

    assertEquals(game.getCurrentPhase(), Phase.End);

    Player[] players = game.getPlayers();

    NextPlayerEvent evt = new NextPlayerEvent(this, players[0]);

    try {
      controller.update(evt);
    } catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    }

    assertEquals(game.getCurrentPlayer(), players[1]);
    assertEquals(game.getCurrentPhase(), Phase.Start);

    // invalidPhase: Start phase
    NextPlayerEvent evtInvalidPhase = new NextPlayerEvent(this, players[1]);
    assertThrows(InvalidPhaseException.class, ()-> {controller.update(evtInvalidPhase);});

    game.nextPhase();
    game.nextPhase();
    game.nextPhase();

    // invalidPlayer: wrong player
    NextPlayerEvent evtInvalidPlayer = new NextPlayerEvent(this, players[2]);
    assertThrows(WrongPlayerException.class, ()->{controller.update(evtInvalidPlayer);});
  }

  @Test
  @DisplayName("To change current phase")
  void skipTest() {
    Game game = null;
    try {
      game = new Game(3);
    } catch (InvalidNumberOfPlayersException ignored) {
    }

    try {
      game.addPlayer("playerOne");
      game.addPlayer("playerTwo");
      game.addPlayer("playerThree");
    } catch (InvalidNumberOfPlayersException e) {
      assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
      return;
    }

    controller = new Controller(game);

    SkipEvent evt = new SkipEvent(this, game.getPlayers()[0]);

    try {
      controller.update(evt);
    } catch (InvalidPhaseException ignored) {
    } catch (WrongPlayerException ignored) {
    }

    assertEquals(game.getCurrentPlayer(), game.getPlayers()[0]);
    assertEquals(game.getCurrentPhase(), Phase.Movement);

    game.nextPhase();
    game.nextPhase();

    // invalidPhase: End phase
    SkipEvent evtInvalidPhase = new SkipEvent(this, game.getCurrentPlayer());
    assertThrows(InvalidPhaseException.class, ()-> {controller.update(evtInvalidPhase);});

    game.nextPlayer();
    game.nextPlayer();

    // invalidPlayer: wrong player
    NextPlayerEvent evtInvalidPlayer = new NextPlayerEvent(this, game.getPlayers()[0]);
    assertThrows(WrongPlayerException.class, ()->{controller.update(evtInvalidPlayer);});
  }

  @Test
  @DisplayName("Tests the undo function that undoes the last performed move.")
  void undoTest(){

    try {
      Game game = new Game(2);

      try {
        game.addPlayer("playerOne");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
        return;
      }
      try {
        game.addPlayer("playerTwo");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
        return;
      }

      controller = new Controller(game);
      UndoEvent evt = new UndoEvent(this, game.getCurrentPlayer());
      try{
        controller.update(evt);
      }
      catch (WrongPlayerException ignored){}

      UndoEvent evtWrongPlayer = new UndoEvent(this, game.getPlayers()[1]);
      assertThrows(WrongPlayerException.class, ()->{ controller.update(evtWrongPlayer); });
    }
    catch (InvalidNumberOfPlayersException ignored){}

  }

  @Test
  @DisplayName("Tests that a correct SpwanWorkerEvent places the currentWorker in the right place, which is the target parameter")
  void SpawnWorkerTest(){

    try{
      Game game = new Game(2);

      try {
        game.addPlayer("playerOne");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
        return;
      }
      try {
        game.addPlayer("playerTwo");
      } catch (InvalidNumberOfPlayersException e) {
        assertNotNull(game.getPlayers()[game.getPlayers().length-1]);
        return;
      }

      controller = new Controller(game);


      Point target = new Point (0,0);

      SpawnWorkerEvent evt = new SpawnWorkerEvent(this, game.getCurrentPlayer(), target);
      try{
        controller.update(evt);
      }
      catch (WrongPlayerException ignored){}
      catch (InvalidPositionException ignored){}

      try {
        assertEquals(game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker()), evt.getTarget());
      }
      catch (ItemNotFoundException e){
        e.printStackTrace();
      }

      Point badTarget = new Point (-1, -1);
      SpawnWorkerEvent evtInvalidPos = new SpawnWorkerEvent(this, game.getCurrentPlayer(), badTarget);
      assertThrows(InvalidPositionException.class, ()->{ controller.update(evtInvalidPos); });

      SpawnWorkerEvent evtWrongPlayer = new SpawnWorkerEvent(this, game.getPlayers()[1], target);
      assertThrows(WrongPlayerException.class, ()->{ controller.update(evtWrongPlayer); });
    }



    catch (InvalidNumberOfPlayersException ignored){}

  }
}
