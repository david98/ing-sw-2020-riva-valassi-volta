package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;

/**
 * TurnFlow is an extension of Behavior. It represents in specific the "Phase" behavior. Here, all methods influenced by cards acting on the Phase aspect
 * of the Game are listed
 * @author Mattia Valassi
 */
public class FlowDecider extends Decider {

    /**
     * restoration = false -> The flow is normal, not modified. restoration -> The flow is not normal, has been modified
     */
    private static boolean restoration = false;

    /**
     * The method extends the construction phase, necessary for Hephaestus and Demeter. It allows you to make back-to-back constructions.
     * @param game the game all players currently play
     * @return the next phase you have to get in. Restoration tells the method if it's the first time you access the extension of the construction phase.
     * If not alerted, we would never leave the construction phase.
     * Returns Phase.Construction if we are in the Construction phase for the first time, returns Phase.End if we are in the second iteration of Construction
     */
    public static Phase nextPhaseExtendsConstruction (Game game){
        if (game.getCurrentPhase().equals(Phase.Construction) && !restoration){
            restoration = true;
            return Phase.Construction;
        }
        else {
            restoration = false;
            return game.getCurrentPhase().next();
        }
    }

    /**
     * The method extends the movement phase, necessary for Artemis. It allows you to make back-to-back movements.
     * @param game the game all players currently play
     * @return the next phase you have to get in. Restoration tells the method if it's the first time you access the extension of the movement phase.
     *      * If not alerted, we would never leave the movement phase.
     *      * Returns Phase.Movement if we are in the Movement phase for the first time, returns Phase.Construction if we are in the second iteration of Movement
     */
    public static Phase nextPhaseExtendsMovement (Game game){
        if (game.getCurrentPhase().equals((Phase.Movement)) && !restoration){
            restoration = true;
            return Phase.Movement;
        }
        else {
            restoration = false;
            return game.getCurrentPhase().next();
        }
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
        else {
            restoration = false;
            return game.getCurrentPhase().next();
        }

    }

}
