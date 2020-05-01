package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.moves.Movement;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * ConsequencesDecider is an extension of Decider. It decides how many and what type of forced and not-forced moves you must execute after the application of an effect.
 * They are put in an ordered sequence.
 * @author Mattia Valassi
 */
public class ConsequencesDecider extends Decider {

    /**
     * This method computes the necessary forced moves to apply Minotaur's effect.
     * @param game the game all players are currently playing
     * @param movement is the Movement move already validated that the player wants to perform
     * @return a list of all movements, forced and not-forced, the game must execute to obtain the desired effect
     * @author Marco Riva
     */
    public static List<Movement> forceOpponentWorker(Game game, Movement movement) {
        List<Movement> movementList = new LinkedList<>();

        Point start = movement.getStart();
        Point end = movement.getEnd();

        try {
            Box destinationBox = game.getBoard().getBox(end);
            Stack<Item> destinationItems = destinationBox.getItems();

            if(destinationItems.peek().canBeRemoved()) {

                int x = 2*end.getX() - start.getX();
                int y = 2*end.getY() - start.getY();
                Point forcedDestination = new Point(x,y);

                Movement forcedMovement = new Movement(game.getBoard(), end,forcedDestination, true);
                movementList.add(forcedMovement);
            }
        } catch (BoxEmptyException ignored) {
        }

        movementList.add(movement);

        return movementList;
    }
}
