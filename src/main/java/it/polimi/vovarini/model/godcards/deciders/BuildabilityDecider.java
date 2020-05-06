package it.polimi.vovarini.model.godcards.deciders;


import it.polimi.vovarini.model.GameDataAccessor;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;

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
     * @param gameData is the gameData all players are currently playing
     * @param target is the box, represented by his point coordinates, where I want to build
     * @return false if the chosen target is equal to the one of the previous construction, true otherwise
     * @author Marco Riva, Mattia Valassi
     */
    public static boolean denyPreviousTarget(GameDataAccessor gameData, Point target) {

        Player currentPlayer = gameData.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        try {
            return !currentPlayer.getConstructionList().get(size - 1).getTarget().equals(target);
        } catch (ArrayIndexOutOfBoundsException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is a constraint applied to a Construction the player wants to perform after a previous one.
     * It denies the opportunity to build in any other target different than the first one chosen
     * @param gameData is the gameData all players are currently playing
     * @param target is the box, represented by his point coordinates, where I want to build
     * @return true if the chosen target is equal to the one of the previous construction, false otherwise
     * @author Marco Riva, Mattia Valassi
     */
    public static boolean buildOnSameTarget(GameDataAccessor gameData, Point target) {


        Player currentPlayer = gameData.getCurrentPlayer();
        int size = currentPlayer.getConstructionList().size();

        try {
            return currentPlayer.getConstructionList().get(size - 1).getTarget().equals(target)
                    && gameData.getBoard().getBox(target).getLevel() < 3;
        } catch (ArrayIndexOutOfBoundsException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is a constraint applied to a Construction the player wants to perform after a previous one.
     * It denies the opportunity to build on a perimeter space.
     * @param gameData is the gameData all players are currently playing
     * @param target is the box, represented by his point coordinates, where I want to build
     * @return false if the chosen target is a perimeter space, true otherwise
     * @author Marco Riva
     */
    public static boolean denyPerimeterSpace(GameDataAccessor gameData, Point target) {
        int min = 0;
        int max = Board.DEFAULT_SIZE - 1;

        int targetX = target.getX();
        int targetY = target.getY();

        return targetX != min && targetX != max && targetY != min && targetY != max;
    }



}
