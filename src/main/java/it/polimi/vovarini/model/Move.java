package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;

public abstract class Move {
  protected Board board;

  protected Move(Board board) {
    this.board = board;
  }

  public abstract Move reverse();

  public abstract void execute();
}
