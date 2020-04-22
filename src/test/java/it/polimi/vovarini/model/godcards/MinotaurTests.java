package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MinotaurTests {

    Game game;

    @BeforeEach
    public void createMinotaurItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard minotaur = GodCardFactory.create(GodName.Minotaur);
        game.getCurrentPlayer().setGodCard(minotaur);
        minotaur.setGame(game);
    }

    @Test
    @DisplayName("Test that a GodCard of type Minotaur can be instantiated correctly")
    public void minotaurCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Minotaur);
    }

    @Test
    @DisplayName("Test a movement with a GodCard of type Minotaur")
    public void minotaurMovement() {

        GodCard minotaur = game.getCurrentPlayer().getGodCard();
        game.getCurrentPlayer().setCurrentSex(Sex.Female);
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
        Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

        Player enemysPlayer = game.getPlayers()[1];
        Worker enemysWorker = enemysPlayer.getCurrentWorker();

        Board board = game.getBoard();
        Point start = new Point(0,0);
        Point end = new Point (1,1);
        Point forcedDestination = new Point(2,2);
        Movement movement = new Movement(board, start, end);
        List<Movement> movementList;

        try {
            /* invalid move
             * start: lv 2 + currentWorker
             * end: lv 0 + otherWorker of currentPlayer
             * forcedDestination: lv 0 + free
             */
            board.place(Block.blocks[0], start);
            board.place(Block.blocks[1], start);
            board.place(currentWorker, start);
            board.place(otherWorker, end);

            if(game.validateMove(movement)) {
                movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertFalse(Reachability.isPointReachableConditionedExchange(game, end));
            assertEquals(currentWorker, board.getBox(start).getItems().peek());
            assertEquals(otherWorker, board.getBox(end).getItems().peek());


            /* invalid move
             * start: lv 2 + currentWorker
             * end: lv 0 + enemy's Worker
             * forcedDestination: lv 0 + otherWorker of currentPlayer
             */
            board.remove(end);
            board.place(enemysWorker, end);
            board.place(otherWorker, forcedDestination);

            if(game.validateMove(movement)) {
                movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertFalse(Reachability.isPointReachableConditionedExchange(game, end));
            assertEquals(currentWorker, board.getBox(start).getItems().peek());
            assertEquals(enemysWorker, board.getBox(end).getItems().peek());
            assertEquals(otherWorker, board.getBox(forcedDestination).getItems().peek());


            /* valid move
             * start: lv 2 + currentWorker
             * end: lv 0 + enemy's Worker
             * forcedDestination: lv 3 + free
             */
            board.remove(forcedDestination);
            board.place(Block.blocks[0], forcedDestination);
            board.place(Block.blocks[1], forcedDestination);
            board.place(Block.blocks[2], forcedDestination);

            assertTrue(Reachability.isPointReachableConditionedExchange(game, end));
            assertFalse(minotaur.isMovementWinning(movement));

            if(game.validateMove(movement)) {
                movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertEquals(currentWorker, board.getBox(end).getItems().peek());
            assertEquals(enemysWorker, board.getBox(forcedDestination).getItems().peek());


            /* valid & winning move
             * start: lv 2 + currentWorker
             * end: lv 3 + enemy's Worker
             * forcedDestination: lv 3 + free
             */
            board.remove(end);
            board.place(currentWorker, start);
            board.place(Block.blocks[0], end);
            board.place(Block.blocks[1], end);
            board.place(Block.blocks[2], end);
            board.remove(forcedDestination);
            board.place(enemysWorker, end);

            assertTrue(Reachability.isPointReachableConditionedExchange(game, end));
            assertTrue(minotaur.isMovementWinning(movement));

            if(game.validateMove(movement)) {
                movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertEquals(currentWorker, board.getBox(end).getItems().peek());
            assertEquals(enemysWorker, board.getBox(forcedDestination).getItems().peek());


            /* invalid move
             * start: lv 1 + currentWorker
             * end: lv 3 + enemy's Worker
             * forcedDestination: lv 3 + free
             */
            board.remove(start);
            board.remove(end);
            board.remove(forcedDestination);
            board.place(currentWorker, start);
            board.place(enemysWorker, end);

            if(game.validateMove(movement)) {
                movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertFalse(Reachability.isPointReachableConditionedExchange(game, end));
            assertEquals(currentWorker, board.getBox(start).getItems().peek());
            assertEquals(enemysWorker, board.getBox(end).getItems().peek());

        } catch (BoxFullException ignored) {
        } catch (InvalidPositionException ignored) {
        } catch (BoxEmptyException ignored) {
        }
    }
}
