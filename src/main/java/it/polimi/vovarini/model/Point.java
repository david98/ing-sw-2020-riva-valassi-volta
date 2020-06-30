package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;

import java.io.Serializable;
import java.util.Random;

/**
 * This class represents a generic point in the board, representing a {@link it.polimi.vovarini.model.board.Box} with coordinates
 */
public class Point implements Serializable {
  private final int x;

  private final int y;

  private final static Random random = new Random();

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Point(Point p) {
    x = p.getX();
    y = p.getY();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isAdjacent(Point other) {
    return !equals(other) && Math.abs(x - other.x) <= 1 && Math.abs(y - other.y) <= 1;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      Point other = (Point) obj;
      return x == other.getX() && y == other.getY();
    } else {
      return super.equals(obj);
    }
  }

  @Override
  public int hashCode() {
    return x * 17 + y;
  }

  public static Point random(int xBound, int yBound) {
    return new Point(random.nextInt(xBound), random.nextInt(yBound));
  }

  public boolean isPerimeterSpace() {
    return x == 0 || x == Board.DEFAULT_SIZE - 1 || y == 0 || y == Board.DEFAULT_SIZE - 1;
  }
}
