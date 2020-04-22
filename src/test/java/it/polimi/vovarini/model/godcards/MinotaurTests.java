package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Movement;
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
    @DisplayName("Test an invalid movement with a GodCard of type Minotaur")
    /** status:
     *  startBox: lv 2 + currentWorker
     *  endBox: lv 0 + otherWorker of currentPlayer
     *  forcedDestinationBox: lv 0 + free
     */
    public void invalidMovementOtherWorker() {

        try {
            GodCard minotaur = game.getCurrentPlayer().getGodCard();
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

            Board board = game.getBoard();
            Point start = new Point(0, 0);
            Point end = new Point(1, 1);
            Movement movement = new Movement(board, start, end);

            board.place(Block.blocks[0], start);
            board.place(Block.blocks[1], start);
            board.place(currentWorker, start);
            board.place(otherWorker, end);

            if (game.validateMove(movement)) {
                List<Movement> movementList = minotaur.listEffects(movement);
                for (Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertFalse(ReachabilityDecider.isPointReachableConditionedExchange(game, end));
            assertEquals(currentWorker, board.getBox(start).getItems().peek());
            assertEquals(otherWorker, board.getBox(end).getItems().peek());

        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        } catch (InvalidPositionException ignored) {
        }
    }

    @Test
    @DisplayName("Test an invalid movement with a GodCard of type Minotaur")
    /** status
     *  startBox: lv 2 + currentWorker
     *  endBox: lv 0 + enemy's Worker
     *  forcedDestinationBox: lv 0 + otherWorker of currentPlayer
     */
    public void invalidMovementForcedDestinationOccupied() {

        try {
            GodCard minotaur = game.getCurrentPlayer().getGodCard();
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Worker otherWorker = game.getCurrentPlayer().getOtherWorker();
            Player enemysPlayer = game.getPlayers()[1];
            Worker enemysWorker = enemysPlayer.getCurrentWorker();

            Board board = game.getBoard();
            Point start = new Point(0, 0);
            Point end = new Point(1, 1);
            Point forcedDestination = new Point(2,2);
            Movement movement = new Movement(board, start, end);

            board.place(Block.blocks[0], start);
            board.place(Block.blocks[1], start);
            board.place(currentWorker, start);
            board.place(enemysWorker, end);
            board.place(otherWorker, forcedDestination);

            if(game.validateMove(movement)) {
                List<Movement> movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertFalse(ReachabilityDecider.isPointReachableConditionedExchange(game, end));
            assertEquals(currentWorker, board.getBox(start).getItems().peek());
            assertEquals(enemysWorker, board.getBox(end).getItems().peek());
            assertEquals(otherWorker, board.getBox(forcedDestination).getItems().peek());

        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        } catch (InvalidPositionException ignored) {
        }
    }

    @Test
    @DisplayName("Test an invalid movement with a GodCard of type Minotaur")
    /** status
     *  startBox: lv 1 + currentWorker
     *  endBox: lv 3 + enemy's Worker
     *  forcedDestinationBox: lv 3 + free
     */
    public void invalidMovementLevel() {

        try {
            GodCard minotaur = game.getCurrentPlayer().getGodCard();
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Player enemysPlayer = game.getPlayers()[1];
            Worker enemysWorker = enemysPlayer.getCurrentWorker();

            Board board = game.getBoard();
            Point start = new Point(0, 0);
            Point end = new Point(1, 1);
            Point forcedDestination = new Point(2,2);
            Movement movement = new Movement(board, start, end);

            board.place(Block.blocks[0], start);
            board.place(currentWorker, start);
            board.place(Block.blocks[0], end);
            board.place(Block.blocks[1], end);
            board.place(Block.blocks[2], end);
            board.place(enemysWorker, end);
            board.place(Block.blocks[0], forcedDestination);
            board.place(Block.blocks[1], forcedDestination);
            board.place(Block.blocks[2], forcedDestination);

            if(game.validateMove(movement)) {
                List<Movement> movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertFalse(ReachabilityDecider.isPointReachableConditionedExchange(game, end));
            assertEquals(currentWorker, board.getBox(start).getItems().peek());
            assertEquals(enemysWorker, board.getBox(end).getItems().peek());

        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        } catch (InvalidPositionException ignored) {
        }
    }

    @Test
    @DisplayName("Test a valid movement with a GodCard of type Minotaur")
    /** status
     *  startBox: lv 2 + currentWorker
     *  endBox: lv 0 + enemy's Worker
     *  forcedDestinationBox: lv 3 + free
     */
    public void validMovement() {

        try {
            GodCard minotaur = game.getCurrentPlayer().getGodCard();
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Worker otherWorker = game.getCurrentPlayer().getOtherWorker();
            Player enemysPlayer = game.getPlayers()[1];
            Worker enemysWorker = enemysPlayer.getCurrentWorker();

            Board board = game.getBoard();
            Point start = new Point(0, 0);
            Point end = new Point(1, 1);
            Point forcedDestination = new Point(2,2);
            Movement movement = new Movement(board, start, end);

            board.place(Block.blocks[0], start);
            board.place(Block.blocks[1], start);
            board.place(currentWorker, start);
            board.place(enemysWorker, end);
            board.place(Block.blocks[0], forcedDestination);
            board.place(Block.blocks[1], forcedDestination);
            board.place(Block.blocks[2], forcedDestination);

            assertTrue(ReachabilityDecider.isPointReachableConditionedExchange(game, end));
            assertFalse(minotaur.isMovementWinning(movement));

            if(game.validateMove(movement)) {
                List<Movement> movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertEquals(currentWorker, board.getBox(end).getItems().peek());
            assertEquals(enemysWorker, board.getBox(forcedDestination).getItems().peek());

        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        } catch (InvalidPositionException ignored) {
        }
    }

    @Test
    @DisplayName("Test a valid and winning movement with a GodCard of type Minotaur")
    /** status
     *  startBox: lv 2 + currentWorker
     *  endBox: lv 3 + enemy's Worker
     *  forcedDestinationBox: lv 3 + free
     */
    public void validWinningMovement() {

        try {
            GodCard minotaur = game.getCurrentPlayer().getGodCard();
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Worker otherWorker = game.getCurrentPlayer().getOtherWorker();
            Player enemysPlayer = game.getPlayers()[1];
            Worker enemysWorker = enemysPlayer.getCurrentWorker();

            Board board = game.getBoard();
            Point start = new Point(0, 0);
            Point end = new Point(1, 1);
            Point forcedDestination = new Point(2,2);
            Movement movement = new Movement(board, start, end);

            board.place(Block.blocks[0], start);
            board.place(Block.blocks[1], start);
            board.place(currentWorker, start);
            board.place(Block.blocks[0], end);
            board.place(Block.blocks[1], end);
            board.place(Block.blocks[2], end);
            board.place(enemysWorker, end);
            board.place(Block.blocks[0], forcedDestination);
            board.place(Block.blocks[1], forcedDestination);
            board.place(Block.blocks[2], forcedDestination);

            assertTrue(ReachabilityDecider.isPointReachableConditionedExchange(game, end));
            assertTrue(minotaur.isMovementWinning(movement));

            if(game.validateMove(movement)) {
                List<Movement> movementList = minotaur.listEffects(movement);
                for(Movement m : movementList) {
                    game.performMove(m);
                }
            }

            assertEquals(currentWorker, board.getBox(end).getItems().peek());
            assertEquals(enemysWorker, board.getBox(forcedDestination).getItems().peek());

        } catch (BoxFullException ignored) {
        } catch (BoxEmptyException ignored) {
        } catch (InvalidPositionException ignored) {
        }
    }
}
