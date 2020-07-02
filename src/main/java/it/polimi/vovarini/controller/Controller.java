package it.polimi.vovarini.controller;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.common.network.server.Server;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acts as an intermediary between remote views and the model: it listens for events raised by remote views and
 * acts accordingly on the model.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 */
public class Controller implements EventListener {

  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  private final Game game;

  public Game getGame() {
    return game;
  }

  /**
   * Creates a Controller acting on the given instance of {@link Game}.
   * @param game The instance of game to be handled by this controller.
   */
  public Controller(Game game) {
    GameEventManager.bindListeners(this);
    this.game = game;
  }

  /**
   * Adds a player to the game, if possible.
   * Raises {@link InvalidNicknameEvent} if the chosen nickname is invalid or already in use
   * by another player.
   *
   * @param evt Raised by a player when they choose a nickname.
   * @throws InvalidNumberOfPlayersException If the game is already full.
   */
  @GameEventListener
  public void update(RegistrationEvent evt) {
    for (Player player : game.getPlayers()) {
      if (player == null) break;
      if (player.getNickname().equalsIgnoreCase(evt.getNickname())) {
        LOGGER.log(Level.INFO, "Raising InvalidNicknameEvent due to a duplication.");
        GameEventManager.raise(new InvalidNicknameEvent(game, InvalidNicknameEvent.ERROR_DUPLICATE, evt.getNickname()));
        return;
      }
    }

    if (!Player.validateNickname(evt.getNickname())) {
      LOGGER.log(Level.SEVERE, "Raising InvalidNicknameEvent due to an invalid nickname.");
      GameEventManager.raise(new InvalidNicknameEvent(game, InvalidNicknameEvent.ERROR_INVALID, evt.getNickname()));
      return;
    }

    try {
      game.addPlayer(evt.getNickname());
      if (game.isFull()) {
        game.drawElectedPlayer();
        GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
        GameEventManager.raise(new BoardUpdateEvent(game, game.getBoard())); //this is normally not needed
        GameEventManager.raise(new GodSelectionStartEvent(game, game.getPlayers(), game.getCurrentPlayer(), godNames));
      }
    } catch (InvalidNumberOfPlayersException e) {
      throw new InvalidNumberOfPlayersException();
    }
  }

  /**
   * Sets the available cards on game, then starts the card selection phase.
   *
   * @param evt Raised by the elected player after they have chosen the available cards.
   * @throws AvailableCardsAlreadySetException If the available cards have already been chosen.
   * @throws WrongPlayerException If the event hasn't been raised by the elected player.
   * @throws InvalidNumberOfGodCardsException If the number of godcards is not the same as the number of players.
   * @throws InvalidCardException If one of the chosen cards is null, doesn't exist or there are duplicate cards.
   */
  @GameEventListener
  public void update(AvailableCardsEvent evt) {

    // ho già ricevuto le carte scelte per questa partita
    if (game.isAvailableCardsAlreadySet())
      throw new AvailableCardsAlreadySetException();

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    // mi hai dato più carte (o meno) del dovuto
    if (evt.getSelectedGods().length != game.getAvailableGodCards().length)
      throw new InvalidNumberOfGodCardsException();

    List<GodName> values = Arrays.asList(GodName.values());

    for (int i = 0; i < evt.getSelectedGods().length - 1; i++) {

      // la carta scelta è nulla
      if (evt.getSelectedGods()[i] == null || evt.getSelectedGods()[i + 1] == null) throw new InvalidCardException();

      // la carta scelta non esiste
      if (!values.contains(evt.getSelectedGods()[i]) || !values.contains(evt.getSelectedGods()[i + 1]))
        throw new InvalidCardException();

      // ci sono due carte uguali
      for (int k = i + 1; k < evt.getSelectedGods().length; k++)
        if (evt.getSelectedGods()[i].equals(evt.getSelectedGods()[k])) {
          throw new InvalidCardException();
        }
    }

    game.setAvailableGodCards(evt.getSelectedGods());
    game.setupGodCards();
  }

