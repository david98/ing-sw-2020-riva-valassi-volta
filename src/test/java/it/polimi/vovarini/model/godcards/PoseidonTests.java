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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PoseidonTests {
    private Game game;
    private GodCard poseidon;

    @BeforeEach
    public void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            poseidon = GodCardFactory.create(GodName.Poseidon);
            poseidon.setGameData(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(poseidon);
            }
            game.start();
        } catch (InvalidNumberOfPlayersException e){
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test that a GodCard of type Poseidon can be instantiated correctly")
    public void poseidonCreation() {
        assertEquals(GodName.Poseidon, game.getCurrentPlayer().getGodCard().name);
    }

    @Test
    @DisplayName("Test that Poseidon's construction conditions are correctly applied")
    /** status:
     * startCurrent: lv 1 + currentWorker
     * targetCurrent: lv 0 + free
     * startOther: lv 0 + otherWorker
     * targetOther: lv 0 + free
     */
    public void testConstructionConstraint() {
        Board board = game.getBoard();
        Point startCurrent = new Point(0, 0);
        Point targetCurrent = new Point(1, 1);
        Point startOther = new Point(3, 1);
        Point targetOther = new Point(2, 2);
        Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

        try {
            board.place(Block.blocks[0], startCurrent);
            board.place(game.getCurrentPlayer().getCurrentWorker(), startCurrent);
            board.place(game.getCurrentPlayer().getOtherWorker(), startOther);
        } catch (InvalidPositionException | BoxFullException e) {
            e.printStackTrace();
        }

        assertEquals(game.getCurrentPhase(), Phase.Start);
        game.setCurrentPhase(poseidon.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Movement);
        game.setCurrentPhase(poseidon.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[0], targetCurrent);
        assertTrue(poseidon.validate(poseidon.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(poseidon.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);
        assertEquals(otherWorker, game.getCurrentPlayer().getCurrentWorker());

        Construction secondConstruction = new Construction(board, Block.blocks[0], targetOther);
        assertTrue(poseidon.validate(poseidon.computeBuildablePoints(), secondConstruction));
        game.performMove(secondConstruction);

        game.setCurrentPhase(poseidon.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);
        assertEquals(otherWorker, game.getCurrentPlayer().getCurrentWorker());

        Construction thirdConstruction = new Construction(board, Block.blocks[1], targetOther);
        assertTrue(poseidon.validate(poseidon.computeBuildablePoints(), thirdConstruction));
        game.performMove(thirdConstruction);

        game.setCurrentPhase(poseidon.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);
        assertEquals(otherWorker, game.getCurrentPlayer().getCurrentWorker());

        Construction fourthConstruction = new Construction(board, Block.blocks[2], targetOther);
        assertTrue(poseidon.validate(poseidon.computeBuildablePoints(), fourthConstruction));
        game.performMove(fourthConstruction);

        game.setCurrentPhase(poseidon.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.End);
    }

}