package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @class Reachability is an extension of Behavior. It represents in specific the "Move" behavior. Here, all methods influenced by cards acting on the Moving aspect
 * of the Game are listed
 */
public class Reachability extends Behavior {

    private static ArrayList<Point> blockedPoints = new ArrayList<Point>();

    /**
     * This method applies the Malus of the GodCard "Athena", blocking points that are a level higher than the current worker destination
     * It will fill the blockedPoints attribute. In default cases, the attribute is an empty list and it will remain that as long as constraintAthena
     * is not applied. ConstraintAthena gets applied by Athena on every player different than the current, if the current player moved up a level
     * @param game is the current game played by all the players
     * @param destination is the box chosen as destination by the current player, performing a Movement move.
     * @author Mattia Valassi
     */
    public void constraintAthena(Game game, Point destination) {
    }

    /**
     * This method checks if, after applying Apollo's effect, the point chosen by the player can de reached with a Movement move
     * (Apollo adds the possibility of exchanging with an adjacent worker of an opponent)
     * @param game is the game currently played by all the players
     * @param point is the destination of movement selected by the current player
     * @return true if the chosen position is reachable, false if it isn't
     * @author Davide Volta
     */
    public static boolean isPointReachableCanExchangeWithWorker(Game game, Point point){
        try {
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);
            if (!point.isAdjacent(currentWorkerPosition)) {
                return false;
            }

            try {
                Box destinationBox = game.getBoard().getBox(point);
                Stack<Item> destinationItems = destinationBox.getItems();
                Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

                return ((destinationItems.peek().canBeRemoved() && !destinationItems.peek().equals(otherWorker)));
            } catch (BoxEmptyException ignored) {
                return true;
            }

        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        }
        return false;
    }

    /**
     * This method checks if, after applying Minotaur's effect, the point chosen by the player can de reached with a Movement move
     * (Minotaur adds the possibility to force an opponent's worker to move in the same direction you're moving, and then take the previous position as yours)
     * @param game is the game currently played by all the players
     * @param point is the destination of movement selected by the current player
     * @return if the chosen position is reachable, false if it isn't
     * @author Marco Riva
     */
    public static boolean isPointReachableConditionedExchange(Game game, Point point){
        try {
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);

            if (!point.isAdjacent(currentWorkerPosition)) {
                return false;
            }

            Box destinationBox = game.getBoard().getBox(point);
            int destinationLevel = destinationBox.getLevel();
            int currentWorkerLevel = game.getBoard().getBox(currentWorkerPosition).getLevel();

            // Minotaur's rules
            if(destinationLevel - currentWorkerLevel <= 1) {
                Stack<Item> destinationItems = destinationBox.getItems();
                Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

                // se nella casella di destinazione c'è un worker nemico
                if(destinationItems.peek().canBeRemoved() && !destinationItems.peek().equals(otherWorker)) {
                    Worker enemysWorker = (Worker) destinationItems.peek();

                    int x = 2*point.getX() - currentWorkerPosition.getX();
                    int y = 2*point.getY() - currentWorkerPosition.getY();
                    Point forcedDestination = new Point(x,y);

                    Box forcedDestinationBox = game.getBoard().getBox(forcedDestination);
                    Item forcedDestinationItem = null;

                    // se la casella successiva nella stessa direzione del movimento è vuota
                    try { forcedDestinationItem = forcedDestinationBox.getItems().peek();
                    } catch (BoxEmptyException ignored) {
                        return true;
                    }

                    // se la casella successiva nella stessa direzione del movimento è libera
                    if(enemysWorker.canBePlacedOn(forcedDestinationItem)) {
                        return true;
                    }
                }
            }

        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        } catch (BoxEmptyException ignored) {
            return false;
        }
        return false;
    }

    public static boolean isPointReachablePreviousBoxDenied(Game game, Point point){
        try{
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);
            if (!point.isAdjacent(currentWorkerPosition)) {
                return false;
            }


        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        }
        return false;
    }

    public static boolean isPointReachableAthena(Game game, Point point) {
        try{
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);
            if (!point.isAdjacent(currentWorkerPosition)) {
                return false;
            }

            try {
                Box destinationBox = game.getBoard().getBox(point);
                Stack<Item> destinationItems = destinationBox.getItems();
                int destinationLevel = destinationBox.getLevel();
                int currentWorkerLevel = game.getBoard().getBox(currentWorkerPosition).getLevel();
                return (destinationLevel - currentWorkerLevel < 1)
                        && currentWorker.canBePlacedOn(destinationItems.peek());
            } catch (BoxEmptyException ignored) {
                return true;
            }

        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        }
        return false;
    }


}
