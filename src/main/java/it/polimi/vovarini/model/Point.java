package it.polimi.vovarini.model;

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
}
