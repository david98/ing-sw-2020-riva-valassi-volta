package it.polimi.vovarini.model.godcards;

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
 * ReachabilityDecider is an extension of Behavior. It represents in specific the "Move" behavior.
 * Here, all methods influenced by cards acting on the Moving aspect of the Game are listed
 * @author Davide Volta
 * @author Mattia Valassi
 * @author Marco Riva
 */
public class ReachabilityDecider extends Decider {

    /**
     * This method presents the movement condition of the Apollo card.
     * This method checks if, after applying Apollo's effect, the point chosen by the player can be reached
     * with a Movement move (Apollo adds the possibility of exchanging with an adjacent worker of an opponent)
     *
     * @param game is the game currently played by all the players
     * @param point is the destination of movement selected by the current player who owns Apollo card
     * @return true if there is an enemy worker on the point chosen as the destination, false otherwise
     * @author Davide Volta
     * @author Marco Riva
     */
    public static boolean isPointReachableCanExchangeWithWorker(Game game, Point point) {
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
                return false;
            }

        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        }
        return false;
    }

    /**
     * This method checks if, after applying Minotaur's effect, the point chosen by the player can be reached
     * with a Movement move (Minotaur adds the possibility to force an opponent's worker to move in the same
     * direction you're moving, and then take the previous position as yours)
     *
     * @param game is the game currently played by all the players
     * @param point is the destination of movement selected by the current player
     * @return true if there is an opponent's worker on the point chosen as the destination and if the opponent's
     * worker can be forced one space straight backwards to an unoccupied space at any level, false otherwise
     * @author Marco Riva
     */
    public static boolean isPointReachableConditionedExchange(Game game, Point point) {
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
     * This method is a constraint to apply to the second movement phase of a player when he's owning the Artemis card.
     * It denies to the player the opportunity to move to his previous box
     *
     * @param game is the game currently played by all the players
     * @param point is the destination of movement selected by the current player
     * @return false if the destination point is the initial point, true otherwise
     * @author Mattia Valassi
     * @author Marco Riva
     */
    public static boolean previousBoxDenied(Game game, Point point) {
        // mi fido che arrivati qui, la lista abbia un movimento, quindi non controllo se è vuoto
        // anche se sarebbe buona norma farlo... se è vuota, manda eccezione
        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getMovementList().size();

        return !currentPlayer.getMovementList().get(size-1).getStart().equals(point);
    }

    /**
     * This method presents the constraint of the Athena and Prometheus GodCards, blocking points
     * that are higher than the position of the current worker
     * It is added to the enemy players' movementConstraints collection when the player who owns
     * the Athena card moves up a level. It is added to the Prometheus card's movementConstraints collection
     * when the player who owns the Prometheus card builds in the construction phase before the movement phase.
     *
     * @param game  is the current game played by all the players
     * @param point is the destination of movement selected by the current player
     * @return false if the destination point is higher than the current worker position, true otherwise
     * @author Mattia Valassi
     * @author Marco Riva
     */
    public static boolean cannotMoveUp(Game game, Point point) {
        try {
            Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
            Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);

            Box destinationBox = game.getBoard().getBox(point);
            int destinationLevel = destinationBox.getLevel();
            int currentWorkerLevel = game.getBoard().getBox(currentWorkerPosition).getLevel();

            return destinationLevel - currentWorkerLevel < 1;

        } catch (ItemNotFoundException ignored) {
            System.err.println("This really should never happen...");
        }
        return false;
    }
}