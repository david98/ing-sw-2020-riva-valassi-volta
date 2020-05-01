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
 * FlowDecider is an extension of Decider. It decides what is the next phase the player should be into, based upon card effects
 * @author Mattia Valassi
 * @version 2.0
 * @since 1.0
 */
public class FlowDecider extends Decider {

    /**
     * This method allows you to be in the Construction phase twice in a row, injecting the constraints connected to the player's GodCard
     * @param game the game all players are currently playing
     * @return Phase.Construction if you called the method while in your first Construction phase. Otherwise, whatever is the next phase
     * @author Mattia Valassi, Marco Riva
     */
    public static Phase extendsConstruction(Game game){
        GodCard currentPlayerGodCard = game.getCurrentPlayer().getGodCard();

        if (game.getCurrentPhase().equals(Phase.Construction) && game.getCurrentPlayer().getConstructionList().size() == 1){
            switch (currentPlayerGodCard.getName()){
                case Demeter:
                    currentPlayerGodCard.constructionConstraints.add(BuildabilityDecider::denyPreviousTarget);
                    break;
                case Hephaestus:
                    currentPlayerGodCard.constructionConstraints.add(BuildabilityDecider::buildOnSameTarget);
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
     * This method allows you to be in the Movement phase twice in a row, injecting the constraints connected to the player's GodCard
     * @param game the game all players are currently playing
     * @return Phase.Movement if you called the method while in your first Movement phase. Otherwise, whatever is the next phase
     * @author Mattia Valassi, Marco Riva
     */
    public static Phase extendsMovement(Game game){

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

    /**
     * This method allows you to be in the Construction phase before and after the Movement phase, if certain conditions connected to the player's GodCard are verified
     * @param game the game all players are currently playing
     * @return Phase.Construction if you are in the Start Phase. Otherwise, whatever is the next phase
     * @author Mattia Valassi
     */
    public static Phase buildBeforeAndAfter(Game game){

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

    /**
     * This method allows you to apply a malus to the players following you, all during your End phase, if certain conditions connected to the player's GodCard are verified
     * @param game the game all players are currently playing
     * @return Whatever is the next phase
     * @author Mattia Valassi
     */
    public static Phase applyMalus(Game game){
        if (game.getCurrentPhase().equals(Phase.End)){
            if (game.getCurrentPlayer().hasPlayerRisen(game)){
                for (Player otherPlayer : game.getPlayers()){
                    if (!otherPlayer.equals(game.getCurrentPlayer())){
                        switch (game.getCurrentPlayer().getGodCard().getName()){
                            case Athena:{
                                otherPlayer.getGodCard().movementConstraints.add(ReachabilityDecider::cannotMoveUp);
                            }
                        }
                    }
                }
            }
        }

        return game.getCurrentPhase().next();
    }

}
