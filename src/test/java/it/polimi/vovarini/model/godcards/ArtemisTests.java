package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
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
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ArtemisTests {

    Game game;

    @BeforeEach
    public void createArtemisItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard artemis = GodCardFactory.create(GodName.Artemis);
        game.getCurrentPlayer().setGodCard(artemis);
        artemis.setGame(game);
    }

    @Test
    @DisplayName("Test that a GodCard of type Artemis can be instantiated correctly")
    public void artemisCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Artemis);
    }

    private static Stream<Arguments> provideAllPossibleTrajectorias() {
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
                args.add(Arguments.of(start, end));
            }
        }

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleTrajectorias")
    @DisplayName("Test an invalid movement with a GodCard of type Artemis")
    public void invalidMovementInitialSpace(Point start, Point end) {
        GodCard artemis = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();

        try {
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(artemis.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Movement firstMovement = new Movement(board, start, end);
        game.performMove(firstMovement);

        game.setCurrentPhase(artemis.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Movement invalidMovement = new Movement(board, end, start);
        assertFalse(artemis.validate(artemis.computeReachablePoints(), invalidMovement));
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleTrajectorias")
    @DisplayName("Test a valid movement with a GodCard of type Artemis")
    public void validSecondMovement(Point start, Point end) {
        GodCard artemis = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();

        try {
            board.place(currentWorker, start);

            game.setCurrentPhase(artemis.computeNextPhase(game));
            assertEquals(game.getCurrentPhase(), Phase.Movement);

            Movement firstMovement = new Movement(board, start, end);
            game.performMove(firstMovement);

            Point secondStart = board.getItemPosition(currentWorker);

            game.setCurrentPhase(artemis.computeNextPhase(game));
            assertEquals(game.getCurrentPhase(), Phase.Movement);

            List<Point> adjacentPoints = board.getAdjacentPositions(secondStart);

            for(Point secondEnd : adjacentPoints) {
                if(!secondEnd.equals(start)) {
                    Movement validMovement = new Movement(board, secondStart, secondEnd);
                    assertTrue(artemis.validate(artemis.computeReachablePoints(), validMovement));
                }
            }

        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        } catch (ItemNotFoundException ignored) {
        }

    }



}
