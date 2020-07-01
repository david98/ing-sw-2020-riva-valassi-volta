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

  /**
   * Creates a reverse Destruction moves undoing the effects of the Construction move
   * @return a Destruction Move with the exact reverse effect of the Construction move
   */
  @Override
  public Move reverse() {
    return new Destruction(board, block, target, forced);
  }

  /**
   * This method plainly executes in a physical way the Construction on the board
   */
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

  /**
   * Getter method for the block of the Construction move
   * @return the block you want to build with the Construction
   */
  public Block getBlock() {
    return block;
  }

  /**
   * Getter method for the coordinates of the Box you want to build upon
   * @return a Point representing the coordinates of the Box you want to build upon
   */
  public Point getTarget() {
    return target;
  }
}
