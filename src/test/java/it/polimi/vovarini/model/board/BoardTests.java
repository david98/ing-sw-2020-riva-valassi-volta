package it.polimi.vovarini.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.vovarini.model.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Board Points Validator")
public class BoardTests {

  @Test
  @DisplayName("Test that a Board can be instantiated correctly")
  void boardCreation() {
    Board board = new Board(Board.DEFAULT_SIZE);
    assertEquals(Board.DEFAULT_SIZE, board.getSize());
  }

  @Test
  @DisplayName("Test that every points valuated as valid by the method is adjacent")
  void adjacentPoints() {
    Board board = new Board(Board.DEFAULT_SIZE);
    for (int x = 0; x < board.getSize(); x++) {
      for (int y = 0; y < board.getSize(); y++) {
        Point current = new Point(x, y);

        List<Point> adjPointsList = board.getAdjacentPositions(current);
        for (Point point : adjPointsList) {
          int distance =
              Math.abs(current.getX() - point.getX()) + Math.abs(current.getY() - point.getY());
          assertTrue(
              distance > 0
                  && distance < 3
                  && point.getX() >= 0
                  && point.getY() >= 0
                  && point.getX() < board.getSize()
                  && point.getY() < board.getSize());
        }
      }
    }
  }
}
