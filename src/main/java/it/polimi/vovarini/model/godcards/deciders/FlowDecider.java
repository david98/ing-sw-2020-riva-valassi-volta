package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.moves.Construction;

/**
 * TurnFlow is an extension of Behavior. It represents in specific the "Phase" behavior. Here, all methods influenced by cards acting on the Phase aspect
 * of the Game are listed
 * @author Mattia Valassi
 */
public class FlowDecider extends Decider {

    //needs new version of javaDoc

    /**
     * The method extends the construction phase, necessary for Hephaestus and Demeter. It allows you to make back-to-back constructions.
     * @param game the game all players currently play
     * @return the next phase you have to get in. Restoration tells the method if it's the first time you access the extension of the construction phase.
     * If not alerted, we would never leave the construction phase.
     * Returns Phase.Construction if we are in the Construction phase for the first time, returns Phase.End if we are in the second iteration of Construction
     */
    public static Phase nextPhaseExtendsConstruction (Game game){
        GodCard currentPlayerGodCard = game.getCurrentPlayer().getGodCard();

        if (game.getCurrentPhase().equals(Phase.Construction) && game.getCurrentPlayer().getConstructionList().size() == 1){
            switch (currentPlayerGodCard.getName()){
                case Demeter:
                    currentPlayerGodCard.constructionConstraints.add(BuildabilityDecider::previousTargetDenied);
                    break;
                case Hephaestus:
                    currentPlayerGodCard.constructionConstraints.add(BuildabilityDecider::additionalBlockOnFirstBlock);
                    break;
                default:
                    break;
            }
            return Phase.Construction;
        }
        else {
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

        GodCard currentPlayerGodCard = game.getCurrentPlayer().getGodCard();

        if (game.getCurrentPhase().equals((Phase.Movement)) && game.getCurrentPlayer().getMovementList().size() == 1){
            switch (currentPlayerGodCard.getName()){
                case Artemis:
                    currentPlayerGodCard.movementConstraints.add(ReachabilityDecider::previousBoxDenied);
                    break;
                default:
                    break;
            }
            return Phase.Movement;
        }
        else {
            return game.getCurrentPhase().next();
        }
    }

    public static Phase nextPhaseConstructionTwice (Game game){

        GodCard currentPlayerGodCard = game.getCurrentPlayer().getGodCard();

        if (game.getCurrentPhase().equals(Phase.Start)){
            try {
                game.getCurrentPlayer().getConstructionList().add(new Construction(game.getBoard(), new Block(Block.MAX_LEVEL), new Point(0, 0), true));
            } catch (InvalidLevelException ignored) {}
            return Phase.Construction;
        }

        if (
            game.getCurrentPhase().equals(Phase.Construction) &&
            game.getCurrentPlayer().getConstructionList().size() == 1 &&
            game.getCurrentPlayer().getConstructionList().get(0).isForced()
        ){
            game.getCurrentPlayer().getConstructionList().clear();
            return Phase.Movement;
        }
        else if (game.getCurrentPlayer().getConstructionList().size() == 2){
            game.getCurrentPlayer().getGodCard().movementConstraints.add(ReachabilityDecider::cannotMoveUp);
            return Phase.Movement;
        }
        else {
            return game.getCurrentPhase().next();
        }

    }

    public static Phase nextPhaseApplyMalus (Game game){
        if (game.getCurrentPhase().equals(Phase.End)){
            if (game.getCurrentPlayer().hasPlayerRisen(game)){
                for (Player otherPlayer : game.getPlayers()){
                    if (!otherPlayer.equals(game.getCurrentPlayer())){
                        switch (game.getCurrentPlayer().getGodCard().getName()){
                            case Athena:{
                                otherPlayer.getGodCard().movementConstraints.add(ReachabilityDecider::cannotMoveUp);
                                return Phase.Start;
                            }
                        }
                    }
                }
            }
        }

        return game.getCurrentPhase().next();
    }

}