  /**
   * Registers the choice of a card made by a player, then raises a {@link CardAssignmentEvent}.
   *
   * @param evt Raised by a player when they select their card.
   * @throws CardsNotSelectedException If the available cards haven't been chosen yet.
   * @throws WrongPlayerException If the event hasn't been raised by the current player.
   * @throws InvalidCardException If the chosen card isn't in the available cards list.
   */
  @GameEventListener
  public void update(CardChoiceEvent evt) {
    // carte non ancora scelte
    if (!game.isAvailableCardsAlreadySet()) throw new CardsNotSelectedException();

    // carte già assegnate ai players (se length = 1, devo assegnarla in automatico)
    if (game.getAvailableGodCards().length <= 1) {
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
   * Selects a worker as the current one for the current player, then
   * moves the game to the next phase if it is in progress.
   *
   * @param evt Raised by a player when they select their current worker.
   * @throws InvalidPhaseException If the current phase isn't {@code Phase.Start}
   * @throws WrongPlayerException If the event hasn't been raised by the current player.
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
   * Tries to spawn a worker at a given position, then raises a {@link PlaceYourWorkersEvent} for the next worker to be
   * placed, or, if everyone has placed their workers, it starts the game.
   *
   * @param evt Raised by a player when they try to place a worker at a given position for the first time.
   * @throws WrongPlayerException       If the event hasn't been raised by the current player.
   * @throws InvalidPositionException   If the target position would be outside of the board.
   * @throws WorkerAlreadySpawnedException If the selected worker has already been placed on the board.
   * @throws OverwrittenWorkerException If the target position is already occupied by another worker.
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
      Item targetItem = game.getBoard().getBox(target).getItems().peek();
      if (targetItem != null && !currentWorker.canBePlacedOn(game.getBoard().getBox(target).getItems().peek())) {
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
                  } catch (ItemNotFoundException exception) {
                    return true;
                  }
                }
        )) {
          game.nextPlayer();
          if (game.getCurrentPlayer().getWorkers().values().stream().noneMatch(worker -> {
            try {
              game.getBoard().getItemPosition(worker);
              return false;
            } catch (ItemNotFoundException exception) {
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
   * Tries to perform a construction for the current player and their current worker.
   *
   * @param evt Raised by the player when they try to perform a construction.
   * @throws InvalidPositionException If the target position would be outside of the board.
   * @throws InvalidPhaseException If the current phase is not {@code Phase.Construction}.
   * @throws WrongPlayerException If the event hasn't been raised by the current player.
   * @throws InvalidMoveException If the player's current worker can't actually build on the target position.
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

    if (!game.getCurrentPlayer().getGodCard().validate(game.getCurrentPlayer().getGodCard().computeBuildablePoints(), build)) {
      GameEventManager.raise(new PhaseUpdateEvent(game, game.getCurrentPhase())); // needed to avoid the client freezing
      throw new InvalidMoveException();
    }

    game.performMove(build);
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
  }

  /**
   * Tries to perform a movement for the current player and their current worker.
   *
   * @param evt Raised by the player when they try to perform a movement.
   * @throws InvalidPhaseException If the current phase is not {@code Phase.Movement}.
   * @throws WrongPlayerException If the event hasn't been raised by the current player.
   * @throws InvalidPositionException If the target position would be outside of the board.
   * @throws InvalidMoveException If the target position isn't reachable.
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

      if (!game.getCurrentPlayer()
          .getGodCard()
          .validate(game.getCurrentPlayer().getGodCard().computeReachablePoints(), movement))
        throw new InvalidMoveException();

      game.performMove(movement);
      game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game));
    } catch (ItemNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Tries to skip to the next phase, if possible.
   *
   * @param evt Raised by the player when they want to skip the current phase without doing anything.
   * @throws WrongPlayerException If the event hasn't been raised by the current player.
   */
  @GameEventListener
  public void update(SkipEvent evt) {

    Player currentPlayer = game.getCurrentPlayer();
    if (!currentPlayer.equals(evt.getSource())) throw new WrongPlayerException();

    if (game.getCurrentPhase().equals(Phase.End)) {
      game.getCurrentPlayer().setWorkerSelected(false);
    }
    game.setCurrentPhase(game.getCurrentPlayer().getGodCard().computeNextPhase(game, true));
  }

}
