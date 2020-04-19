package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
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
  public void execute() {
    try {
      board.place(block,target);
    } catch (InvalidPositionException e) {
      System.err.println("Invalid target");
    } catch (BoxFullException e) {
      System.err.println("Target box is full.");
    }
  }

  public Block getBlock() { return block; }

  public Point getTarget() { return target; }
}
