package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.moves.Movement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TritonTests {
  private Game game;
  private GodCard triton;

  @BeforeEach
  public void init() {
    try {
      game = new Game(2);

      game.addPlayer("Guest01");
      game.addPlayer("Guest02");

      triton = GodCardFactory.create(GodName.Triton);
      triton.setGameData(game);
      for (Player player : game.getPlayers()) {
        player.setGodCard(triton);
      }
    } catch (InvalidNumberOfPlayersException e) {
      e.printStackTrace();
    }
  }

  private static Stream<Arguments> provideAllPossibleMovementMoves() {

    LinkedList<Arguments> args = new LinkedList<>();

    Board board = new Board(Board.DEFAULT_SIZE);
    LinkedList<Point> allPoints = new LinkedList<>();

    for (int x = 0; x < board.getSize(); x++) {
      for (int y = 0; y < board.getSize(); y++) {
        allPoints.add(new Point(x, y));
      }
    }

    for (Point start : allPoints) {
      List<Point> startAdjacentPositions = board.getAdjacentPositions(start);
      for (Point endStart : startAdjacentPositions) {
        List<Point> endStartAdjacentPositions = board.getAdjacentPositions(endStart);
        for (Point secondEnd : endStartAdjacentPositions) {
          args.add(Arguments.of(start, endStart, secondEnd));
        }
      }
    }

    return args.stream();
  }

  @Test
  @DisplayName("Test that a GodCard of type Triton can be instantiated correctly")
  public void tritonCreation() {
    assertEquals(GodName.Triton, game.getCurrentPlayer().getGodCard().name);
  }

  @ParameterizedTest
  @MethodSource("provideAllPossibleMovementMoves")
  @DisplayName("Test that Triton's movement conditions are correctly applied")
  public void testMovementConstraint(Point start, Point endStart, Point secondEnd) {

    Board board = game.getBoard();

    try {
      board.place(game.getCurrentPlayer().getCurrentWorker(), start);
    } catch (InvalidPositionException | BoxFullException ignored) {
    }

    game.setCurrentPhase(Phase.Start);
    game.getCurrentPlayer().setWorkerSelected(true);
    game.setCurrentPhase(triton.computeNextPhase(game));
    assertEquals(Phase.Movement, game.getCurrentPhase());

    Movement firstMovement = new Movement(board, start, endStart);
    assertTrue(triton.validate(triton.computeReachablePoints(), firstMovement));
    game.performMove(firstMovement);
    game.setCurrentPhase(triton.computeNextPhase(game));

    if (endStart.isPerimeterSpace()) {
      assertEquals(Phase.Movement, game.getCurrentPhase());

      Movement secondMovement = new Movement(board, endStart, secondEnd);
      assertTrue(triton.validate(triton.computeReachablePoints(), secondMovement));
      game.performMove(secondMovement);

      game.setCurrentPhase(triton.computeNextPhase(game));

      if (secondEnd.isPerimeterSpace()) {
        assertEquals(Phase.Movement, game.getCurrentPhase());
      } else {
        assertEquals(Phase.Construction, game.getCurrentPhase());
      }
    } else {
      assertEquals(Phase.Construction, game.getCurrentPhase());
    }
  }
}