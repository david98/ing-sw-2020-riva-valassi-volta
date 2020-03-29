package it.polimi.vovarini.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.vovarini.model.Point;
<<<<<<< HEAD
import it.polimi.vovarini.model.CriticPointsParameterResolver;
=======
>>>>>>> ee41b5e56d7bd38089cc03bd7d86c1c04c52460d
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@DisplayName("Board Points Validator")
@ExtendWith(CriticPointsParameterResolver.class)
public class BoardTests {


  @Test
  @DisplayName("Test that a Board can be instantiated correctly")
  void boardCreation() {
    Board board = new Board(Board.DEFAULT_SIZE);
    assertEquals(Board.DEFAULT_SIZE, board.getSize());
  }

  @Test
  @DisplayName("Test that every points valuated as valid by the method is adjacent")
<<<<<<< HEAD
  void adjacentPoints(Point current){
    Board board = new Board(Board.DEFAULT_SIZE);
    List<Point> adjPointsList = board.getAdjacentPositions(current);
    for (Point point : adjPointsList){
      double distance = Math.sqrt(Math.pow(point.getX() - current.getX(), 2) + Math.pow(point.getY() - current.getY(), 2));
      assertTrue((distance == 1 || distance == Math.sqrt(2)) && !point.equals(current) && point.getX() >= 0 && point.getY() >= 0 && point.getX() < Board.DEFAULT_SIZE && point.getY() < Board.DEFAULT_SIZE );
=======
  void adjacentPoints(Point current) {

    Board board = new Board(Board.DEFAULT_SIZE);
    List<Point> adjPointsList = board.getAdjacentPositions(current);
    for (Point point : adjPointsList) {
      int distance = Math.abs((current.getX() + current.getY()) - (point.getX() + point.getY()));
      assertTrue(
          distance < 1
              && !point.equals(current)
              && point.getX() >= 0
              && point.getY() >= 0
              && point.getX() <= Board.DEFAULT_SIZE
              && point.getY() <= Board.DEFAULT_SIZE);
>>>>>>> ee41b5e56d7bd38089cc03bd7d86c1c04c52460d
    }
  }
}
