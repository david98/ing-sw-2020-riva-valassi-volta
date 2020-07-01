package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.board.Board;

import java.io.Serializable;

/**
 * This class represents a generic move performed during the game.
 */
public abstract class Move implements Serializable {
  protected Board board;
  protected boolean forced;

  /**
   * Builds a generic move
   * @param board the board where the game is played and the items are placed
   * @param forced true if the system forces the move, false otherwise (user move)
   */
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

  /**
   * Returns if the move is forced (performed by the system)
   * @return true if the move is forced, false otherwise
   */
  public boolean isForced() {
    return forced;
  }

  /**
   * Getter method for the Board where the Move acts
   * @return the board where the move acts
   */
  public Board getBoard() {
    return board;
  }
}
