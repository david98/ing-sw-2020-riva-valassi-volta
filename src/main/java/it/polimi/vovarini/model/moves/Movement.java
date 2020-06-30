package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Item;

/**
 * This class represents a worker movement from one cell to another.
 */
public class Movement extends Move {

  private final Point start;
  private final Point end;

  /**
   * Creates a Movement. If end contains a removable item,
   * an exchange between start and end.
   *
   * @param board  Instance of board currently in use
   * @param start  Worker starting Point
   * @param end    Point that the worker wants to reach
   * @param forced False if the move was voluntarily initiated by a player,
   *               true if this is the result of some card effect.
   */
  public Movement(Board board, Point start, Point end, boolean forced) {
    super(board, forced);
    this.start = new Point(start);
    this.end = new Point(end);
  }

  /**
   * Creates a non-forced Movement. If end contains a removable item,
   * * an exchange between start and end.
   *
   * @param board The board to build on.
   * @param start Where to pick the item to be moved.
   * @param end   Where to move the item picked from start.
   */
  public Movement(Board board, Point start, Point end) {
    super(board, false);
    this.start = new Point(start);
    this.end = new Point(end);
  }

  @Override
  public Move reverse() {
    return new Movement(board, end, start, forced);
  }

  @Override
  /**
   * Executes this Movement.
   *
   * @throws RuntimeException If either start or end were empty/invalid positions.
   */
  public void execute() throws RuntimeException {
    try {
      Item startItem = board.getBox(start).removeTopmost();

      if (startItem == null) {
        throw new RuntimeException(); //invalid start
      }

      Item endItem = board.getBox(end).getItems().peek();
      if (endItem != null && endItem.canBeRemoved()) {
        board.getBox(end).removeTopmost();
        board.place(endItem, start);
      }
      board.place(startItem, end);
    } catch (InvalidPositionException e) {
      throw new RuntimeException(); //invalid start or end
    } catch (BoxFullException e) {
      throw new RuntimeException(); //end box is full
    }
  }

  public Point getStart() {
    return new Point(start);
  }

  public Point getEnd() {
    return new Point(end);
  }
}
