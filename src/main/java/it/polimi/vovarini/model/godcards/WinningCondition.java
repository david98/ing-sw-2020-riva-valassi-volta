package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Movement;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;

import java.util.ArrayList;

/**
 * @class WinningCondition is an extension of Behavior. It represents in specific the "Win" behavior. Here, all methods influenced by cards acting on the Win aspect
 * of the Game are listed
 * @author Mattia Valassi
 */
public class WinningCondition extends Behavior {

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
