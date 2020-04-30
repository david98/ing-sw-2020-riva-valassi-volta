package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.Stack;

/**
 * ReachabilityDecider is an extension of Decider. It decides what boxes a player can move to
 * @version 2.0
 * @since 1.0
 * @author Mattia Valassi
 */
public class ReachabilityDecider extends Decider {

    /**
     * This method represents an alternative condition on reachability, allowing you to reach an adjacent box occupied by an opponent's worker
     * @param game  is the game currently played by all the players
     * @param point is the destination of movement selected by the current player
     * @return true if the chosen position is reachable, false if it isn't
     * @author Davide Volta
     */
    public static boolean canExchangeWithWorker(Game game, Point point) {
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
     * This method represents an alternative condition on reachability, allowing you to reach an adjacent box occupied by an opponent's worker
     * if conditions connected to the player's GodCard are verified
     * @param game  is the game currently played by all the players
     * @param point is the destination of movement selected by the current player
     * @return if the chosen position is reachable, false if it isn't
     * @author Marco Riva
     */
    public static boolean conditionedExchange(Game game, Point point) {
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
            if (destinationLevel - currentWorkerLevel <= 1) {
                Stack<Item> destinationItems = destinationBox.getItems();
                Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

                // se nella casella di destinazione c'è un worker nemico
                if (destinationItems.peek().canBeRemoved() && !destinationItems.peek().equals(otherWorker)) {
                    Worker enemysWorker = (Worker) destinationItems.peek();

                    // direzione = end - start
                    // destinazioneForzata = end + direzione = (2 * end) - start
                    int x = 2 * point.getX() - currentWorkerPosition.getX();
                    int y = 2 * point.getY() - currentWorkerPosition.getY();
                    Point forcedDestination = new Point(x, y);

                    // se il punto calcolato è fuori dalla plancia di gioco
                    if (!game.getBoard().isPositionValid(forcedDestination)) {
                        return false;
                    }

                    try {
                        Box forcedDestinationBox = game.getBoard().getBox(forcedDestination);
                        Item forcedDestinationItem = forcedDestinationBox.getItems().peek();

                        // se la casella successiva nella stessa direzione del movimento è libera
                        if (enemysWorker.canBePlacedOn(forcedDestinationItem)) {
                            return true;
                        }
                        // se la casella successiva nella stessa direzione del movimento è vuota
                    } catch (BoxEmptyException ignored) {
                        return true;
                    }

                }
            }

        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        } catch (BoxEmptyException ignored) {
            // se la cella dove voglio spostarmi è vuota, la mossa è permessa
            // grazie alle regole generali, non grazie al potere del Minotauro
            return false;
        }
        return false;
    }


    /**
     * This method represents a constraint on reachability, denying you to reach your previous box if you are performing two movements in a row
     * @param game is the game currently played by all the players
     * @param point is the destination of movement selected by the current player
     * @return if the chosen position is reachable, false if it isn't
     * @author Marco Riva, Mattia Valassi
     */
    public static boolean previousBoxDenied(Game game, Point point) {
        // mi fido che arrivati qui, la lista abbia un movimento, quindi non controllo se è vuoto
        // anche se sarebbe buona norma farlo... se è vuota, manda eccezione
        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getMovementList().size();

        if(currentPlayer.getMovementList().get(size-1).getStart().equals(point)) {
            return false;
        }

        return true;
    }

    /**
     * This method represents a constraint on reachability, denying the other players to level up with a movement if the player
     * currently playing did not level up in his turn
     * @param game  is the current game played by all the players
     * @param point is the box chosen as destination by the current player, performing a Movement move.
     * @author Mattia Valassi, Marco Riva
     */
    public static boolean cannotMoveUp(Game game, Point point) {
        try {
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);

            Box destinationBox = game.getBoard().getBox(point);
            int destinationLevel = destinationBox.getLevel();
            int currentWorkerLevel = game.getBoard().getBox(currentWorkerPosition).getLevel();

            return (destinationLevel - currentWorkerLevel < 1);

        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        }
        return false;
    }

}