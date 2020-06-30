package it.polimi.vovarini.model;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Move;
import it.polimi.vovarini.model.moves.Movement;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents a Santorini game.
 */
public class Game implements Serializable, GameDataAccessor {

  public static final int MIN_PLAYERS = 2;
  public static final int MAX_PLAYERS = 3;

  private int initialNumberOfPlayers;

  private Player[] players;
  private int currentPlayerIndex;

  private Phase currentPhase;

  private final Board board;

  private final Stack<Move> moves;
  private final Stack<Move> undoneMoves;

  private GodName[] availableGodCards;

  private boolean setupComplete;

  private final Random random;

  /**
   * Creates a Game with the given number of players.
   *
   * @param numberOfPlayers How many players should be allowed into this game.
   * @throws InvalidNumberOfPlayersException If numberOfPlayers is less than {@value MIN_PLAYERS} or
   *                                         greater then {@value MAX_PLAYERS}.
   */
  public Game(int numberOfPlayers) throws InvalidNumberOfPlayersException {
    if (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
      throw new InvalidNumberOfPlayersException();
    }

    initialNumberOfPlayers = numberOfPlayers;

    players = new Player[numberOfPlayers];
    availableGodCards = new GodName[numberOfPlayers];

    currentPlayerIndex = 0;

    moves = new Stack<>();
    undoneMoves = new Stack<>();

    board = new Board(Board.DEFAULT_SIZE);

    currentPhase = Phase.Start;

    setupComplete = false;

    random = new Random();
  }

  /**
   * Getter method for the Board where the game is played
   * @return the instance of the Board where the game is played
   */
  public Board getBoard() {
    return board;
  }

  /**
   * This method adds a new player with the given nickname to this game,
   * then raises a {@link NewPlayerEvent}.
   * No validity check on nickname is performed.
   *
   * @param nickname the name of the player to be added
   * @throws InvalidNumberOfPlayersException if this game is full.
   */
  public void addPlayer(String nickname) {

    if (players[players.length - 1] != null) {
      throw new InvalidNumberOfPlayersException();
    }

    Player player = new Player(nickname);

    for (int i = 0; i < players.length; i++) {
      if (players[i] == null) {
        players[i] = player;
        GameEventManager.raise(new NewPlayerEvent(this, player));
        return;
      }
    }
  }

  /**
   * This method chooses a random player as the new current player.
   */
  public void drawElectedPlayer() {
    currentPlayerIndex = random.nextInt(players.length);
  }

  /**
   * Rotates the current player to the next one, then it either raises a {@link SelectYourCardEvent}
   * if they still have to choose a god card or a {@link PlaceYourWorkersEvent} if the worker
   * placement phase can start.
   */
  public void setupGodCards() {

    nextPlayer();

    if (availableGodCards.length == 1) {
      GodCard lastGodCard = GodCardFactory.create(availableGodCards[0]);
      lastGodCard.setGameData(this);
      getCurrentPlayer().setGodCard(lastGodCard);
      GameEventManager.raise(new CardAssignmentEvent(this, getCurrentPlayer(), lastGodCard));
      LinkedList<GodName> availableGods = new LinkedList<>(Arrays.asList(availableGodCards));
      availableGods.remove(lastGodCard.getName());
      availableGodCards = availableGods.toArray(GodName[]::new);

      // settare currentPlayer a players[0], oppure potremmo proseguire il turno da qui, lasciando invariato il codice attuale
      GameEventManager.raise(new PlaceYourWorkersEvent(this, getCurrentPlayer()));
    } else {
      GameEventManager.raise(new SelectYourCardEvent(this, getCurrentPlayer(), availableGodCards));
    }
  }

  /**
   * Executes the given movement on this game, raising a {@link PlayerInfoUpdateEvent} to add the move
   * to the list on the {@link Player} class.
   *
   * If this move leads to a win for the current player, this method also raises a {@link VictoryEvent}.
   * @param move The movement to be performed.
   */
  public void performMove(Movement move) {

    undoneMoves.clear();
    moves.push(move);
    getCurrentPlayer().getMovementList().add(move);
    GameEventManager.raise(new PlayerInfoUpdateEvent(this, getCurrentPlayer()));
    boolean isMovementWinning = getCurrentPlayer().getGodCard().isMovementWinning(move);


    for (Movement executableMove : getCurrentPlayer().getGodCard().consequences(move, this)) {
      Movement temp = new Movement(board, executableMove.getStart(), executableMove.getEnd());
      temp.execute();
      //executableMove.execute();
      //JDK ti odio più di sistemi informativi
      //Questo non è possibile
    }

    if (isMovementWinning) {
      GameEventManager.raise(new VictoryEvent(this, getCurrentPlayer()));
    }

  }

  /**
   * Executes the given construction on this game, raising a {@link PlayerInfoUpdateEvent} to add the move
   * to the list on the {@link Player} class.
   *
   * @param move The construction move to be performed.
   */
  public void performMove(Construction move) {

    undoneMoves.clear();
    moves.push(move);
    getCurrentPlayer().getConstructionList().add(move);
    GameEventManager.raise(new PlayerInfoUpdateEvent(this, getCurrentPlayer()));

    for (Move executableMove : getCurrentPlayer().getGodCard().consequences(move, this)) {
      executableMove.execute();
    }

  }

  /**
   * Getter method for the current Phase the game stands in
   * @return the instance of the current Phase the game stands in
   */
  public Phase getCurrentPhase() {
    return currentPhase;
  }

