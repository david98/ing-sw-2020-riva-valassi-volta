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
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Movement;
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

    Game game;

    @BeforeEach
    public void createAthenaItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard athena = GodCardFactory.create(GodName.Athena);
        game.getCurrentPlayer().setGodCard(athena);
        athena.setGame(game);

        GodCard enemyGodCard = GodCardFactory.create(GodName.Nobody);
        game.getPlayers()[1].setGodCard(enemyGodCard);
        enemyGodCard.setGame(game);

    }

    @Test
    @DisplayName("Test that a GodCard of type Athena can be instantiated correctly")
    public void athenaCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Athena);
    }

    private static Stream<Arguments> provideAllPossibleMoveUp() {
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
                for (int endLevel = 1; endLevel < Block.MAX_LEVEL; endLevel++) {
                    for(int startLevel = endLevel-1; startLevel < endLevel; startLevel++) {
                        if(!start.equals(notAllowedStart) && !start.equals(notAllowedEnd)
                            && !end.equals(notAllowedStart) && !end.equals(notAllowedEnd))
                        args.add(Arguments.of(start, end, startLevel, endLevel));
                    }
                }
            }
        }

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMoveUp")
    @DisplayName("Test an invalid movement after applying the malus of the GodCard Athena")
    public void invalidEnemyMoveUp(Point enemyStart, Point enemyEnd, int lEnemyStart, int lEnemyEnd) {

        Player currentPlayer = game.getCurrentPlayer();
        GodCard athena = currentPlayer.getGodCard();
        Worker athenaWorker = currentPlayer.getCurrentWorker();

        Player enemyPlayer = game.getPlayers()[1];
        GodCard enemyGodCard = enemyPlayer.getGodCard();
        Worker enemyWorker = enemyPlayer.getCurrentWorker();

        Board board = game.getBoard();
        Point athenaStart = new Point(0, 0);
        Point athenaEnd = new Point(1, 1);

        try {
            board.place(athenaWorker, athenaStart);
            board.place(Block.blocks[0], athenaEnd);

            for(int i = 0; i < lEnemyStart; i++) {
                board.place(Block.blocks[i], enemyStart);
            }
            board.place(enemyWorker, enemyStart);

            for(int i = 0; i < lEnemyEnd; i++) {
                board.place(Block.blocks[i], enemyEnd);
            }
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(athena.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Movement athenaMovement = new Movement(board, athenaStart, athenaEnd);
        assertTrue(athena.validate(athena.computeReachablePoints(),athenaMovement));
        game.performMove(athenaMovement);

        game.setCurrentPhase(athena.computeNextPhase(game));
        game.setCurrentPhase(athena.computeNextPhase(game));
        game.setCurrentPhase(athena.computeNextPhase(game));

        assertEquals(game.getCurrentPlayer(), enemyPlayer);

        game.setCurrentPhase(enemyGodCard.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        // Provo a far salire l'avversario
        Movement enemyMovement = new Movement(board, enemyStart, enemyEnd);
        assertFalse(enemyGodCard.validate(enemyGodCard.computeReachablePoints(), enemyMovement));
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMoveUp")
    @DisplayName("Test that the enemy can move up following a movement by Athena that does not unleash her power")
    public void validEnemyMoveUp(Point enemyStart, Point enemyEnd, int lEnemyStart, int lEnemyEnd) {
        Player currentPlayer = game.getCurrentPlayer();
        GodCard athena = currentPlayer.getGodCard();
        Worker athenaWorker = currentPlayer.getCurrentWorker();

        Player enemyPlayer = game.getPlayers()[1];
        GodCard enemyGodCard = enemyPlayer.getGodCard();
        Worker enemyWorker = enemyPlayer.getCurrentWorker();

        Board board = game.getBoard();
        Point athenaStart = new Point(0, 0);
        Point athenaEnd = new Point(1, 1);

        try {
            board.place(athenaWorker, athenaStart);

            for(int i = 0; i < lEnemyStart; i++) {
                board.place(Block.blocks[i], enemyStart);
            }
            board.place(enemyWorker, enemyStart);

            for(int i = 0; i < lEnemyEnd; i++) {
                board.place(Block.blocks[i], enemyEnd);
            }
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(athena.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Movement athenaMovement = new Movement(board, athenaStart, athenaEnd);
        assertTrue(athena.validate(athena.computeReachablePoints(),athenaMovement));
        game.performMove(athenaMovement);

        game.setCurrentPhase(athena.computeNextPhase(game));
        game.setCurrentPhase(athena.computeNextPhase(game));
        game.setCurrentPhase(athena.computeNextPhase(game));

        assertEquals(game.getCurrentPlayer(), enemyPlayer);

        game.setCurrentPhase(enemyGodCard.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        // Provo a far salire l'avversario
        Movement enemyMovement = new Movement(board, enemyStart, enemyEnd);
        assertTrue(enemyGodCard.validate(enemyGodCard.computeReachablePoints(), enemyMovement));
    }

}
