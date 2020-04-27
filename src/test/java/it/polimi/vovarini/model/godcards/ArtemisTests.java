package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Movement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArtemisTests {

    Game game;

    @BeforeEach
    public void createArtemisItems() {
        try {
            game = new Game(2);
        } catch (InvalidNumberOfPlayersException ignored) {

        }
        try {
            game.addPlayer("Guest01");
            game.addPlayer("Guest02");
        } catch (InvalidNumberOfPlayersException ignored) {
        }
        GodCard artemis = GodCardFactory.create(GodName.Artemis);
        game.getCurrentPlayer().setGodCard(artemis);
        artemis.setGame(game);
    }

    @Test
    @DisplayName("Test that a GodCard of type Artemis can be instantiated correctly")
    public void artemisCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Artemis);
    }


    @Test
    @DisplayName("Test an invalid movement with a GodCard of type Artemis")
    // andrebbe parametrizzato
    public void invalidMovementInitialSpace() {
        GodCard artemis = game.getCurrentPlayer().getGodCard();
        Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();

        Board board = game.getBoard();
        Point start = new Point(0, 0);
        Point end = new Point(1, 1);

        try {
            board.place(currentWorker, start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(artemis.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Movement firstMovement = new Movement(board, start, end);
        game.performMove(firstMovement);

        game.setCurrentPhase(artemis.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);

        Movement invalidMovement = new Movement(board, end, start);
        assertFalse(artemis.validate(artemis.computeReachablePoints(), invalidMovement));
    }

}
