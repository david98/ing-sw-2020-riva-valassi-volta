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

  /**
   * Builds a geometrical Point
   * @param x value on the x axis for the Point
   * @param y value on the y axis for the Point
   */
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Creates a deep clone of a Point
   * @param p is the Point I want to clone
   */
  public Point(Point p) {
    x = p.getX();
    y = p.getY();
  }

  /**
   * Getter method for the x coordinate of the point
   * @return the x coordinate of the point
   */
  public int getX() {
    return x;
  }

  /**
   * Getter method for the y coordinate of the point
   * @return the y coordinate of the point
   */
  public int getY() {
    return y;
  }

  /**
   * This method checks if this point is adjacent to another point
   * @param other the other point
   * @return true if the two points are adjacent, false otherwise
   */
  public boolean isAdjacent(Point other) {
    return !equals(other) && Math.abs(x - other.x) <= 1 && Math.abs(y - other.y) <= 1;
  }

  /**
   * ToString method of the Point class
   * @return a string representing this Point
   */
  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  /**
   * equals method of the Point class
   * @param obj an object to compare with this Point
   * @return true if obj is an instance of Point and has the same coordinates of this Point, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      Point other = (Point) obj;
      return x == other.getX() && y == other.getY();
    } else {
      return super.equals(obj);
    }
  }

  /**
   * hashCode method for the Point clas
   * @return an hashcode representing this Point
   */
  @Override
  public int hashCode() {
    return x * 17 + y;
  }

  /**
   * This method returns a random point in the board
   * @param xBound the maximum value of the x coordinate
   * @param yBound the maximum value of the y coordinate
   * @return a random point in the board respecting the bounds passed as parameters
   */
  public static Point random(int xBound, int yBound) {
    return new Point(random.nextInt(xBound), random.nextInt(yBound));
  }

  /**
   * This method checks if a point is part of the perimeter of the board
   * @return true if the point is a part of the perimeter, false otherwise
   */
  public boolean isPerimeterSpace() {
    return x == 0 || x == Board.DEFAULT_SIZE - 1 || y == 0 || y == Board.DEFAULT_SIZE - 1;
  }
}
