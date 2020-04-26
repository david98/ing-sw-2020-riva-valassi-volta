package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AtlasTests {

    Game game;

    @BeforeEach
    public void createAtlasItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard atlas = GodCardFactory.create(GodName.Atlas);
        game.getCurrentPlayer().setGodCard(atlas);
        atlas.setGame(game);
    }

    private static Stream<Arguments> provideAllPossibleConstruction() {
        LinkedList<Arguments> args = new LinkedList<>();

        for (int lStart = 0; lStart < Block.MAX_LEVEL; lStart++) {
            for (int lTarget = 0; lTarget < Block.MAX_LEVEL; lTarget++) {
                args.add(Arguments.of(lStart, lTarget));
            }
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Atlas can be instantiated correctly")
    public void atlasCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Atlas);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleConstruction")
    @DisplayName("Test a valid construction with a GodCard of type Atlas")
    public void validConstruction(int startLevel, int targetLevel) {

        GodCard atlas = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
        Board board = game.getBoard();
        Point start = new Point(0, 0);
        Point target = new Point(0, 1);

        for (int i = 0; i < startLevel; i++) {
            try {
                board.place(Block.blocks[i], start);
            } catch (InvalidPositionException | BoxFullException e) {
                e.printStackTrace();
            }
        }
        try {
            board.place(currentWorker, start);
        } catch (InvalidPositionException | BoxFullException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < targetLevel; i++) {
            try {
                board.place(Block.blocks[i], target);
            } catch (InvalidPositionException | BoxFullException e) {
                e.printStackTrace();
            }
        }

        try {
            Construction construction = new Construction(board, Block.blocks[3], target);
            assertTrue(atlas.validate(atlas.computeBuildablePoints(), construction));
            game.performMove(construction);
            assertEquals(Block.blocks[3], board.getBox(target).getItems().peek());
        } catch (BoxEmptyException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test an invalid construction with a GodCard of type Atlas")
    /** status
     *  startBox: lv 0 + currentWorker
     *  targetBox: lv 4 + free
     */
    public void invalidConstructionTargetLevel() {
        GodCard atlas = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();
        Point start = new Point(0, 0);
        Point target = new Point(0, 1);

        try {
            board.place(currentWorker, start);
            board.place(Block.blocks[0], target);
            board.place(Block.blocks[1], target);
            board.place(Block.blocks[2], target);
            board.place(Block.blocks[3], target);

            Construction construction = new Construction(board, Block.blocks[3], target);
            assertFalse(atlas.validate(atlas.computeBuildablePoints(), construction));

        } catch (BoxFullException ignored) {
        } catch (InvalidPositionException ignored) {
        }
    }

    @Test
    @DisplayName("Test an invalid construction with a GodCard of type Atlas")
    /** status
     *  startBox: lv 0 + currentWorker
     *  targetBox: lv 0 + enemy's Worker
     */
    public void invalidConstructionTargetOccupied() {
        GodCard atlas = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
        Player enemyPlayer = game.getPlayers()[1];
        Worker enemyWorker = enemyPlayer.getCurrentWorker();

        Board board = game.getBoard();
        Point start = new Point(0, 0);
        Point target = new Point(0, 1);

        try {
            board.place(currentWorker, start);
            board.place(enemyWorker, target);

            Construction construction = new Construction(board, Block.blocks[3], target);
            assertFalse(atlas.validate(atlas.computeBuildablePoints(), construction));

        } catch (BoxFullException ignored) {
        } catch (InvalidPositionException ignored) {
        }
    }
}
