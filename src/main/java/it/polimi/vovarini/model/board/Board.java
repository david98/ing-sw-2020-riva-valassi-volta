package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.events.BoardUpdateEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Item;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the board where the game is played.
 */
public class Board implements Serializable {

  public static final int DEFAULT_SIZE = 5;

  private final Box[][] boxes;
  private final int size;

  /**
   * Creates a square board of the given size.
   *
   * @param size The size of the board.
   */
  public Board(int size) {
    this.size = size;
    boxes = new Box[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        boxes[i][j] = new Box();
      }
    }
  }

  /**
   * Creates a board which is a clone of b.
   *
   * @param b The board to be cloned.
   */
  public Board(Board b) {
    size = b.size;
    boxes = new Box[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        boxes[i][j] = new Box(b.boxes[i][j]);
      }
    }
  }

  /**
   * Checks if p is within the board.
   *
   * @param p The point to check.
   * @return Whether p is within the board.
   */
  public boolean isPositionValid(Point p) {
    return (p.getX() >= 0 && p.getY() >= 0 && p.getX() < size && p.getY() < size);
  }

  /**
   * Computes and returns a list of all points within the board
   * that are adjacent to p (d(point, p) <= 1).
   *
   * @param p The base point.
   * @return A list containing all points adjacent to p.
   */
  public List<Point> getAdjacentPositions(Point p) {
    LinkedList<Point> adjacentPositions = new LinkedList<>();
    for (int i = p.getY() - 1; i <= p.getY() + 1; i++) {
      for (int j = p.getX() - 1; j <= p.getX() + 1; j++) {
        Point point = new Point(j, i);
        if (i >= 0 && i < size && j >= 0 && j < size && !point.equals(p)) {
          adjacentPositions.add(point);
        }
      }
    }
    return adjacentPositions;
  }

  public Box getBox(Point position) {
    return boxes[position.getY()][position.getX()];
  }

  /**
   * Places item on the board at position p, if possible.
   *
   * @param item The item to be placed on the board.
   * @param p    Where the item should be placed.
   * @throws InvalidPositionException                              If p is outside of the board.
   * @throws it.polimi.vovarini.common.exceptions.BoxFullException If the box at position p is full.
   */
  public void place(Item item, Point p) {
    if (!isPositionValid(p)) {
      throw new InvalidPositionException();
    }
    Box box = getBox(p);
    box.place(item);
    GameEventManager.raise(new BoardUpdateEvent(this, this));
  }

  /**
   * Returns the item position after searching on top of all the boxes.
   *
   * @param item The item to search for.
   * @return Where the first occurrence of item was found.
   * @throws ItemNotFoundException If item was not found.
   */
  public Point getItemPosition(Item item) {
    for (int i = 0; i < boxes.length; i++) {
      for (int j = 0; j < boxes.length; j++) {
        if (Objects.equals(boxes[j][i].getItems().peek(), item)) {
          return new Point(i, j);
        }
      }
    }
    throw new ItemNotFoundException();
  }

  public int getSize() {
    return size;
  }

}
