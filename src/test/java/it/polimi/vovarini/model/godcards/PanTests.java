package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.moves.Movement;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PanTests {

    Game game;

    @BeforeEach
    public void createPanItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }

        GodCard pan = GodCardFactory.create(GodName.Pan);
        game.getCurrentPlayer().setGodCard(pan);
    }

    @Test
    @DisplayName("Test that a GodCard of type Pan can be instantiated correctly")
    public void panCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Pan);
    }

    @Test
    @DisplayName("Test a movementWinning with a GodCard of type Pan")
    public void panMovementWinning() {
        GodCard pan = game.getCurrentPlayer().getGodCard();
        Board board = game.getBoard();
        Point start = new Point(0,0);
        Point end = new Point (1,1);
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        // from 1 to 0
        try {
            board.place(Block.blocks[0], start);
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        assertFalse(pan.isMovementWinning(new Movement(board, start, end)));

        // from 2 to 0
        try {
            currentWorker = (Worker) board.remove(start);
            board.place(Block.blocks[1], start);
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        }

        assertTrue(pan.isMovementWinning(new Movement(board, start, end)));

        // from 3 to 0
        try {
            currentWorker = (Worker) board.remove(start);
            board.place(Block.blocks[2], start);
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        }

        assertTrue(pan.isMovementWinning(new Movement(board, start, end)));

        // from 3 to 1
        try {
            board.place(Block.blocks[0], end);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        assertTrue(pan.isMovementWinning(new Movement(board, start, end)));

        // from 3 to 2
        try {
            board.place(Block.blocks[1], end);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        assertFalse(pan.isMovementWinning(new Movement(board, start, end)));

        // from 2 to 3 (general rules)
        try {
            currentWorker = (Worker) board.remove(start);
            board.remove(start);
            board.place(currentWorker, start);
            board.place(Block.blocks[2], end);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        }

        assertTrue(pan.isMovementWinning(new Movement(board, start, end)));
    }


}
