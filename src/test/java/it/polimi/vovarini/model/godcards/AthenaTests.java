package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.moves.Movement;
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

public class AthenaTests {

    private static Game game;
    private static GodCard athena;
    private static GodCard nobody;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            athena = GodCardFactory.create(GodName.Athena);
            athena.setGameData(game);
            nobody = GodCardFactory.create(GodName.Nobody);
            nobody.setGameData(game);
            game.getPlayers()[0].setGodCard(athena);
            game.getPlayers()[1].setGodCard(nobody);
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
        game.setCurrentPhase(Phase.Start);
        nobody.getMovementConstraints().clear();

        for(Player p : game.getPlayers()) {
            p.getMovementList().clear();
            p.getConstructionList().clear();
        }
        if(game.getCurrentPlayer().getGodCard().equals(nobody)) {
            game.nextPlayer();
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

        Point notAllowedStart = new Point(0,0);
        Point notAllowedEnd = new Point (1,1);

        for(Point start : allPoints) {
            List<Point> adjacentPositions = board.getAdjacentPositions(start);
            for(Point end : adjacentPositions) {
                for(int startLevel = 0; startLevel < Block.MAX_LEVEL; startLevel++) {
                    for (int endLevel = 0; endLevel <= startLevel + 1 && endLevel < Block.MAX_LEVEL; endLevel++) {

                        if(!start.equals(notAllowedStart) && !start.equals(notAllowedEnd)
                            && !end.equals(notAllowedStart) && !end.equals(notAllowedEnd))
                        {
                            args.add(Arguments.of(start, end, startLevel, endLevel));
                        }
                    }
                }
            }
        }
        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Athena can be instantiated correctly")
    public void athenaCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Athena);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMovementMoves")
    @DisplayName("Test that Athena's movement constraints are correctly applied to the opponent")
    public void invalidEnemyMoveUp(Point enemyStart, Point enemyEnd, int startLevel, int endLevel) {

        Board board = game.getBoard();
        Point athenaStart = new Point(0, 0);
        Point athenaEnd = new Point(1, 1);

        try {
            for(int i = 0; i < startLevel; i++) {
                board.place(Block.blocks[i], athenaStart);
                board.place(Block.blocks[i], enemyStart);
            }

            board.place(game.getPlayers()[0].getCurrentWorker(), athenaStart);
            board.place(game.getPlayers()[1].getCurrentWorker(), enemyStart);

            for(int i = 0; i < endLevel; i++) {
                board.place(Block.blocks[i], athenaEnd);
                board.place(Block.blocks[i], enemyEnd);
            }
        } catch (InvalidPositionException | BoxFullException e) {
            e.printStackTrace();
        }

        game.setCurrentPhase(athena.computeNextPhase(game));
        assertEquals(Phase.Movement, game.getCurrentPhase());

        Movement athenaMovement = new Movement(board, athenaStart, athenaEnd);
        assertTrue(athena.validate(athena.computeReachablePoints(),athenaMovement));
        game.performMove(athenaMovement);

        game.setCurrentPhase(athena.computeNextPhase(game));
        game.setCurrentPhase(athena.computeNextPhase(game));
        game.setCurrentPhase(athena.computeNextPhase(game));
        assertEquals(game.getPlayers()[1], game.getCurrentPlayer());

        game.setCurrentPhase(nobody.computeNextPhase(game));
        assertEquals(Phase.Movement, game.getCurrentPhase());

        Movement enemyMovement = new Movement(board, enemyStart, enemyEnd);
        assertEquals(endLevel - startLevel < 1, nobody.validate(nobody.computeReachablePoints(),enemyMovement));
    }

}
