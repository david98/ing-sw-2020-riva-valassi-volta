package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LimusTests {

    private Game game;
    private GodCard limus;

    @BeforeEach
    public void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            limus = GodCardFactory.create(GodName.Limus);
            limus.setGameData(game);
            game.getPlayers()[0].setGodCard(limus);

            GodCard nobody = GodCardFactory.create(GodName.Nobody);
            nobody.setGameData(game);
            game.getPlayers()[1].setGodCard(nobody);

        } catch (InvalidNumberOfPlayersException e){
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test that a GodCard of type Limus can be instantiated correctly")
    public void limusCreation() {
        assertEquals(GodName.Limus, game.getCurrentPlayer().getGodCard().name);
    }


    @Test
    @DisplayName("Test that Limus' construction constraints are correctly applied to the opponent")
    public void invalidEnemyConstruction() {

        Player limusPlayer = game.getCurrentPlayer();
        Player enemyPlayer = game.getPlayers()[1];
        GodCard enemyGodCard = enemyPlayer.getGodCard();

        Board board = game.getBoard();
        Point limusWorker1 = new Point(0, 0);
        Point limusWorker2 = new Point(1, 0);
        Point enemyWorker1 = new Point(1, 1);
        Point enemyWorker2 = new Point(3, 3);

        board.place(limusPlayer.getCurrentWorker(), limusWorker1);
        board.place(limusPlayer.getOtherWorker(), limusWorker2);

        board.place(enemyPlayer.getCurrentWorker(), enemyWorker1);
        board.place(enemyPlayer.getOtherWorker(), enemyWorker2);


        game.setCurrentPhase(limus.computeNextPhase(game));
        limusPlayer.getMovementList().add(new Movement(board, new Point(0, 0), new Point(1, 0)));
        game.setCurrentPhase(limus.computeNextPhase(game));
        limusPlayer.getConstructionList().add(new Construction(board, Block.blocks[0], new Point(0, 0)));
        game.setCurrentPhase(limus.computeNextPhase(game));
        game.setCurrentPhase(limus.computeNextPhase(game));

        assertEquals(enemyPlayer, game.getCurrentPlayer());
        enemyPlayer.setWorkerSelected(true);
        enemyPlayer.setCurrentSex(Sex.Male);
        game.setCurrentPhase(enemyGodCard.computeNextPhase(game));
        enemyPlayer.getMovementList().add(new Movement(board, new Point(0, 0), new Point(1, 0)));
        game.setCurrentPhase(enemyGodCard.computeNextPhase(game));

        assertEquals(Phase.Construction, game.getCurrentPhase());


        List<Point> buildablePoints = enemyGodCard.computeBuildablePoints();

        List<Point> adjacentPoints = board.getAdjacentPositions(enemyWorker1);
        List<Point> adjacentLimus1 = board.getAdjacentPositions(limusWorker1);
        List<Point> adjacentLimus2 = board.getAdjacentPositions(limusWorker2);

        for (Point p : adjacentLimus1) {
            adjacentPoints.remove(p);
        }

        for (Point p : adjacentLimus2) {
            adjacentPoints.remove(p);
        }

        adjacentPoints.remove(limusWorker1);
        adjacentPoints.remove(limusWorker2);

        assertEquals(adjacentPoints, buildablePoints);

        Point buildableBecauseDome = new Point(0,1);

        for (int i = 0; i < Block.MAX_LEVEL-1; i++) {
            board.place(Block.blocks[i], buildableBecauseDome);
        }

        List<Point> newBuildablePoints = enemyGodCard.computeBuildablePoints();

        assertTrue(newBuildablePoints.contains(buildableBecauseDome));
    }

}
