package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Movement;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

public class NobodyTests {

    Game game;
    String name;

    @BeforeEach
    void createNobodyItems(){
        game = new Game(2);
        name = "";
    }

    @Test
    @DisplayName("Test that a GodCard of type Nobody can be instantiated correctly")
    void nobodyCreation() {
        Nobody nobody = new Nobody(game);
        assertEquals(nobody.name, "Nobody");
    }

    @Test
    @DisplayName("Test that checks if the method computes the right reachable points")
    void reachablePoints(Point current){
        for (Point point : game.getBoard().getAdjacentPositions(current)){
            if (point.getX() < 0 || point.getX() >= Board.DEFAULT_SIZE || point.getY() < 0 || point.getY() >= Board.DEFAULT_SIZE){
                //Mi aspetto che il test fallisca in quanto viene lanciata un'eccezione teoricamente scongiurata dalla getAdjacentPositions
                assertThrows(InvalidPositionException.class, () -> game.getCurrentPlayer().getCurrentWorker().canBePlacedOn(game.getBoard().getTopmostItem(point)));
            }

            int currentLevel = 0, pointLevel = 0;
            Stack<Item> stackCurrent = game.getBoard().getBox(current).getItems();
            if (stackCurrent.empty()) currentLevel = 0;
            else if (stackCurrent.peek() instanceof Block) currentLevel = ((Block) stackCurrent.peek()).getLevel();
            else {
                stackCurrent.pop();
                if (stackCurrent.empty()) currentLevel = 0;
                else if (stackCurrent.peek() instanceof Block) currentLevel = ((Block) stackCurrent.peek()).getLevel();
            }

            Stack<Item> stackPoint = game.getBoard().getBox(point).getItems();
            if (stackPoint.empty()) pointLevel = 0;
            else if (stackPoint.peek() instanceof Block) pointLevel = ((Block) stackCurrent.peek()).getLevel();
            else {
                stackPoint.pop();
                if (stackPoint.empty()) pointLevel = 0;
                else if (stackPoint.peek() instanceof Block) pointLevel = ((Block) stackCurrent.peek()).getLevel();
            }


            try {
                assertTrue
                        (game.getCurrentPlayer().getCurrentWorker().canBePlacedOn(game.getBoard().getTopmostItem(point)) &&
                                   !point.equals(current) &&
                                   (currentLevel == pointLevel ||  Math.abs(currentLevel - pointLevel) == 1)
                        );
            }
            catch (InvalidPositionException e){
                System.err.println("TEST FAILED. POINT SHOULD NOT EXIST");
            }
            catch (BoxEmptyException e){
                //Se il punto non è lo stesso, dovrei controllare che il livello sia superiore o inferiore solo di 1
                assertTrue(!point.equals(current) && (currentLevel == pointLevel ||  Math.abs(currentLevel - pointLevel) == 1) );
            }

        }
    }



}
