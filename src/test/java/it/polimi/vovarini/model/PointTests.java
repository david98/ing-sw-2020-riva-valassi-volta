package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointTests {

  private static Stream<Arguments> provideAllPossiblePoints() {

    LinkedList<Arguments> args = new LinkedList<>();
    Board board = new Board(Board.DEFAULT_SIZE);

    for (int x = 0; x < board.getSize(); x++)
      for (int y = 0; y < board.getSize(); y++)
        args.add(Arguments.of(new Point(x, y)));

    return args.stream();
  }

  @Test
  @DisplayName("Test that a Point can be instantiated correctly")
  void pointCreation() {
    Point point = new Point(2, 3);
    assertEquals(2, point.getX());
    assertEquals(3, point.getY());

    Point point2 = new Point(point);
    assertEquals(point, point2);

  }

  @Test
  @DisplayName("Test that the equals method overridden by Point works")
  void pointEquals() {
    Point point1 = new Point(0, 0);
    Point point2 = new Point(0, 0);

    assertEquals(point1, point2);
  }

  @Test
  @DisplayName("Test if a Point is adjacent to another one")
  void pointAdjacent() {
    Point point1 = new Point(1, 1);
    Point point2 = new Point(0, 0);

    assertTrue(point1.isAdjacent(point2));
  }

  @ParameterizedTest
  @MethodSource("provideAllPossiblePoints")
  @DisplayName("Test if a Point is on the perimeter")
  void pointPerimeter(Point point) {

    int x = point.getX();
    int y = point.getY();
    int min = 0;
    int max = Board.DEFAULT_SIZE - 1;

    boolean expected = x == min || x == max || y == min || y == max;

    assertEquals(expected, point.isPerimeterSpace());

  }


}
