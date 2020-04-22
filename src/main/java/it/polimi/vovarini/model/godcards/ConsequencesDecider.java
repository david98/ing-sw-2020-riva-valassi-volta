package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.moves.Movement;

import java.util.LinkedList;
import java.util.List;

public class ConsequencesDecider extends Decider {

    public static List<Movement> listEffectsMinotaur(Game game, Movement movement) {

        List<Movement> movementList = new LinkedList<>();

        if(ReachabilityDecider.isPointReachableConditionedExchange(game, movement.getEnd())) {

            Point start = movement.getStart();
            Point end = movement.getEnd();

            int x = 2*end.getX() - start.getX();
            int y = 2*end.getY() - start.getY();

            Point forcedDestination = new Point(x,y);

            Movement forcedMovement = new Movement(game.getBoard(), end,forcedDestination, true);
            movementList.add(forcedMovement);
        }

        movementList.add(movement);

        return movementList;
    }
}
