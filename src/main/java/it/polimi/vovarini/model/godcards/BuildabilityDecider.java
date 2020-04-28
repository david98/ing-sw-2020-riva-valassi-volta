package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

/**
 * BuildabilityDecider is an extension of Behavior. It represents in specific the "Build" behavior.
 * Here, all methods influenced by cards acting on the Building aspect of the Game are listed
 * @author Mattia Valassi
 * @author Marco Riva
 */
public class BuildabilityDecider extends Decider {

    /**
     * This method presents the construction constraint of the Demeter card.
     * It is added to the Demetra card's buildingConstraints collection when the player who owns
     * the Demeter card enters the second construction phase.
     *
     * @param game is the game all players are currently playing
     * @param point is the target of construction selected by the current player
     * @return false if the targetPoint is the same point where the current player made the last construction, true otherwise
     * @author Marco Riva
     */
    public static boolean previousTargetDenied(Game game, Point point) {

        // mi fido che arrivati qui, la lista abbia una costruzione, quindi non controllo se è vuota
        // anche se sarebbe buona norma farlo... se è vuota, manda eccezione
        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        if(currentPlayer.getConstructionList().get(size-1).getTarget().equals(point)) {
            return false;
        }

        return true;
    }

    /**
     * This method presents the construction constraint of the Hephaestus card.
     * It is added to the Hephaestus card's buildingConstraints collection when the player who owns
     * the Hephaestus card enters the second construction phase.
     *
     * @param game is the game all players are currently playing
     * @param point is the target of construction selected by the current player
     * @return true if the targetPoint is the same point where the current player made the last construction and
     *         if it is possible to build any block other than a dome, false otherwise
     * @author Marco Riva
     */
    public static boolean additionalBlockOnFirstBlock(Game game, Point point) {

        // mi fido che arrivati qui, la lista abbia una costruzione, quindi non controllo se è vuota
        // anche se sarebbe buona norma farlo... se è vuota, manda eccezione
        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        if(currentPlayer.getConstructionList().get(size-1).getTarget().equals(point)
            && game.getBoard().getBox(point).getLevel() < 3) {
            return true;
        }

        return false;
    }



}
