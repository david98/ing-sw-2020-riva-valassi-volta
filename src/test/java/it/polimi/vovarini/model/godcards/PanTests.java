package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.moves.Movement;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PanTests {

    private static Game game;
    private static GodCard pan;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            pan = GodCardFactory.create(GodName.Pan);
            pan.setGameData(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(pan);
            }
        } catch (InvalidNumberOfPlayersException e){
            e.printStackTrace();
        }
    }

    @BeforeEach
    private void resetGame(){
        Board b = game.getBoard();
        for (int x = 0; x < b.getSize(); x++){
            for (int y = 0; y < b.getSize(); y++){
                Point cur = new Point(x, y);
                while (b.remove(cur) != null);
            }
        }
    }

    private static Stream<Arguments> provideAllPossibleWinningMoves() {
        LinkedList<Arguments> args = new LinkedList<>();

        for (int lStart = Block.MIN_LEVEL - 1; lStart < Block.MAX_LEVEL; lStart++){
            for (int lEnd = Block.MIN_LEVEL - 1; lEnd <= Block.MAX_LEVEL; lEnd++) {
                args.add(Arguments.of(lStart, lEnd));
            }
        }

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleWinningMoves")
    @DisplayName("Test that Pan's winning conditions are correctly applied")
    void testWinningCondition(int startLevel, int endLevel){
        Point start = new Point(0, 0);
        Point end = new Point(0, 1);
        for (int i = 0; i < startLevel; i++){
            try {
                game.getBoard().place(Block.blocks[i], start);
            } catch (InvalidPositionException | BoxFullException e){
                e.printStackTrace();
            }
        }
        try {
            game.getBoard().place(game.getCurrentPlayer().getCurrentWorker(), start);
        } catch (InvalidPositionException | BoxFullException e){
            e.printStackTrace();
        }

        for (int i = 0; i < endLevel; i++){
            try {
                game.getBoard().place(Block.blocks[i], end);
            } catch (InvalidPositionException | BoxFullException e){
                e.printStackTrace();
            }
        }

        Movement m = new Movement(game.getBoard(), start, end);
        assertEquals((startLevel - endLevel >= 2) ||
                (endLevel == Block.WIN_LEVEL && startLevel < Block.WIN_LEVEL),
                pan.isMovementWinning(m));
    }
}
