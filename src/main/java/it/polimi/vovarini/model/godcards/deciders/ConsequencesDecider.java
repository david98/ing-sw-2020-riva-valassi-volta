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

public class ConsequencesDecider extends Decider {

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
