package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.moves.Movement;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Block;
import org.junit.jupiter.api.BeforeAll;
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

public class HeraTests {

    private static Game game;
    private static GodCard hera;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            hera = GodCardFactory.create(GodName.Hera);
            hera.setGameData(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(hera);
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
                while(b.remove(cur) != null);
            }
        }
    }

    private static Stream<Arguments> provideAllPossibleMovementMoves() {

        LinkedList<Arguments> args = new LinkedList<>();

        Board board = new Board(Board.DEFAULT_SIZE);
        LinkedList<Point> allPoints = new LinkedList<>();

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                allPoints.add(new Point(x, y));
            }
        }

        for(Point start : allPoints) {
            List<Point> startAdjacentPositions = board.getAdjacentPositions(start);
            for(Point end : startAdjacentPositions) {
                    args.add(Arguments.of(start, end));
            }
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Hera can be instantiated correctly")
    public void heraCreation() {
        assertEquals(GodName.Hera, game.getCurrentPlayer().getGodCard().name);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMovementMoves")
    @DisplayName("Test that Hera's winning constraint are correctly applied")
    void testWinningConstraint(Point start, Point end){

        try {
            game.getBoard().place(Block.blocks[0], start);
            game.getBoard().place(Block.blocks[1], start);
            game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), start);
            game.getBoard().place(Block.blocks[0], end);
            game.getBoard().place(Block.blocks[1], end);
            game.getBoard().place(Block.blocks[2], end);
        } catch (InvalidPositionException | BoxFullException e){
            e.printStackTrace();
        }

        Movement movement = new Movement(game.getBoard(), start, end);

        int endX = end.getX();
        int endY = end.getY();
        boolean expected = endX != 0 && endX != Board.DEFAULT_SIZE - 1 && endY != 0 && endY != Board.DEFAULT_SIZE - 1;

        assertEquals(expected, hera.isMovementWinning(movement));
    }
}
