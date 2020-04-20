package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Board Tests")
public class BoardTests {

  private static Board board;
  private static Block minLevelBlock;

  @BeforeAll
  public static void init() {
    board = new Board(Board.DEFAULT_SIZE);
    try {
      minLevelBlock = new Block(Block.MIN_LEVEL);
    } catch (InvalidLevelException ignored) {

    }
  }

  @BeforeEach
  private void resetBoard() {
    board = new Board(Board.DEFAULT_SIZE);
  }

  @Test
  @DisplayName("Test that a Board can be instantiated correctly")
  void boardCreation() {
    assertEquals(Board.DEFAULT_SIZE, board.getSize());
  }

  private static List<Point> provideAllPoints() {
    LinkedList<Point> points = new LinkedList<>();
    for (int x = 0; x < board.getSize(); x++) {
      for (int y = 0; y < board.getSize(); y++) {
        points.add(new Point(x, y));
      }
    }
    return points;
  }

  private static List<Point> provideInvalidPoints() {
    LinkedList<Point> invalidPoints = new LinkedList<>();
    invalidPoints.add(new Point(-1, -1)); // top left corner
    invalidPoints.add(new Point(-1, 0));
    invalidPoints.add(new Point(0, -1));
    invalidPoints.add(new Point(board.getSize(), board.getSize())); // bottom right corner
    invalidPoints.add(new Point(board.getSize(), board.getSize() - 1));
    invalidPoints.add(new Point(board.getSize() - 1, board.getSize()));
    invalidPoints.add(new Point(-1, board.getSize())); // bottom left corner
    invalidPoints.add(new Point(-1, board.getSize() - 1));
    invalidPoints.add(new Point(0, board.getSize()));
    invalidPoints.add(new Point(board.getSize(), -1)); // top right corner
    invalidPoints.add(new Point(board.getSize() - 1, -1));
    invalidPoints.add(new Point(board.getSize(), 0));

    return invalidPoints;
  }

  @ParameterizedTest
  @MethodSource("provideAllPoints")
  @DisplayName("Test that adjacent points are computed correctly")
  void adjacentPoints(Point p) {
    List<Point> adjPointsList = board.getAdjacentPositions(p);
    for (Point point : adjPointsList) {
      int distance = Math.abs(p.getX() - point.getX()) + Math.abs(p.getY() - point.getY());
      assertTrue(
          distance > 0
              && distance < 3
              && point.getX() >= 0
              && point.getY() >= 0
              && point.getX() < board.getSize()
              && point.getY() < board.getSize());
    }
  }

  @ParameterizedTest
  @MethodSource("provideInvalidPoints")
  @DisplayName("Test that isPositionValid works")
  void validPosition(Point p) {
    assertFalse(board.isPositionValid(p));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidPoints")
  @DisplayName(
      "Test that place throws an InvalidPositionException when a Point is outside of the Board")
  void placeInvalidPoint(Point p) {
    InvalidPositionException e =
        assertThrows(
            InvalidPositionException.class,
            () -> board.place(minLevelBlock, p),
            "Expected place to throw InvalidPositionException, but it didn't");
  }

  @ParameterizedTest
  @MethodSource("provideAllPoints")
  @DisplayName("Test that getItemPosition returns the actual Point where the Item has been placed")
  void placeAndCheckPosition(Point p) {
    assertDoesNotThrow(() -> board.place(minLevelBlock, p));
    assertDoesNotThrow(() -> assertEquals(p, board.getItemPosition(minLevelBlock)));
  }

  @Test
  @DisplayName("Tests that the clone() method actually returns a deep copy")
  void cloneWorks(){
    Board b = new Board(Board.DEFAULT_SIZE);
    try {
      b.place(Block.blocks[0], new Point(0, 0));
      Board b2 = b.clone();
      b2.remove(new Point(0,0));
      assertDoesNotThrow(() -> b.getItems(new Point(0, 0)));
    } catch (InvalidPositionException ignored){
    } catch (BoxFullException ignored){
    } catch (BoxEmptyException ignored){
    }

  }
}
