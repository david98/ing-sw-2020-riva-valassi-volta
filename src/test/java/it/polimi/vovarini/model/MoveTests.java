package it.polimi.vovarini.model;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Destruction;
import it.polimi.vovarini.model.moves.Move;
import it.polimi.vovarini.model.moves.Movement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveTests {

  private static Board board;

  private static EnumMap<Sex, Worker> workers;

  @BeforeAll
  private static void init() {
    Player testPlayer = new Player("test_player");
    board = new Board(Board.DEFAULT_SIZE);
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Male, new Worker(Sex.Male, testPlayer));
    workers.put(Sex.Female, new Worker(Sex.Female, testPlayer));
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
      assertTrue(board.getBox(start).getItems().isEmpty());
    } catch (BoxFullException | ItemNotFoundException ignored) {

    }
  }

  @Test
  @DisplayName("Test that the reverse method generates a reverse movement")
  void testReverse() {
    Movement okMove = new Movement(board, new Point(0, 0), new Point(1, 1), true);
    Move okRev = okMove.reverse();
    Movement rev = (Movement) okRev;

    assertEquals(okMove.getEnd(), rev.getStart());
    assertEquals(okMove.getStart(), rev.getEnd());
    assertEquals(okMove.getBoard(), rev.getBoard());
    assertEquals(okMove.isForced(), rev.isForced());

    Construction okCon = new Construction(board, new Block(Block.MIN_LEVEL), new Point(1, 1), true);
    Move okRevTwo = okCon.reverse();
    Destruction revCon = (Destruction) okRevTwo;

    assertEquals(okCon.getTarget(), revCon.getTarget());
    assertEquals(okCon.getBlock(), revCon.getBlock());
    assertEquals(okCon.getBoard(), revCon.getBoard());
    assertEquals(okCon.isForced(), revCon.isForced());

    Destruction okDes = new Destruction(board, new Block(Block.MAX_LEVEL), new Point(3, 3), true);
    Move okRevThree = okDes.reverse();
    Construction revDes = (Construction) okRevThree;

    assertEquals(okDes.getBlock(), revDes.getBlock());
    assertEquals(okDes.getTarget(), revDes.getTarget());
    assertEquals(okDes.isForced(), revDes.isForced());
    assertEquals(okDes.getBoard(), revDes.getBoard());
  }
}
