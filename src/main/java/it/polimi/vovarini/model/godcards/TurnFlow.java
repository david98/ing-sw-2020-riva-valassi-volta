package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Point;

import java.util.ArrayList;

/**
 * @class TurnFlow is an extension of Behavior. It represents in specific the "Phase" behavior. Here, all methods influenced by cards acting on the Phase aspect
 * of the Game are listed
 * @author Mattia Valassi
 */
public class TurnFlow extends Behavior {

    private static boolean restoration = false;

    public static Phase nextPhaseExtendsConstruction (Game game){
        if (game.getCurrentPhase().equals(Phase.Construction)) return Phase.Construction;
        else return game.getCurrentPhase().next();
    }

    public static Phase nextPhaseExtendsMovement (Game game){
        if (game.getCurrentPhase().equals((Phase.Movement))) return Phase.Movement;
        else return game.getCurrentPhase().next();
    }

    public static Phase nextPhaseConstructionTwice (Game game){
        if (game.getCurrentPhase().equals(Phase.Start)){
            restoration = true;
            return Phase.Construction;
        }

        if (game.getCurrentPhase().equals(Phase.Construction) && restoration){
            restoration = false;
            return Phase.Movement;
        }
        else return game.getCurrentPhase().next();

    }

}
