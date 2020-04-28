package it.polimi.vovarini.model.godcards;

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
 * ConsequencesDecider is an extension of Behavior. It represents in specific the "Consequences" behavior.
 * Here, all methods influenced by cards which, following a move, have consequences
 * @author Mattia Valassi
 * @author Marco Riva
 */
public class ConsequencesDecider extends Decider {

    /**
     * This method presents the consequences of the Minotaur card. When the Minotaur's effect is used,
     * the forced movement move that moves the enemy's worker according to the rules of the game is generated.
     *
     * @param game is the game all players are currently playing
     * @param movement is the movement move the player wants to perform, which is already been validated
     * @return list of movement moves to execute
     * @author Marco Riva
     */
    public static List<Movement> listEffectsMinotaur(Game game, Movement movement) {

        List<Movement> movementList = new LinkedList<>();

        Point start = movement.getStart();
        Point end = movement.getEnd();

        try {
            Box destinationBox = game.getBoard().getBox(end);
            Stack<Item> destinationItems = destinationBox.getItems();

            // se sul punto end è presente un worker
            if(destinationItems.peek().canBeRemoved()) {

                // direzione = end - start
                // destinazioneForzata = end + direzione = (2 * end) - start
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
