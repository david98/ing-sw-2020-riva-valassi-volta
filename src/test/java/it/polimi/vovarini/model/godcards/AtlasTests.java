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
import org.junit.jupiter.api.BeforeAll;
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

    private static Game game;
    private static GodCard atlas;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            atlas = GodCardFactory.create(GodName.Atlas);
            atlas.setGame(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(atlas);
            }
        } catch (InvalidNumberOfPlayersException e){
            e.printStackTrace();;
        }
    }

    @BeforeEach
    private void resetGame(){
        Board b = game.getBoard();
        for (int x = 0; x < b.getSize(); x++){
            for (int y = 0; y < b.getSize(); y++){
                Point cur = new Point(x, y);
                while (true){
                    try {
                        b.remove(cur);
                    } catch (BoxEmptyException | InvalidPositionException e){
                        break;
                    }
                }
            }
        }
    }

    private static Stream<Arguments> provideAllPossibleConstruction() {
        LinkedList<Arguments> args = new LinkedList<>();

        for (int lStart = 0; lStart < Block.MAX_LEVEL; lStart++) {
            for (int lTarget = 0; lTarget <= Block.MAX_LEVEL; lTarget++) {
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
    @DisplayName("Test that Atlas' validation rules are correctly applied")
    public void testValidationCondition(int startLevel, int targetLevel) {

        Board board = game.getBoard();
        Point start = new Point(0, 1);
        Point target = new Point(0, 0);

        for (int i = 0; i < startLevel; i++) {
            try {
                board.place(Block.blocks[i], start);
            } catch (InvalidPositionException | BoxFullException e) {
                e.printStackTrace();
            }
        }
        try {
            board.place(game.getCurrentPlayer().getCurrentWorker(), start);
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

        Construction construction = new Construction(board, Block.blocks[3], target);
        assertEquals(Block.MAX_LEVEL != targetLevel, atlas.validate(atlas.computeBuildablePoints(), construction));
    }

    @Test
    @DisplayName("Test an invalid construction with a GodCard of type Atlas")
    /** status
     *  startBox: lv 0 + currentWorker
     *  targetBox: lv 0 + enemy's Worker
     */
    public void invalidConstructionTargetOccupied() {
        Worker enemyWorker = game.getPlayers()[1].getCurrentWorker();
        Board board = game.getBoard();
        Point start = new Point(0, 1);
        Point target = new Point(0, 0);

        try {
            board.place(game.getCurrentPlayer().getCurrentWorker(), start);
            board.place(enemyWorker, target);
        } catch (BoxFullException | InvalidPositionException e) {
            e.printStackTrace();
        }

        Construction construction = new Construction(board, Block.blocks[3], target);
        assertFalse(atlas.validate(atlas.computeBuildablePoints(), construction));
    }
}
