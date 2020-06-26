package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.board.Board;

import java.io.Serializable;

/**
 * This class represents any kind of move performable by a player inside the game
 */
public abstract class Move implements Serializable {
  protected Board board;
  protected boolean forced;

  protected Move(Board board, boolean forced) {
    this.board = board;
    this.forced = forced;
  }

  public abstract Move reverse();

  public abstract void execute();

  public boolean isForced() {
    return forced;
  }

  public Board getBoard() {
    return board;
  }
}
