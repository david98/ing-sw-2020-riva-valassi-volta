package it.polimi.vovarini.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PointTests {

  @Test
  @DisplayName("Test that a Point can be instantiated correctly")
  void pointCreation() {
    Point point = new Point(2, 3);
    assertEquals(2, point.getX());
    assertEquals(3, point.getY());
  }

  @Test
  @DisplayName("Test that the equals method overridden by Point works")
  void pointEquals() {
    Point point1 = new Point(0, 0);
    Point point2 = new Point(0, 0);

    assertEquals(point1, point2);
  }
}
