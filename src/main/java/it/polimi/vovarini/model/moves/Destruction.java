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

  /**
   * Builds a Destruction move
   * @param board the board where the game is played and the items are placed
   * @param block the block you want to destroy
   * @param point the coordinates of the Box containing the block you want to destroy
   * @param forced true if the system forces the move, false otherwise (user move)
   */
  public Destruction(Board board, Block block, Point point, boolean forced) {
    super(board, forced);
    this.block = block;
    target = point;
  }

  /**
   * Builds a Destruction, non-forced move
   * @param board the board where the game is played and the items are placed
   * @param block the block you want to destroy
   * @param point the coordinates of the Box containing the block you want to destroy
   */
  public Destruction(Board board, Block block, Point point) {
    super(board, false);
    this.block = block;
    target = point;
  }

  /**
   * Creates a Construction move undoing the effects of the Destruction move
   * @return a Construction move with the exact reverse effect of the Destruction move
   */
  @Override
  public Move reverse() {
    return new Construction(board, block, target, forced);
  }

  /**
   * This method plainly executes in a physical way the Construction on the board
   */
  @Override
  public void execute() {
  }

  /**
   * Getter method for the block of the Destruction move
   * @return the block you want to destroy with the Destruction
   */
  public Block getBlock() {
    return block;
  }

  /**
   * Getter method for the coordinates of the Box you want to destroy upon
   * @return a Point representing the coordinates of the Box you want to destroy upon
   */
  public Point getTarget() {
    return target;
  }

}