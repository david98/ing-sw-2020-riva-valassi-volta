package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;

/**
 * This class represents a general construction move, where the players decides to build a block on a cell of the board
 */
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
  public void execute() {
    Item targetItem = board.getItems(target).peek();

    if (targetItem != null && targetItem.canBeRemoved()) {
      board.remove(target);
      board.place(block, target);
      board.place(targetItem, target);
    } else {
      board.place(block,target);
    }
  }

  public Block getBlock() { return block; }

  public Point getTarget() { return target; }
}
