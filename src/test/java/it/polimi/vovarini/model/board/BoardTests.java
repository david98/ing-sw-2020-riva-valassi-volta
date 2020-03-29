package it.polimi.vovarini.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.vovarini.model.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BoardTests {
  @Test
  @DisplayName("Test that a Board can be instantiated correctly")
  void boardCreation() {
    Board board = new Board(Board.DEFAULT_SIZE);
    assertEquals(Board.DEFAULT_SIZE, board.getSize());
  }

  @Test
  @DisplayName("Test that every points valuated as valid by the method is adjacent")
  void adjacentPoints(Point current){

    Board board = new Board(Board.DEFAULT_SIZE);
    List<Point> adjPointsList = board.getAdjacentPositions(current);
    for (Point point : adjPointsList){
      int distance = Math.abs( (current.getX() + current.getY()) - (point.getX() + point.getY()) );
      assertTrue(distance < 1 && !point.equals(current) && point.getX() >= 0 && point.getY() >= 0 && point.getX() <= Board.DEFAULT_SIZE && point.getY() <= Board.DEFAULT_SIZE );
    }
  }
}
