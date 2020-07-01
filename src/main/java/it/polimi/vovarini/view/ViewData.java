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

  private boolean correctlyRegistered;

  public ViewData() {
    currentPhase = Phase.Start;
    board = new Board(Board.DEFAULT_SIZE);
    players = new LinkedHashSet<>();
    playersColors = new HashMap<>();

    random = new Random();

    correctlyRegistered = false;
  }

  public Phase getCurrentPhase() {
    return currentPhase;
  }

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

  public Player getOwner() {
    return owner;
  }

  public void setOwner(Player owner) {
    this.owner = owner;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public Set<Player> getPlayerSet() {
    return players;
  }

  @Override
  public Player[] getPlayers() {
    return players.toArray(Player[]::new);
  }

  public void addPlayer(Player player, Color color) {
    this.players.add(player);
    playersColors.put(player, color);
  }

  public void addPlayer(Player player) {
    addPlayer(player, new Color(random.nextInt(255),
            random.nextInt(255),
            random.nextInt(255)));
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public Worker getSelectedWorker() {
    return selectedWorker;
  }

  public void setSelectedWorker(Worker selectedWorker) {
    this.selectedWorker = selectedWorker;
  }

  public Point getCurrentStart() {
    return currentStart;
  }

  public void setCurrentStart(Point currentStart) {
    this.currentStart = currentStart;
  }

  public Map<Player, Color> getPlayersColors() {
    return playersColors;
  }

  public void setCorrectlyRegistered(boolean correctlyRegistered) {
    this.correctlyRegistered = correctlyRegistered;
  }

  public boolean isCorrectlyRegistered() {
    return correctlyRegistered;
  }
}
