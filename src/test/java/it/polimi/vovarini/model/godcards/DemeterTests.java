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
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Construction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DemeterTests {

    Game game;

    @BeforeEach
    public void createDemeterItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard demeter = GodCardFactory.create(GodName.Demeter);
        game.getCurrentPlayer().setGodCard(demeter);
        demeter.setGame(game);
    }

    @Test
    @DisplayName("Test that a GodCard of type Demeter can be instantiated correctly")
    public void demeterCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Demeter);
    }

    private static Stream<Arguments> provideAllPossibleTarget() {
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
            for(Point target : adjacentPositions) {
                args.add(Arguments.of(start, target));
            }
        }

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleTarget")
    @DisplayName("Test an invalid construction with a GodCard of type Demeter")
    public void invalidConstructionPreviousTarget(Point start, Point target) {
        GodCard demeter = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();

        try {
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(demeter.computeNextPhase(game));
        game.setCurrentPhase(demeter.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[0], target);
        assertTrue(demeter.validate(demeter.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(demeter.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction invalidConstruction = new Construction(board, Block.blocks[1], target);
        assertFalse(demeter.validate(demeter.computeBuildablePoints(), invalidConstruction));
    }

    @Test
    @DisplayName("Test an invalid construction with a GodCard of type Demeter")
    // a questo punto, probabilmente inutile
    public void secondConstructionIgnored() {
        GodCard demeter = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();
        Point start = new Point(0, 0);

        // riempio tutti i box adiacenti al worker (tranne uno!) fino alla cupola
        try {
            board.place(currentWorker, start);

            List buildablePoints = demeter.computeBuildablePoints();

            for(int i = 0; i < buildablePoints.size() -1 ; i++) {
                for(int k = 0; k < Block.MAX_LEVEL; k++) {
                    board.place(Block.blocks[k], (Point) buildablePoints.get(i));
                }
            }
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        // l'unico punto dove il worker può costruire, gli altri Box sono pieni fino alla cupola
        Point target = demeter.computeBuildablePoints().get(0);

        game.setCurrentPhase(demeter.computeNextPhase(game));
        game.setCurrentPhase(demeter.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[0], target);
        assertTrue(demeter.validate(demeter.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        // dopo la prima costruzione, non mi è possibile effettuare la seconda costruzione, perchè l'unica
        // cella dovo posso costruire è bloccata dal vincolo di Demeter (stessa cella della prima costruzione)
        // però vado lo stesso in fase di costruzione, ci penserà qualcun altro a mandarmi in fase end
        game.setCurrentPhase(demeter.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        // non arriverà mai una mossa, ma in ogni caso, quest'ultima non verrà convalidata
        Construction invalidConstruction = new Construction(board, Block.blocks[1], target);
        assertFalse(demeter.validate(demeter.computeBuildablePoints(), invalidConstruction));
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleTarget")
    @DisplayName("Test a valid construction with a GodCard of type Demeter")
    public void validSecondConstruction(Point start, Point target) {
        GodCard demeter = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();

        try {
            board.place(currentWorker, start);

            game.setCurrentPhase(demeter.computeNextPhase(game));
            game.setCurrentPhase(demeter.computeNextPhase(game));
            assertEquals(game.getCurrentPhase(), Phase.Construction);

            Construction firstConstruction = new Construction(board, Block.blocks[0], target);
            assertTrue(demeter.validate(demeter.computeBuildablePoints(), firstConstruction));
            game.performMove(firstConstruction);

            game.setCurrentPhase(demeter.computeNextPhase(game));
            assertEquals(game.getCurrentPhase(), Phase.Construction);

            List<Point> adjacentPoints = board.getAdjacentPositions(start);

            for(Point secondTarget : adjacentPoints) {
                if(!secondTarget.equals(target)) {
                    Construction validConstruction = new Construction(board, Block.blocks[0], secondTarget);
                    assertTrue(demeter.validate(demeter.computeBuildablePoints(), validConstruction));
                }
            }

        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }
    }

}
