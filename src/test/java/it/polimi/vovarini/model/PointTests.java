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
}
