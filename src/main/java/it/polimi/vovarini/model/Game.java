package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.*;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;

import java.util.EmptyStackException;
import java.util.Stack;

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
    for (int i = 0; i < numberOfPlayers; i++) {
      players[i] = new Player(GodCardFactory.create(GodName.Apollo), "Player" + i);
    }

    players = new Player[numberOfPlayers];
    currentPlayerIndex = 0;

    moves = new Stack<>();
    undoneMoves = new Stack<>();

    board = new Board(Board.DEFAULT_SIZE);

    currentPhase = Phase.Start;
  }

  public boolean validateMove(Movement movement) {
    return false;
  }

  public boolean validateMove(Construction construction) {
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
    currentPhase = currentPhase.next();
    return currentPhase;
  }

  public Player[] getPlayers() {
    return players;
  }

  public Player getCurrentPlayer() {
    return players[currentPlayerIndex];
  }

  public Player nextPlayer() {
    currentPhase = Phase.Start;
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    return players[currentPlayerIndex];
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
}
