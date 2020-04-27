package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
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
    }

    @Test
    @DisplayName("Test that a GodCard of type Athena can be instantiated correctly")
    public void athenaCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Athena);
    }

    @Test
    public void invalidEnemyMoveUp() {
        Player currentPlayer = game.getCurrentPlayer();
        GodCard athena = currentPlayer.getGodCard();
        Worker athenaWorker = currentPlayer.getCurrentWorker();
        Player enemyPlayer = game.getPlayers()[1];
        enemyPlayer.setGodCard(new GodCard(GodName.Nobody));
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
            board.place(Block.blocks[0], enemyEnd);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        Movement athenaMovement = new Movement(board, athenaStart, athenaEnd);
        assertTrue(athena.validate(athena.computeReachablePoints(),athenaMovement));
        game.performMove(athenaMovement);

        // QUI mettere chiamata a metodo che controlla se è stato attivato il potere di Athena
        // (tramite movementList su Player). A seguito della chiamata, la collezione movementConstraint
        // degli avversari conterrà il metodo cannotMoveUp() presente su ReachabilityDecider

        // Provo a far salire l'avversario
        /*

        game.nextPlayer();
        Movement enemyMovement = new Movement(board, enemyStart, enemyEnd);
        assertFalse(enemyGodCard.validate(enemyGodCard.computeReachablePoints(), enemyMovement));

         */
    }

}
