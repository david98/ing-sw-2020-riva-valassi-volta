package it.polimi.vovarini.controller;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.common.exceptions.OverwrittenWorkerException;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;

import java.util.Arrays;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that represents the Controller concept of the MVC pattern. Acts as a Listener to events triggered from the View, and updates the Model about it, notifying him
 * of what he has to change
 */
public class Controller implements EventListener {

  private final Game game;

  public Game getGame() {
    return game;
  }

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    GameEventManager.bindListeners(this);
    this.game = game;
  }

  /**
   *
   * @param evt is the RegistrationEvent the view generates when a player wants to log into the game
   * @throws InvalidNicknameException if the nickname is not validated (length, special characters...) or if is already in use inside the current played game
   * @throws InvalidNumberOfPlayersException if another player wants to log into the game, but the game already has all its players
   */
  @GameEventListener
  public void update(RegistrationEvent evt) throws InvalidNicknameException {
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
        game.drawElectedPlayer();
        GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
        GameEventManager.raise(new GodSelectionStartEvent(game, game.getPlayers(), game.getCurrentPlayer(), godNames));
      }
    } catch (InvalidNumberOfPlayersException e) {
      throw new InvalidNumberOfPlayersException();
    }
  }

  @GameEventListener
  public void update(AvailableCardsEvent evt) {

    // ho già ricevuto le carte scelte per questa partita
    if(game.isAvailableCardsAlreadySet())
      throw new AvailableCardsAlreadySetException();

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    // mi hai dato più carte (o meno) del dovuto
    if(evt.getSelectedGods().length != game.getAvailableGodCards().length) throw new InvalidNumberOfGodCardsException();

    List<GodName> values = Arrays.asList(GodName.values());

    for (int i = 0; i < evt.getSelectedGods().length-1; i++) {

      // la carta scelta è nulla
      if(evt.getSelectedGods()[i] == null || evt.getSelectedGods()[i+1] == null) throw new InvalidCardException();

      // la carta scelta non esiste
      if(!values.contains(evt.getSelectedGods()[i]) || !values.contains(evt.getSelectedGods()[i+1])) throw new InvalidCardException();

      // ci sono due carte uguali
      for(int k = i+1; k < evt.getSelectedGods().length; k++)
        if(evt.getSelectedGods()[i].equals(evt.getSelectedGods()[k])) {
          throw new InvalidCardException();
        }
    }

    game.setAvailableGodCards(evt.getSelectedGods());
    game.setupGodCards();
  }

  @GameEventListener
  public void update(CardChoiceEvent evt) {
    // carte non ancora scelte
    if(!game.isAvailableCardsAlreadySet()) throw new CardsNotSelectedException();

    // carte già assegnate ai players (se length = 1, devo assegnarla in automatico)
    if(game.getAvailableGodCards().length <= 1) {
      // throw new SalcazzoException();
    }

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    LinkedList<GodName> availableGods = new LinkedList<>(Arrays.asList(game.getAvailableGodCards()));

    if (!availableGods.contains(evt.getSelectedGod())) {
      throw new InvalidCardException();
    }

    GodCard playerCard = GodCardFactory.create(evt.getSelectedGod());
    playerCard.setGameData(game);
    currentPlayer.setGodCard(playerCard);

    availableGods.remove(evt.getSelectedGod());

    game.setAvailableGodCards(availableGods.toArray(GodName[]::new));
    GameEventManager.raise(new CardAssignmentEvent(game, currentPlayer, playerCard));
    game.setupGodCards();
  }

  /**
   *
   * @param evt is the WorkerSelectionEvent the view generates when a player selects the Worker he wants to use
   * @throws InvalidPhaseException The selection of the Worker must be performed during Phase.Start. If the controller receives this event in another phase, something is wrong
   * @throws WrongPlayerException Another player must not be able to select a Worker when he's not playing
   */
  @GameEventListener
  public void update(WorkerSelectionEvent evt) {
    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Start))
      throw new InvalidPhaseException();

    game.getCurrentPlayer().setCurrentSex(evt.getSex());
    if (game.isSetupComplete()) {
      game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    }
  }

  /**
   *
   * @param evt is the SpawnWorkerEvent the view generates when a player has to place a Worker for the first time
   * @throws WrongPlayerException Another player must not be able to choose the starting position of the current player's own Worker
   * @throws InvalidPositionException If the player tries to put his Worker outside the board
   * @throws OverwrittenWorkerException If the player tries to put his Worker on top of another Worker (of any player)
   */
  @GameEventListener
  public void update(SpawnWorkerEvent evt) {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    Point target = evt.getTarget();
    if (!game.getBoard().isPositionValid(target)) throw new InvalidPositionException();

    Worker currentWorker = currentPlayer.getCurrentWorker();

    try {
      game.getBoard().getItemPosition(currentWorker);  // se scatena ItemNotFoundExc, può essere piazzato
      // worker già piazzato
      throw new WorkerAlreadySpawnedException();

    } catch (ItemNotFoundException e) {
      Item targetItem = game.getBoard().getItems(target).peek();
      if (targetItem != null && !currentWorker.canBePlacedOn(game.getBoard().getItems(target).peek())){
        // Worker sopra altro worker
        throw new OverwrittenWorkerException();
      } else {
        game.getBoard().place(currentPlayer.getCurrentWorker(), target);

        /**
         * controlliamo, se ha piazzato tutti i propri operai passiamo la mano al
         * giocatore successivo
         */
        if (currentPlayer.getWorkers().values().stream().noneMatch(worker -> {
          try {
            game.getBoard().getItemPosition(worker);
            return false;
          } catch (ItemNotFoundException exception){
            return true;
          }
        }
        )) {
          game.nextPlayer();
          if (game.getCurrentPlayer().getWorkers().values().stream().noneMatch(worker -> {
                    try {
                      game.getBoard().getItemPosition(worker);
                      return false;
                    } catch (ItemNotFoundException exception){
                      return true;
                    }
                  }
          )) {
            // tutti hanno piazzato
            game.start();
          } else {
            GameEventManager.raise(new PlaceYourWorkersEvent(game, game.getCurrentPlayer()));
          }
        }
      }
    }
  }

  /**
   *
   * @param evt is the BuildEvent the view generates when a player wants to perform a Construction move
   * @throws InvalidPositionException If the box selected by the player as the construction target is not inside the valid boxes computed by computeBuildablePoints
   * @throws InvalidPhaseException If the player triggers a Construction move in a phase that is not Phase.Construction
   * @throws WrongPlayerException If a construction move is triggered by a player which is not the one currently playing
   * @throws InvalidMoveException If a player selects a valid Box, but tries to build an invalid block in terms of level
   */
  @GameEventListener
  public void update(BuildEvent evt) {
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

    if (!game.getCurrentPlayer().getGodCard().validate(game.getCurrentPlayer().getGodCard().computeReachablePoints(), build))
        throw new InvalidMoveException();

    game.performMove(build);
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
  }

  /**
   *
   * @param evt is the MovementEvent the view generates when a player wants to perform a Movement move
   * @throws InvalidPhaseException If the player triggers a Movement move in a phase that is not Phase.Movement
   * @throws WrongPlayerException If a movement move is triggered by a player which is not the one currently playing
   * @throws InvalidPositionException If the box selected by the player as the movement target is not inside the valid boxes computed by computeReachablePoints
   * @throws InvalidMoveException This should never happen, as there are not other controls to perform other than reach.
   */
  @GameEventListener
  public void update(MovementEvent evt) {
    try {
      Player currentPlayer = game.getCurrentPlayer();
      if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

      Point start = game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker());

      Phase currentPhase = game.getCurrentPhase();
      if (!currentPhase.equals(Phase.Movement)) throw new InvalidPhaseException();

      Point end = evt.getPoint();
      if (!game.getBoard().isPositionValid(end)) throw new InvalidPositionException();

      Movement movement = new Movement(game.getBoard(), start, end);

        if (!game.getCurrentPlayer().getGodCard().validate(game.getCurrentPlayer().getGodCard().computeReachablePoints(), movement))
          throw new InvalidMoveException();


      game.performMove(movement);
      game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    } catch (ItemNotFoundException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   *
   * @param evt is the UndoEvent the view generates when a player wants to undo a move he just performed
   * @throws WrongPlayerException if another player tries to undo the last move performed, a move that he did not perform
   */
  @GameEventListener
  public void update(UndoEvent evt) {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    game.undoLastMove();
  }

  /**
   *
   * @param evt is the SkipEvent the view generates when a player wants to skip to the next phase
   * @throws WrongPlayerException if another players tries to skip a phase when he's not currently playing
   * @throws UnskippablePhaseException if the current player tries to skip a phase while he did not perform the required actions of that phase
   */
  @GameEventListener
  public void update(SkipEvent evt) {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    if (game.getCurrentPhase().equals(Phase.Start) && !currentPlayer.isWorkerSelected()) throw new UnskippablePhaseException();
    if (game.getCurrentPhase().equals(Phase.Movement) && currentPlayer.getMovementList().isEmpty()) throw new UnskippablePhaseException();
    if (game.getCurrentPhase().equals(Phase.Construction) && currentPlayer.getConstructionList().isEmpty()) throw new UnskippablePhaseException();

    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
  }

}
