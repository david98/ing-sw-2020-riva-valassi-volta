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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void consequencesApplied() {
        GodCard athena = game.getCurrentPlayer().getGodCard();
        Worker athenaWorker = game.getCurrentPlayer().getCurrentWorker();
        Player enemyPlayer = game.getPlayers()[1];
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

            Movement athenaMovement = new Movement(board, athenaStart, athenaEnd);
            Movement enemyMovement = new Movement(board, enemyStart, enemyEnd);

            game.performMove(athenaMovement);
            /* dentro performMove():
               aggiungere chiamata a metodo che controlla se il potere di Athena è stato attivato
               e in caso affermativo fa la push di constraintAthena() dentro la collezione
               movementConstraint dei Player avversari
            */


            // ora controllo che effettivamente gli avversari non possono salire di livello
            /*
                Provo a far salire l'avversario e controllo che la mossa non è stata eseguita

                next player: game.setCurrentPhase(athena.computeNextPhase(game)); x 4
                performMove(enemyMovement);
                assertEquals(enemyWorker, board.getBox(enemyStart).getItems().peek());
             */


        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

    }



}