  /**
   * Setter method to set the Players playing this game
   * @param players the Players playing this game
   */
  public void setPlayers(Player[] players) {
    this.players = players;
  }

  /**
   * Getter method to get the Players playing this game
   * @return the players playing this game
   */
  public Player[] getPlayers() {
    return players;
  }

  /**
   * Getter method to get the Player currently playing
   * @return the instance of the Player currently playing
   */
  public Player getCurrentPlayer() {
    return players[currentPlayerIndex];
  }

  /**
   * Getter method for the set of cards in play in this Game
   * @return the names of the GodCards in play in this Game
   */
  public GodName[] getAvailableGodCards() {
    return availableGodCards;
  }

  /**
   * Setter method for the set of cards in play in this Game
   * @param availableGodCards the names of the GodCards to be played in this game
   */
  public void setAvailableGodCards(GodName[] availableGodCards) {
    this.availableGodCards = availableGodCards;
  }

  /**
   * Rotates the current player to the next one, then raises a {@link CurrentPlayerChangedEvent}.
   */
  public void nextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    getCurrentPlayer().setWorkerSelected(false);
    GameEventManager.raise(new CurrentPlayerChangedEvent(this, players[currentPlayerIndex]));
  }

  /**
   * This method checks if the current player has lost
   * @return true if the current player has lost, false otherwise
   */
  private boolean currentPlayerHasLost() {
    switch (currentPhase) {
      case Start -> {
        // check if at least one worker can move, else raise a LossEvent
        if (getCurrentPlayer().getGodCard().computeReachablePoints().isEmpty()) {
          getCurrentPlayer().setCurrentSex(getCurrentPlayer().getOtherWorker().getSex());
          if (getCurrentPlayer().getGodCard().computeReachablePoints().isEmpty()) {
            return true;
          }
        }
        getCurrentPlayer().setWorkerSelected(false);
      }
      case Movement -> {

      }
      case Construction -> {
        if (getCurrentPlayer().getGodCard().computeBuildablePoints().isEmpty()) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * This method removes a player, who probably lost, from the list of players involved in this Game
   * @param p is the player I want to remove
   */
  private void removePlayer(Player p) {
    Player[] newPlayersArray = new Player[players.length - 1];
    int k = 0;
    for (int i = 0; i < board.getSize(); i++) {
      for (int j = 0; j < board.getSize(); j++) {
        Point cur = new Point(i, j);
        if (p.getWorkers().containsValue(board.getBox(cur).getItems().peek())) {
          board.getBox(cur).removeTopmost();
        }
      }
    }
    for (Player player : players) {
      if (!player.equals(p)) {
        if (k >= newPlayersArray.length) {
          throw new RuntimeException("WTF man? Player not found");
        }
        newPlayersArray[k] = player;
        k++;
      }
    }

    players = newPlayersArray;
  }

  /**
   * Sets the current phase to the given one, then raises a {@link PhaseUpdateEvent}.
   *
   * If the current player can't perform any move in the new phase, if only one other player is left,
   * it raises a {@link VictoryEvent}, otherwise it uses {@link Player#setHasLost(boolean)} to
   * signal the player loss.
   *
   * @param phase The desired new phase.
   */
  public void setCurrentPhase(Phase phase) {
    this.currentPhase = phase;
    GameEventManager.raise(new PhaseUpdateEvent(this, currentPhase));
    Player lastPlayer = getCurrentPlayer();
    boolean lost = currentPlayerHasLost();
    if (lost) {
      removePlayer(lastPlayer);
      if (players.length <= 1) {
        GameEventManager.raise(new VictoryEvent(this, players[0]));
      } else {
        nextPlayer();
        lastPlayer.setHasLost(true);
        currentPhase = Phase.Start;
        GameEventManager.raise(new PhaseUpdateEvent(this, currentPhase));
      }
    } else {
      lastPlayer.setHasLost(false);
    }

  }

  /**
   * This method undoes the last move done by the player currently playing
   */
  public void undoLastMove() {
    try {
      Move opposite = moves.pop().reverse();
      undoneMoves.push(opposite);
      opposite.execute();
    } catch (EmptyStackException ignored) {

    }
  }

  /**
   * Starts the game and raises a {@link GameStartEvent}.
   */
  public void start() {
    setupComplete = true;
    for (Player p : players) {
      p.setWorkerSelected(false);
    }
    setCurrentPhase(Phase.Start);
    GameEventManager.raise(new GameStartEvent(this, this.getPlayers()));
  }

  /**
   * This method returns if the setup of the game is complete
   * @return true if the setup of the game is complete, false othwerise
   */
  public boolean isSetupComplete() {
    return setupComplete;
  }

  /**
   * This method checks if all players entered correctly in the Game
   * @return true if all players entered correctly in the Game, false otherwise
   */
  public boolean isFull() {
    return Arrays.stream(players).noneMatch(Objects::isNull);
  }

  /**
   * This method checks if the cards to play in the Game are already set
   * @return true if the cards to play in the Game are already set, false otherwise
   */
  public boolean isAvailableCardsAlreadySet() {
    return Arrays.stream(availableGodCards).noneMatch(Objects::isNull);
  }

  /**
   * Getter method for the number of players who will play the Game
   * @return the number of players who will play the Game
   */
  public int getInitialNumberOfPlayers() {
    return initialNumberOfPlayers;
  }
}
