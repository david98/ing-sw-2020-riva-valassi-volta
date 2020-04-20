package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.board.Board;

public abstract class Move {
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
