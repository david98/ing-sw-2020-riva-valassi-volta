package it.polimi.vovarini.model;

import it.polimi.vovarini.common.events.CurrentPlayerChangedEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.events.PhaseUpdateEvent;
import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.CurrentPlayerLosesException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Move;
import it.polimi.vovarini.model.moves.Movement;

import java.util.*;

public class Game {

  public static final int MIN_PLAYERS = 2;
  public static final int MAX_PLAYERS = 3;

  private Player[] players;
  private int currentPlayerIndex;

  private Phase currentPhase;

  public Board getBoard() {
    return board;
  }

  private Board board;

  private Stack<Move> moves;
  private Stack<Move> undoneMoves;

  public Game(int numberOfPlayers) throws InvalidNumberOfPlayersException {
    if (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
      throw new InvalidNumberOfPlayersException();
    }

    players = new Player[numberOfPlayers];

    currentPlayerIndex = 0;

    moves = new Stack<>();
    undoneMoves = new Stack<>();

    board = new Board(Board.DEFAULT_SIZE);

    currentPhase = Phase.Start;
  }

  public void addPlayer(String nickname)
      throws InvalidNumberOfPlayersException {

    if (players[players.length - 1] != null) {
      throw new InvalidNumberOfPlayersException();
    }

    Player player = new Player(nickname);

    for (int i = 0; i < players.length; i++) {
      if (players[i] == null) {
        players[i] = player;
        return;
      }
    }
  }

  /**
   * Returns true if movement is valid for the current player and their current worker.
   *
   * @param movement The move to be validated.
   * @return If movement is valid.
   */
  public boolean validateMove(Movement movement) {

    try {
      Collection<Point> reachablePoints = getCurrentPlayer().getGodCard().computeReachablePoints();

      return reachablePoints.contains(movement.getEnd());
    } catch (CurrentPlayerLosesException e){
      return false;
    }

  }

  public boolean validateMove(Construction construction) {

    try {
      for (Point point : getCurrentPlayer().getGodCard().computeBuildablePoints()) {
        if (construction.getTarget().equals(point)) {
          Stack<Item> items = board.getItems(construction.getTarget());
          if (construction.getBlock().canBePlacedOn(items.peek()))
            return true;
        }
      }
    } catch (CurrentPlayerLosesException e) {
      System.out.println("Tu non puoi costruire perchè hai perso.");
      System.out.println("Io invece devo gestire questa eccezione come si deve.");
      System.out.println("Ma prima devo capire come viene lanciata.");
    } catch (BoxEmptyException e) {
      if(construction.getBlock().getLevel() == 1) {
        return true;
      }
    } catch (InvalidPositionException ignored) {
    }

    return false;
  }

  public void performMove(Move move) {
    undoneMoves.clear();
    moves.push(move);
    move.execute();
  }

  public Phase getCurrentPhase() {
    return currentPhase;
  }

  public Phase nextPhase() {
    setCurrentPhase(currentPhase.next());
    return currentPhase;
  }

  public Player[] getPlayers() {
    return players;
  }

  public Player getCurrentPlayer() {
    return players[currentPlayerIndex];
  }

  public Player nextPlayer() {
    setCurrentPhase(Phase.Start);
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    GameEventManager.raise(new CurrentPlayerChangedEvent(this, players[currentPlayerIndex].clone()));
    return players[currentPlayerIndex];
  }

  public void setCurrentPhase(Phase phase){
    this.currentPhase = phase;
    GameEventManager.raise(new PhaseUpdateEvent(this, phase));
  }

  // needs to manage turn flow
  public void undoLastMove() {
    try {
      Move opposite = moves.pop().reverse();
      undoneMoves.push(opposite);
      opposite.execute();
    } catch (EmptyStackException ignored) {

    }
  }

  public void redoMove() {
    try {
      Move move = undoneMoves.pop().reverse();
      moves.push(move);
      move.execute();
    } catch (EmptyStackException ignored) {

    }
  }

  public boolean isFull(){
    return Arrays.stream(players).noneMatch(Objects::isNull);
  }
}
