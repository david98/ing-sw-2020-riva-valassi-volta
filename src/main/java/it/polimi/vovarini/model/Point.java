package it.polimi.vovarini.model;

import java.util.Random;

public class Point {
  private int x;

  private int y;

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
    return x * 10 + y;
  }

  public boolean isValidPoint() {
    return x >= 0 && x < 5 && y >= 0 && y < 5;
  }

  public static Point random(int xBound, int yBound){
    Random r = new Random();
    return new Point(r.nextInt(xBound), r.nextInt(yBound));
  }
}
