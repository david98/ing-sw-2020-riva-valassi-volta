package it.polimi.vovarini.view;

import it.polimi.vovarini.model.GameDataAccessor;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.view.cli.styling.Color;

import java.io.Serializable;
import java.util.*;

/**
 * Data that is needed by any kind of view.
 *
 * @author Davide Volta
 */
public class ViewData implements Serializable, GameDataAccessor {
  private Player owner;
  private Player currentPlayer;
  private final Set<Player> players;
  private final Map<Player, Color> playersColors;
  private Phase currentPhase;

  private Board board;

  private Worker selectedWorker;
  private Point currentStart;

  private final Random random;

  /**
   * Builds a new ViewData object
   */
  public ViewData() {
    currentPhase = Phase.Start;
    board = new Board(Board.DEFAULT_SIZE);
    players = new LinkedHashSet<>();
    playersColors = new HashMap<>();

    random = new Random();
  }

  /**
   * Getter method for the current phase inside ViewData
   * @return the current phase inside ViewData
   */
  public Phase getCurrentPhase() {
    return currentPhase;
  }

  /**
   * Setter method for the current phase inside ViewData
   * @param currentPhase is the phase I want to set as current inside ViewData
   */

  public void setCurrentPhase(Phase currentPhase) {
    this.currentPhase = currentPhase;
  }

  @Override
  public boolean isFull() {
    return false;
  }

  @Override
  public void nextPlayer() {

  }

  /**
   * Getter method for the owner of this ViewData
   * @return the player who owns this ViewData
   */
  public Player getOwner() {
    return owner;
  }

  /**
   * Setter method for the owner of this ViewData
   * @param owner a player that should own this ViewData
   */
  public void setOwner(Player owner) {
    this.owner = owner;
  }

  /**
   * Getter method for the current player in this ViewData
   * @return the current player in this ViewData
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Setter method for the current player in this ViewData
   * @param currentPlayer is the player who I want to set as currently playing inside this ViewData
   */
  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  /**
   * Getter method for a Set of the players involved in the game
   * @return a Set of the players involved in the game
   */
  public Set<Player> getPlayerSet() {
    return players;
  }

  /**
   * Getter method for an array of the players involved in the game
   * @return an array of the players involved in the game
   */
  @Override
  public Player[] getPlayers() {
    return players.toArray(Player[]::new);
  }

  /**
   * This method adds a player inside this ViewData
   * @param player the player I want to add
   * @param color the color associated with that player
   */
  public void addPlayer(Player player, Color color) {
    this.players.add(player);
    playersColors.put(player, color);
  }

  /**
   * This method adds a player inside this ViewData
   * @param player the player I want to add
   */
  public void addPlayer(Player player) {
    addPlayer(player, new Color(random.nextInt(255),
            random.nextInt(255),
            random.nextInt(255)));
  }

  /**
   * This method removes a player from ViewData
   * @param player the player I want to remove from ViewData
   */
  public void removePlayer(Player player) {
    players.remove(player);
  }

  /**
   * Getter method for the Board inside this ViewData
   * @return the Board inside this ViewData
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Setter method for the Boards inside this ViewData
   * @param board the Board I want to set inside this ViewData
   */
  public void setBoard(Board board) {
    this.board = board;
  }

  /**
   * Getter method for the worker currently selected inside this ViewData
   * @return the worker currently selected inside this ViewData
   */
  public Worker getSelectedWorker() {
    return selectedWorker;
  }

  /**
   * Setter method for the worker currently selected inside this ViewData
   * @param selectedWorker the Worker I want to set as currently selected inside this ViewData
   */
  public void setSelectedWorker(Worker selectedWorker) {
    this.selectedWorker = selectedWorker;
  }

  /**
   * Getter method for the current starting point inside this ViewData. Is the point from which the player starts playing
   * @return the current starting point inside this ViewData
   */
  public Point getCurrentStart() {
    return currentStart;
  }

  /**
   * Setter method for the current starting point inside this ViewData. Is the point from which the player starts playing
   * @param currentStart is the Point I want to set as start in this ViewData
   */
  public void setCurrentStart(Point currentStart) {
    this.currentStart = currentStart;
  }

  /**
   * Getter method for a Map of the players and the colors associated to them
   * @return a Map containing information about the colors associated to each player
   */
  public Map<Player, Color> getPlayersColors() {
    return playersColors;
  }
}
