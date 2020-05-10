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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ZeusTests {

    private static Game game;
    private static GodCard zeus;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            zeus = GodCardFactory.create(GodName.Zeus);
            zeus.setGameData(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(zeus);
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

        for (int level = 0; level < Block.MAX_LEVEL; level++) {
                args.add(Arguments.of(level));
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Zeus can be instantiated correctly")
    public void zeusCreation() {
        assertEquals(GodName.Zeus, game.getCurrentPlayer().getGodCard().name);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleConstruction")
    @DisplayName("Test that Zeus' validation rules are correctly applied")
    public void testValidationCondition(int level) {

        Board board = game.getBoard();
        Point target = new Point(0, 0);

        for (int i = 0; i < level; i++) {
            try {
                board.place(Block.blocks[i], target);
            } catch (InvalidPositionException | BoxFullException e) {
                e.printStackTrace();
            }
        }
        try {
            board.place(game.getCurrentPlayer().getCurrentWorker(), target);
        } catch (InvalidPositionException | BoxFullException e) {
            e.printStackTrace();
        }

        Construction construction = new Construction(board, Block.blocks[level], target);
        assertEquals(Block.MAX_LEVEL - 1 != level, zeus.validate(zeus.computeBuildablePoints(), construction));
    }

    @Test
    public void testInvalidZeus() {
        Point start = new Point (0,0);
        Point target = new Point (1,1);
        Board board = game.getBoard();

        try {
            board.place(game.getCurrentPlayer().getCurrentWorker(),start);
            board.place(game.getCurrentPlayer().getOtherWorker(), target);
        } catch (InvalidPositionException e) {
            e.printStackTrace();
        } catch (BoxFullException e) {
            e.printStackTrace();
        }

        Construction construction = new Construction(board, Block.blocks[0], target);
        assertFalse(zeus.validate(zeus.computeBuildablePoints(), construction));
    }
}
