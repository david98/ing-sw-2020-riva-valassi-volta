package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;

/**
 * This class represents a construction move.
 */
public class Construction extends Move {

  private Block block;
  private Point target;

  /**
   * Creates a construction move.
   *
   * @param board  The board to build on.
   * @param block  Which block should be placed.
   * @param point  Where to place block on board.
   * @param forced False if the move was voluntarily initiated by a player,
   *               true if this is the result of some card effect.
   */
  public Construction(Board board, Block block, Point point, boolean forced) {
    super(board, forced);
    this.block = block;
    target = point;
  }

  /**
   * Creates a non-forced construction move.
   *
   * @param board The board to build on.
   * @param block Which block should be placed.
   * @param point Where to place block on board.
   */
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
    Item targetItem = board.getBox(target).getItems().peek();

    if (targetItem != null && targetItem.canBeRemoved()) {
      board.getBox(target).removeTopmost();
      board.place(block, target);
      board.place(targetItem, target);
    } else {
      board.place(block, target);
    }
  }

  public Block getBlock() {
    return block;
  }

  public Point getTarget() {
    return target;
  }
}
