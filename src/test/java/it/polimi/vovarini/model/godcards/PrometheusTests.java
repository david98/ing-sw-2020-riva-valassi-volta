package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PrometheusTests {

    Game game;

    @BeforeEach
    public void createPrometheusItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard prometheus = GodCardFactory.create(GodName.Prometheus);
        game.getCurrentPlayer().setGodCard(prometheus);
        prometheus.setGame(game);
    }

    @Test
    @DisplayName("Test that a GodCard of type Prometheus can be instantiated correctly")
    public void prometheusCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Prometheus);
    }

    private static Stream<Arguments> provideAllPossibleTrajectories() {
        LinkedList<Arguments> args = new LinkedList<>();

        Board board = new Board(Board.DEFAULT_SIZE);
        LinkedList<Point> allPoints = new LinkedList<>();

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                allPoints.add(new Point(x, y));
            }
        }

        for(Point start : allPoints) {
            List<Point> adjacentPositions = board.getAdjacentPositions(start);
            for(Point end : adjacentPositions) {
                for (int level = 0; level < Block.MAX_LEVEL; level++) {
                        args.add(Arguments.of(start, end, level));
                }
            }
        }

        return args.stream();
    }

    private static Stream<Arguments> provideAllPossibleMoveUp() {
        LinkedList<Arguments> args = new LinkedList<>();

        Board board = new Board(Board.DEFAULT_SIZE);
        LinkedList<Point> allPoints = new LinkedList<>();

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                allPoints.add(new Point(x, y));
            }
        }

        for(Point start : allPoints) {
            List<Point> adjacentPositions = board.getAdjacentPositions(start);
            for(Point end : adjacentPositions) {
                for (int endLevel = 1; endLevel < Block.MAX_LEVEL; endLevel++) {
                    for(int startLevel = endLevel-1; startLevel < endLevel; startLevel++) {
                        args.add(Arguments.of(start, end, startLevel, endLevel));
                    }
                }
            }
        }

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleTrajectories")
    @DisplayName("Test an invalid movement after applying the malus of the GodCard Prometheus")
    public void invalidMoveUp(Point start, Point target, int level) {
        Player currentPlayer = game.getCurrentPlayer();
        GodCard prometheus = currentPlayer.getGodCard();
        Worker currentWorker = currentPlayer.getCurrentWorker();

        Board board = game.getBoard();

        try {
            for(int i = 0; i < level; i++) {
                board.place(Block.blocks[i], start);
                board.place(Block.blocks[i], target);
            }
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[level], target);
        assertTrue(prometheus.validate(prometheus.computeReachablePoints(),firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Point end = target;
        Movement invalidMovement = new Movement(board, start, end);
        assertFalse(prometheus.validate(prometheus.computeReachablePoints(),invalidMovement));
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMoveUp")
    @DisplayName("Test that the player can move up if Prometheus does not unleash his power")
    public void validMoveUp(Point start, Point end, int lStart, int lEnd) {
        Player currentPlayer = game.getCurrentPlayer();
        GodCard prometheus = currentPlayer.getGodCard();
        Worker currentWorker = currentPlayer.getCurrentWorker();

        Board board = game.getBoard();

        try {
            for(int i = 0; i < lStart; i++) {
                board.place(Block.blocks[i], start);
            }
            board.place(currentWorker, start);

            for(int i = 0; i < lEnd; i++) {
                board.place(Block.blocks[i], end);
            }
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Movement validMoveUp = new Movement(board, start, end);
        assertTrue(prometheus.validate(prometheus.computeReachablePoints(),validMoveUp));
        game.performMove(validMoveUp);

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

    }



}
