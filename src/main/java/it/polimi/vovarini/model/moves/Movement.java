package it.polimi.vovarini.model.moves;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Item;

public class Movement extends Move {

  private final Point start;
  private final Point end;

  /**
   * Constructor method of Movement
   * @param board Instance of board currently in use
   * @param start Worker starting Point
   * @param end Point that the worker wants to reach
   * @param forced Boolean value stating if the movementMove is forced by the ability of a GodCard
   */
  public Movement(Board board, Point start, Point end, boolean forced) {
    super(board, forced);
    this.start = new Point(start);
    this.end = new Point(end);
  }

  /**
   * Constructor method of Movement without forced assignment (forced is false by default)
   * @param board Instance of board currently in use
   * @param start Worker starting Point
   * @param end Point that the worker wants to reach
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
  public void execute() {
    try {
      Item startItem = board.remove(start);
      Item endItem = null;
      try {
        endItem = board.getItems(end).peek();
        if (endItem.canBeRemoved()) {
          board.remove(end);
          board.place(endItem, start);
        }
      } catch (BoxEmptyException ignored) {

      }
      board.place(startItem, end);
    } catch (BoxEmptyException e) {
      System.err.println("Start was empty.");
    } catch (InvalidPositionException e) {
      System.err.println("Invalid start/end");
    } catch (BoxFullException e) {
      System.err.println("End box is full.");
    }
  }

  public Point getStart() {
    return new Point(start);
  }

  public Point getEnd() {
    return new Point(end);
  }
}
