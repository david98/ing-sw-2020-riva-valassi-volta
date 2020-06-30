package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;

/**
 * This class represents an undo of a Construction move.
 */
public class Destruction extends Move {

  private Block block;
  private Point target;

  public Destruction(Board board, Block block, Point point, boolean forced) {
    super(board, forced);
    this.block = block;
    target = point;
  }

  public Destruction(Board board, Block block, Point point) {
    super(board, false);
    this.block = block;
    target = point;
  }

  @Override
  public Move reverse() {
    return new Construction(board, block, target, forced);
  }

  @Override
  public void execute() {
  }

  public Block getBlock() {
    return block;
  }

  public Point getTarget() {
    return target;
  }

}