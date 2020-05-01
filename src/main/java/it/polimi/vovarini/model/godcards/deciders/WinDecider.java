package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.model.moves.Movement;

/**
 * WinDecider is an extension of Decider. It decides if a player was able to win with his last performed move
 * @author Mattia Valassi
 * @version 2.0
 * @since 1.0
 */
public class WinDecider extends Decider {

    /**
     * This method adds a winning condition, making the player able to win if he moves down two or more levels in a single Movement
     * @param movement is the Movement move the player wants to perform
     * @return true if the Movement makes the player move down two or more levels, false otherwise
     * @author Marco Riva
     */
    public static boolean downTwoLevels(Movement movement) {
        int endLevel = movement.getBoard().getBox(movement.getEnd()).getLevel();
        int currentLevel = movement.getBoard().getBox(movement.getStart()).getLevel();

        return currentLevel - endLevel >= 2;
    }
}
