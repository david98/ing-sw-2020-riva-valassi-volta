package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
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

public class HephaestusTests {

    Game game;

    @BeforeEach
    public void createHephaestusItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard hephaestus = GodCardFactory.create(GodName.Hephaestus);
        game.getCurrentPlayer().setGodCard(hephaestus);
        hephaestus.setGame(game);
    }

    @Test
    @DisplayName("Test that a GodCard of type Hephaestus can be instantiated correctly")
    public void hephaestusCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Hephaestus);
    }

    private static Stream<Arguments> provideAllPossibleValidTarget() {
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
                args.add(Arguments.of(start, target, 0));
                args.add(Arguments.of(start, target, 1));
            }
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test an invalid construction with a GodCard of type Hephaestus")
    public void invalidSecondConstructionDome() {
        GodCard hephaestus = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();
        Point start = new Point(0, 0);
        Point target = new Point(1, 1);

        try {
            board.place(currentWorker, start);
            board.place(Block.blocks[0], target);
            board.place(Block.blocks[1], target);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[2], target);
        assertTrue(hephaestus.validate(hephaestus.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction invalidConstruction = new Construction(board, Block.blocks[3], target);
        assertFalse(hephaestus.validate(hephaestus.computeBuildablePoints(), invalidConstruction));
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleValidTarget")
    @DisplayName("Test an invalid construction with a GodCard of type Hephaestus")
    public void invalidSecondConstructionNotPreviousTarget(Point start, Point target, int lTarget) {
        GodCard hephaestus = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();

        try {
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[0], target);
        assertTrue(hephaestus.validate(hephaestus.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        // non posso costruire in posizioni che non siano il target
        List<Point> adjacentPoints = board.getAdjacentPositions(start);

        for(Point secondTarget : adjacentPoints) {
            if(!secondTarget.equals(target)) {
                Construction invalidConstruction = new Construction(board, Block.blocks[0], secondTarget);
                assertFalse(hephaestus.validate(hephaestus.computeBuildablePoints(), invalidConstruction));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleValidTarget")
    @DisplayName("Test a valid construction with a GodCard of type Hephaestus")
    public void validSecondConstruction(Point start, Point target, int lTarget) {
        GodCard hephaestus = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();

        try {
            for(int i = 0; i < lTarget; i++) {
                board.place(Block.blocks[i], target);
            }
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[lTarget], target);
        assertTrue(hephaestus.validate(hephaestus.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction secondConstruction = new Construction(board, Block.blocks[lTarget+1], target);
        assertTrue(hephaestus.validate(hephaestus.computeBuildablePoints(), secondConstruction));
    }

}
