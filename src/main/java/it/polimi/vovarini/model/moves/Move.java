package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.board.Board;

import java.io.Serializable;

/**
 * This class represents a generic move performed during the game.
 */
public abstract class Move implements Serializable {
  protected Board board;
  protected boolean forced;

  protected Move(Board board, boolean forced) {
    this.board = board;
    this.forced = forced;
  }

  /**
   * @return A move which, if executed, would rollback the results of this move.
   */
  public abstract Move reverse();

  /**
   * Executes this move.
   */
  public abstract void execute();

  public boolean isForced() {
    return forced;
  }

  public Board getBoard() {
    return board;
  }
}
