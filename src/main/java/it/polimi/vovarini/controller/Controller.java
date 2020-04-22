package it.polimi.vovarini.controller;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.OverwrittenWorkerException;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;

import java.util.ArrayList;
import java.util.EventListener;

public class Controller implements EventListener {

  private ArrayList<GodName> selectedCards;
  private final Game game;

  public Game getGame() {
    return game;
  }

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    GameEventManager.bindListeners(this);
    this.game = game;
    selectedCards = new ArrayList<>();
  }

  // CLI: String input at the start of the game. Any string longer than one should be considered a
  // nickname input
  @GameEventListener
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
      if (game.isFull()){
        GameEventManager.raise(new GameStartEvent(this));
      }
    } catch (InvalidNumberOfPlayersException e) {
      throw new InvalidNumberOfPlayersException();
    }
  }

  // Not part of the 1vs1 simulation we want to develop now
  @GameEventListener
  public void update(CardChoiceEvent evt) {
    this.selectedCards.addAll(evt.getSelectedCards());
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

  // CLI: keyboard M,F characters
  @GameEventListener
  public void update(WorkerSelectionEvent evt) throws InvalidPhaseException, WrongPlayerException {
    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Start))
      throw new InvalidPhaseException();

    game.getCurrentPlayer().setCurrentSex(evt.getSex());
  }

  @GameEventListener
  public void update(SpawnWorkerEvent evt) throws WrongPlayerException, InvalidPositionException, OverwrittenWorkerException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    Point target = evt.getTarget();
    if (!game.getBoard().isPositionValid(target)) throw new InvalidPositionException();

    Worker currentWorker = currentPlayer.getCurrentWorker();

    try {
      if (game.getBoard().getItemPosition(currentWorker) != null) {
        // Worker già posizionato
        throw new OverwrittenWorkerException();
      }
    } catch (ItemNotFoundException e) {
      try {
        if(!currentWorker.canBePlacedOn(game.getBoard().getItems(target).peek())) {
          // Worker sopra altro worker
          throw new OverwrittenWorkerException();
        }
        // non dovrebbe mai arrivare qui, viene sempre scatenata BoxEmptyException
      } catch (BoxEmptyException ex) {
        try {
          game.getBoard().place(currentPlayer.getCurrentWorker(), target);
        } catch (BoxFullException ignored) {
          // Non dovrebbe mai succedere
        }
      }
    }
  }

  // CLI: Coordinates input or keyboard arrows
  @GameEventListener
  public void update(BuildEvent evt)
      throws InvalidPositionException, InvalidPhaseException,
          WrongPlayerException, InvalidMoveException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

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

  // CLI: Coordinates input or keyboard arrows
  @GameEventListener
  public void update(MovementEvent evt)
          throws InvalidPhaseException, WrongPlayerException, InvalidPositionException,
          InvalidMoveException {
    try {
      Player currentPlayer = game.getCurrentPlayer();
      if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

      Point start = game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker());

      Phase currentPhase = game.getCurrentPhase();
      if (!currentPhase.equals(Phase.Movement)) throw new InvalidPhaseException();

      Point end = evt.getPoint();
      if (!game.getBoard().isPositionValid(end)) throw new InvalidPositionException();

      Movement movement = new Movement(game.getBoard(), start, end);
      if (!game.validateMove(movement)) throw new InvalidMoveException();

      // Se la mossa è valida, prima eseguo le conseguenze (ndr cambiare nome) sideEffects
      currentPlayer.getGodCard().consequences(game);

      game.performMove(movement);
    } catch (ItemNotFoundException e) {
      throw new RuntimeException(e);
    }

  }

  // CLI: Ctrl+Z pressed by the user
  @GameEventListener
  public void update(UndoEvent evt) throws WrongPlayerException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    game.undoLastMove();
  }

  // CLI: we should find something to press that will make the "skip button" function, to trigger
  // the next turn (like pressing space)
  @GameEventListener
  public void update(NextPlayerEvent evt) throws InvalidPhaseException, WrongPlayerException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.End)) throw new InvalidPhaseException();

    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
  }

  // Gestisce lo skip dell'utente tra una fase e l'altra
  // Non va bene in fase END, perchè bisognerebbe lanciare NextPlayerEvent
  @GameEventListener
  public void update(SkipEvent evt) throws WrongPlayerException, InvalidPhaseException {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (currentPhase.equals(Phase.End)) throw new InvalidPhaseException();

    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
  }


  public static void main(String[] args) {}
}
