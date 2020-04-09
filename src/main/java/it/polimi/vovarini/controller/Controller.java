package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.BuildEvent;
import it.polimi.vovarini.controller.events.CardAssignmentEvent;
import it.polimi.vovarini.controller.events.CardChoiceEvent;
import it.polimi.vovarini.controller.events.WorkerSelectionEvent;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.controller.events.MovementEvent;
import it.polimi.vovarini.controller.events.NextPlayerEvent;
import it.polimi.vovarini.controller.events.RegistrationEvent;
import it.polimi.vovarini.controller.events.UndoEvent;

import java.util.*;

public class Controller implements EventListener {

  private ArrayList<GodName> selectedCards;
  private final Game game;

  public Game getGame() {
    return game;
  }

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    this.game = game;
    selectedCards = new ArrayList<GodName>();
  }

  // CLI: keyboard M,F characters
  public void update(WorkerSelectionEvent evt) throws InvalidPhaseException, WrongPlayerException {

    if (!game.getCurrentPlayer().equals(evt.getPlayerSource())) {
      throw new WrongPlayerException();
    }

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Start)) throw new InvalidPhaseException();

    game.getCurrentPlayer().setCurrentSex(evt.getSex());
    game.nextPhase();
  }

  // CLI: Coordinates input or keyboard arrows
  public void update(BuildEvent evt)
      throws InvalidPositionException, InvalidPhaseException, NonBuildablePositionException,
          WrongPlayerException, InvalidMoveException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Construction)) throw new InvalidPhaseException();

    Point input = new Point(evt.getBuildEnd());
    if (!game.getBoard().isPositionValid(input)) throw new InvalidPositionException();

    Construction build =
        new Construction(
            game.getBoard(), Block.blocks[evt.getLevel() - 1], evt.getBuildEnd(), false);
    if (!game.validateMove(build)) {
      throw new InvalidMoveException();
    }

    game.performMove(build);

    /*boolean pointFound = false;
    Block blockToBuild = null;
    int levelToBuild = 0;
    try {
      for (Point point : game.getCurrentPlayer().getGodCard().computeBuildablePoints()) {
        if (point.equals(input)) {
          pointFound = true;
          try {
            Stack<Item> currentStack = game.getBoard().getBox(point).getItems();
            if (currentStack.peek().canBeRemoved()) currentStack.pop();
            levelToBuild = currentStack.size() + 1;
          } catch (BoxEmptyException e) {
            levelToBuild = 1;
          } finally {
            try {
              blockToBuild = new Block(levelToBuild);
            } catch (InvalidLevelException ignored) {
            }
          }
          Move newMove = new Construction(game.getBoard(), blockToBuild, point);
          game.performMove(newMove);
          break;
        }
      }
    } catch (CurrentPlayerLosesException e) {
      // method required in model or RemoteView to signal the player which triggered the exception
    }

    if (!pointFound) {
      throw new NonBuildablePositionException();
    }*/
  }

  // Not part of the 1vs1 simulation we want to develop now
  public void update(CardChoiceEvent evt) {

    for (GodName card : evt.getSelectedCards()) {
      this.selectedCards.add(card);
    }
  }

  // Not part of the 1vs1 simulation we want to develop now
  public void update(CardAssignmentEvent evt)
      throws CardsNotSelectedException, InvalidCardException {
    if (this.selectedCards.isEmpty()) {
      throw new CardsNotSelectedException();
    } else {
      if (!this.selectedCards.contains(evt.getAssignedCard())) {
        throw new InvalidCardException();
      }

      // GodCard playerCard = new GodCard(game, evt.getAssignedCard()); (new card instance)
      // game.getCurrentPlayer().setGodCard(playerCard);
    }
  }

  // CLI: String input at the start of the game. Any string longer than one should be considered a
  // nickname input
  public void update(RegistrationEvent evt) throws InvalidNicknameException {

    for (Player player : game.getPlayers()) {
      if (player.getNickname().equalsIgnoreCase(evt.getNickname())) {
        throw new InvalidNicknameException(InvalidNicknameException.ERROR_DUPLICATE);
      }

      if (!Player.validateNickname(evt.getNickname())) {
        throw new InvalidNicknameException(InvalidNicknameException.ERROR_INVALID);
      }

      /*Va controllata lunghezza/serie di spazi/caratteri speciali?
      if (evt.getNickname() == null) {
        // throw new NicknameNullException();
      }*/

      // Questa perché è commentata? Sicuramente questo controllo va fatto

      // Questo pezzo di codice andrà dentro addPlayer(String nickname) nel Model (players
      // instanziati
      // dopo inserimento del nickname)
      /*for (Player player : game.getPlayers()) {
        if (player.getNickname().equalsIgnoreCase(evt.getNickname())) {
          throw new NicknameAlreadyInUseException();
        }
      }*/
    }
  }

  // CLI: Ctrl+Z pressed by the user
  public void update(UndoEvent evt) throws WrongPlayerException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    game.undoLastMove();
  }

  // CLI: we should find something to press that will make the "skip button" function, to trigger
  // the next turn (like pressing space)
  public void update(NextPlayerEvent evt) throws InvalidPhaseException, WrongPlayerException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.End)) throw new InvalidPhaseException();

    game.nextPlayer();
  }

  // Gestisce lo skip dell'utente tra una fase e l'altra
  /*public void update(NextPhaseEvent evt) throws WrongPlayerException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    game.nextPhase();
  }*/

  // CLI: Coordinates input or keyboard arrows
  public void update(MovementEvent evt)
          throws InvalidPhaseException, WrongPlayerException, InvalidPositionException,
          InvalidMoveException, CurrentPlayerLosesException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Movement)) throw new InvalidPhaseException();

    Point end = evt.getPoint();
    if (!game.getBoard().isPositionValid(end)) throw new InvalidPositionException();

    Point start = null;
    try {
      start = game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker());
    } catch (ItemNotFoundException ignored) {

    }

    Movement movement = new Movement(game.getBoard(), start, end);

    if (!game.validateMove(movement)) {
      throw new InvalidMoveException();
    }
    
    game.performMove(movement);
  }

  public static void main(String[] args) {}
}
