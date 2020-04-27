package it.polimi.vovarini.model.godcards;


import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

/**
 * @class Buildability is an extension of Behavior. It represents in specific the "Build" behavior. Here, all methods influenced by cards acting on the Building aspect
 * of the Game are listed
 * @author Mattia Valassi
 */
public class BuildabilityDecider extends Decider {

    public static boolean isPointBuildablePreviousTargetDenied(Game game, Point point) {

        // mi fido che arrivati qui, la lista abbia una costruzione, quindi non controllo se è vuota
        // anche se sarebbe buona norma farlo... se è vuota, manda eccezione
        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        if(currentPlayer.getConstructionList().get(size-1).getTarget().equals(point)) {
            return false;
        }

        return true;
    }

    public static boolean additionalBlockOnFirstBlock(Game game, Point point) {

        // mi fido che arrivati qui, la lista abbia una costruzione, quindi non controllo se è vuota
        // anche se sarebbe buona norma farlo... se è vuota, manda eccezione
        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        if(currentPlayer.getConstructionList().get(size-1).getTarget().equals(point)) {
            return true;
        }

        return false;
    }



}
