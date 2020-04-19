package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.moves.Movement;
import it.polimi.vovarini.model.board.items.Block;

/**
 * @class WinningCondition is an extension of Behavior. It represents in specific the "Win" behavior. Here, all methods influenced by cards acting on the Win aspect
 * of the Game are listed
 * @author Mattia Valassi
 */
public class WinningCondition extends Behavior {

    /**
     * This method checks if, after applying Pan's effect, the current player wins after the execution of the Movement move he wants to perform
     * (Pan modifies the winning condition, adding the possibility to win if a worker can descend two or more levels in a Movement move)
     * @param movement the Movement move the player would like to perform
     * @return true if the player wins performing the movement, false otherwise
     */
    public static boolean isWinningPan(Movement movement){
        if (movement.isForced()) {
            return false;
        }
        int endLevel = movement.getBoard().getBox(movement.getEnd()).getLevel();
        int currentLevel = movement.getBoard().getBox(movement.getStart()).getLevel();

        if (endLevel != Block.WIN_LEVEL) {
            return currentLevel - endLevel >= 2;
        }

        return currentLevel < Block.WIN_LEVEL;
    }


}
