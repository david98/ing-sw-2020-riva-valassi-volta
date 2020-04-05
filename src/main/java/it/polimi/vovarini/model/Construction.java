package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;

public class Construction extends Move {

  private Block block;
  private Point target;

  public Construction(Board board, Block block, Point point, boolean forced) {
    super(board, forced);
    this.block = block;
    target = point;
  }

  public Construction(Board board, Block block, Point point) {
    super(board, false);
    this.block = block;
    target = point;
  }

  @Override
  public Move reverse() {
    return new Destruction(board, block, target, forced);
  }

  @Override
  public void execute() {}
}
