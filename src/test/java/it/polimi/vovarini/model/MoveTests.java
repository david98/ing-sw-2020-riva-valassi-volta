package it.polimi.vovarini.model;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Movement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.stream.Stream;

public class MoveTests {

  private static Board board;

  private static EnumMap<Sex, Worker> workers;

  @BeforeAll
  private static void init() {
    board = new Board(Board.DEFAULT_SIZE);
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Male, new Worker(Sex.Male));
    workers.put(Sex.Female, new Worker(Sex.Female));
  }

  @BeforeEach
  void resetBoard() {
    board = new Board(Board.DEFAULT_SIZE);
  }

  private static Stream<Arguments> provideAllPossibleTrajectories() {
    LinkedList<Point> allPoints = new LinkedList<>();
    for (int x = 0; x < board.getSize(); x++) {
      for (int y = 0; y < board.getSize(); y++) {
        allPoints.add(new Point(x, y));
      }
    }

    LinkedList<Arguments> args = new LinkedList<>();
    for (Point start : allPoints) {
      for (Point end : allPoints) {
        if (!start.equals(end)) {
          args.add(Arguments.of(start, end));
        }
      }
    }
    return args.stream();
  }

  @ParameterizedTest
  @MethodSource("provideAllPossibleTrajectories")
  @DisplayName("Test that a worker can be moved to an empty space")
  /*
   * We have to keep in mind that move does not check for its validity.
   * It is therefore possible, as an example, to move a Worker from one corner
   * of the Board to the opposite.
   * */
  void basicMovementTest(Point start, Point end) {
    try {
      board.place(workers.get(Sex.Male), start);

      Movement movement = new Movement(board, start, end);
      movement.execute();
      assertEquals(board.getItemPosition(workers.get(Sex.Male)), end);
      BoxEmptyException thrown = assertThrows(BoxEmptyException.class, () -> board.getItems(start));
    } catch (InvalidPositionException | BoxFullException | ItemNotFoundException ignored) {

    }
  }
}
