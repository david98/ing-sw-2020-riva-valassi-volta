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

    @Test
    @DisplayName("Test an invalid movement after applying the malus of the GodCard Athena")
    public void invalidEnemyMoveUp() {

        Player currentPlayer = game.getCurrentPlayer();
        GodCard athena = currentPlayer.getGodCard();
        Worker athenaWorker = currentPlayer.getCurrentWorker();

        Player enemyPlayer = game.getPlayers()[1];
        GodCard enemyGodCard = enemyPlayer.getGodCard();
        Worker enemyWorker = enemyPlayer.getCurrentWorker();

        Board board = game.getBoard();
        Point athenaStart = new Point(0, 0);
        Point athenaEnd = new Point(1, 1);
        Point enemyStart = new Point(2,2);
        Point enemyEnd = new Point (2,3);

        try {
            board.place(athenaWorker, athenaStart);
            board.place(Block.blocks[0], athenaEnd);
            board.place(enemyWorker, enemyStart);
            board.place(Block.blocks[1], enemyEnd);
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

    @Test
    @DisplayName("Test that the enemy can move up following a movement by Athena that does not unleash her power")
    public void validEnemyMoveUp() {
        Player currentPlayer = game.getCurrentPlayer();
        GodCard athena = currentPlayer.getGodCard();
        Worker athenaWorker = currentPlayer.getCurrentWorker();

        Player enemyPlayer = game.getPlayers()[1];
        GodCard enemyGodCard = enemyPlayer.getGodCard();
        Worker enemyWorker = enemyPlayer.getCurrentWorker();

        Board board = game.getBoard();
        Point athenaStart = new Point(0, 0);
        Point athenaEnd = new Point(1, 1);
        Point enemyStart = new Point(2,2);
        Point enemyEnd = new Point (2,3);

        try {
            board.place(athenaWorker, athenaStart);
            board.place(enemyWorker, enemyStart);
            board.place(Block.blocks[1], enemyEnd);
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
