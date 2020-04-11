package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.*;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxFullException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.godcards.GodName;

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

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Start)) throw new InvalidPhaseException();

    game.getCurrentPlayer().setCurrentSex(evt.getSex());
  }

  // CLI: Coordinates input or keyboard arrows
  public void update(BuildEvent evt)
      throws InvalidPositionException, InvalidPhaseException,
          WrongPlayerException, InvalidMoveException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Construction)) throw new InvalidPhaseException();

    Point input = new Point(evt.getBuildEnd());
    if (!game.getBoard().isPositionValid(input)) throw new InvalidPositionException();

    Board board = game.getBoard();
    Block toBuild = Block.blocks[evt.getLevel() - 1];
    Point target = evt.getBuildEnd();

    Construction build = new Construction(board, toBuild, target, false);

    if (!game.validateMove(build)) throw new InvalidMoveException();

    game.performMove(build);
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
  public void update(RegistrationEvent evt) throws InvalidNicknameException, InvalidNumberOfPlayersException {

    for (Player player : game.getPlayers()) {
      if(player == null) break ;
      if (player.getNickname().equalsIgnoreCase(evt.getNickname())) {
        throw new InvalidNicknameException(InvalidNicknameException.ERROR_DUPLICATE);
      }
    }
    if (!Player.validateNickname(evt.getNickname())) {
      throw new InvalidNicknameException(InvalidNicknameException.ERROR_INVALID);
    }

    try {
      game.addPlayer(evt.getNickname());
    } catch (InvalidNumberOfPlayersException e) {
      throw new InvalidNumberOfPlayersException();
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
  // Non va bene in fase END, perchè bisognerebbe lanciare NextPlayerEvent
  public void update(SkipEvent evt) throws WrongPlayerException, InvalidPhaseException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (currentPhase.equals(Phase.End)) throw new InvalidPhaseException();

    game.nextPhase();
  }

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

    if (!game.validateMove(movement)) throw new InvalidMoveException();
    
    game.performMove(movement);
  }

  public void update(SpawnWorkerEvent evt) throws WrongPlayerException, InvalidPositionException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getPlayerSource())) throw new WrongPlayerException();

    Point target = evt.getTarget();
    if (!game.getBoard().isPositionValid(target)) throw new InvalidPositionException();

    try {
      game.getBoard().place(currentPlayer.getCurrentWorker(), target);
    }
    catch (BoxFullException ignored){}
  }



  public static void main(String[] args) {}
}
