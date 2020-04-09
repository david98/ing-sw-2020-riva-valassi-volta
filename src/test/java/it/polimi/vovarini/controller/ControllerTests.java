package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.MovementEvent;
import it.polimi.vovarini.controller.events.WorkerSelectionEvent;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.BoxFullException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;
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
        game.addPlayer("playerOne", 2);
      } catch (InvalidNumberOfPlayersException e) {
        assertTrue(game.getPlayers().length == 2);
        return;
      }
      try {
        game.addPlayer("playerTwo", 2);
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
      assertEquals(game.getCurrentPlayer().getCurrentWorker().getSex(), Sex.Female);

      WorkerSelectionEvent evtM = new WorkerSelectionEvent(this, game.getCurrentPlayer(), Sex.Male);
      try {
        controller.update(evtM);
      } catch (InvalidPhaseException e) {
        assertNotEquals(game.getCurrentPhase(), Phase.Start);
        return;
      }
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
  void MovementTest() {
    try {
      Game game = new Game(2);
      Point point = new Point (0, 1);

      try {
        game.addPlayer("playerOne", 2);
      } catch (InvalidNumberOfPlayersException e) {
        assertTrue(game.getPlayers().length == 2);
        return;
      }
      try {
        game.addPlayer("playerTwo", 2);
      } catch (InvalidNumberOfPlayersException e) {
        assertTrue(game.getPlayers().length == 2);
        return;
      }

      GodCard cardOne = new GodCard(GodName.Nobody, game);
      GodCard cardTwo = new GodCard(GodName.Nobody, game);

      game.getPlayers()[0].setGodCard(cardOne);
      game.getPlayers()[1].setGodCard(cardTwo);

      game.getCurrentPlayer().setCurrentSex(Sex.Male);
      try {
        game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), new Point(0, 0));
      }
      catch (InvalidPositionException ignored){}
      catch (BoxFullException ignored){}
      controller = new Controller(game);

      game.nextPhase();

      MovementEvent evt = new MovementEvent(this, game.getCurrentPlayer(), point);
      try{
        controller.update(evt);
        try {
          assertTrue(point.equals(game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker())));
        }
        catch (ItemNotFoundException ignored) {}
      }
      catch (InvalidPhaseException ignored){}
      catch (WrongPlayerException ignored){}
      catch (InvalidPositionException ignored){}
      catch (InvalidMoveException ignored){}


    }
    catch (InvalidNumberOfPlayersException ignored){

    }
  }
}
