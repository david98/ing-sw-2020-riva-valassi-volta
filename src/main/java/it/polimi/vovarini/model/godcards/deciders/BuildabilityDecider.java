package it.polimi.vovarini.model.godcards.deciders;


import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

/**
 * BuildabilityDecider is an extension of Decider. It decides what boxes the player can build upon
 * @author Mattia Valassi
 * @version 2.0
 * @since 1.0
 */
public class BuildabilityDecider extends Decider {

    /**
     * This method is a constraint applied to a Construction the player wants to perform after a previous one.
     * It denies the opportunity to build on the same target you just built upon.
     * @param game is the game all players are currently playing
     * @param target is the box, represented by his point coordinates, where I want to build
     * @return false if the chosen target is equal to the one of the previous construction, true otherwise
     * @author Marco Riva, Mattia Valassi
     */
    public static boolean denyPreviousTarget(Game game, Point target) {

        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        if(currentPlayer.getConstructionList().get(size-1).getTarget().equals(target)) {
            return false;
        }

        return true;
    }

    /**
     * This method is a constraint applied to a Construction the player wants to perform after a previous one.
     * It denies the opportunity to build in any other target different than the first one chosen
     * @param game is the game all players are currently playing
     * @param target is the box, represented by his point coordinates, where I want to build
     * @return true if the chosen target is equal to the one of the previous construction, false otherwise
     * @author Marco Riva, Mattia Valassi
     */
    public static boolean buildOnSameTarget(Game game, Point target) {


        Player currentPlayer = game.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        if(currentPlayer.getConstructionList().get(size-1).getTarget().equals(target)
            && game.getBoard().getBox(target).getLevel() < 3) {
            return true;
        }

        return false;
    }



}
