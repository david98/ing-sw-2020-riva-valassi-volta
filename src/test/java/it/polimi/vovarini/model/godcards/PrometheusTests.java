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
import it.polimi.vovarini.model.moves.Construction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PrometheusTests {

    Game game;

    @BeforeEach
    public void createPrometheusItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard prometheus = GodCardFactory.create(GodName.Prometheus);
        game.getCurrentPlayer().setGodCard(prometheus);
        prometheus.setGame(game);
    }

    @Test
    @DisplayName("Test that a GodCard of type Prometheus can be instantiated correctly")
    public void prometheusCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Prometheus);
    }

    @Test
    public void invalidMoveUp() {
        Player currentPlayer = game.getCurrentPlayer();
        GodCard prometheus = currentPlayer.getGodCard();
        Worker currentWorker = currentPlayer.getCurrentWorker();

        Board board = game.getBoard();
        Point start = new Point(0, 0);
        Point target = new Point(1,1);

        try {
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[0], target);
        assertTrue(prometheus.validate(prometheus.computeReachablePoints(),firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        // QUI mettere chiamata a metodo che controlla se è stato attivato il potere di Prometheus
        // (tramite movementList su Player). A seguito della chiamata, la collezione movementConstraint
        // di Prometheus conterrà il metodo cannotMoveUp() presente su ReachabilityDecider

        /*

        Movement invalidMovement = new Movement(board, start, target);
        assertFalse(prometheus.validate(prometheus.computeReachablePoints(),invalidMovement));

         */
    }



}
