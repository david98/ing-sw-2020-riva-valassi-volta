package it.polimi.vovarini.model.board;

import it.polimi.vovarini.model.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTests {
  @Test
  @DisplayName("Test that a Board can be instantiated correctly")
  void boardCreation() {
    Board board = new Board(Board.DEFAULT_SIZE);
    assertEquals(Board.DEFAULT_SIZE, board.getSize());
  }

  @Test
  @DisplayName("Test adjacent positions on a board")
  void boardGetAdjacentPositions() {
    Board board = new Board(Board.DEFAULT_SIZE);

    Point p1 = new Point(1, 0);
    Point p2 = new Point(0, 1);
    Point p3 = new Point(1, 1);

    Point[] pointsList = new Point[] {p1, p2, p3};

    assertArrayEquals(pointsList, board.getAdjacentPositions(new Point(0, 0)).toArray());
  }
}
