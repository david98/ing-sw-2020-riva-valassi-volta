package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.events.GodCardUpdateEvent;
import it.polimi.vovarini.common.events.PlayerInfoUpdateEvent;
import it.polimi.vovarini.common.events.WorkerSelectionEvent;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.GameDataAccessor;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.common.exceptions.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Move;

/**
 * FlowDecider is an extension of Decider. It decides what is the next phase the player should be into,
 * based upon card effects
 * @author Mattia Valassi
 * @version 2.0
 * @since 1.0
 */
public class FlowDecider extends Decider {

    /**
     * This method allows you to be in the Construction phase twice in a row, injecting the constraints connected to the player's GodCard
     * @param gameData the gameData all players are currently playing
     * @return Phase.Construction if you called the method while in your first Construction phase. Otherwise, whatever is the next phase
     * @author Mattia Valassi, Marco Riva
     */
    public static Phase extendsConstruction(GameDataAccessor gameData){
        GodCard currentPlayerGodCard = gameData.getCurrentPlayer().getGodCard();

        if (gameData.getCurrentPhase().equals(Phase.Construction)) {

            if(gameData.getCurrentPlayer().getConstructionList().size() == 1){
                switch (currentPlayerGodCard.getName()){
                    case Demeter:
                      currentPlayerGodCard.getConstructionConstraints().add(BuildabilityDecider::denyPreviousTarget);
                      GameEventManager.raise(new GodCardUpdateEvent(currentPlayerGodCard, gameData.getCurrentPlayer()));
                      break;
                    case Hephaestus:
                      currentPlayerGodCard.getConstructionConstraints().add(BuildabilityDecider::buildOnSameTarget);
                      GameEventManager.raise(new GodCardUpdateEvent(currentPlayerGodCard, gameData.getCurrentPlayer()));
                      break;
                    case Hestia:
                        currentPlayerGodCard.getConstructionConstraints().add(BuildabilityDecider::denyPerimeterSpace);
                        GameEventManager.raise(new GodCardUpdateEvent(currentPlayerGodCard, gameData.getCurrentPlayer()));
                        break;
                    case Poseidon:
                        try {
                            Point otherWorkerPosition = gameData.getBoard().getItemPosition(gameData.getCurrentPlayer().getOtherWorker());
                            int otherWorkerLevel = gameData.getBoard().getBox(otherWorkerPosition).getLevel();

                            if(otherWorkerLevel == 0) {
                                if(gameData.getCurrentPlayer().getCurrentWorker().getSex().equals(Sex.Male)) {
                                    gameData.getCurrentPlayer().setCurrentSex(Sex.Female);
                                    GameEventManager.raise(new WorkerSelectionEvent(gameData.getCurrentPlayer(), Sex.Female));
                                }
                                else {
                                    gameData.getCurrentPlayer().setCurrentSex(Sex.Male);
                                    GameEventManager.raise(new WorkerSelectionEvent(gameData.getCurrentPlayer(), Sex.Male));
                                }

                                return Phase.Construction;
                            }
                            return Phase.End;

                        } catch (ItemNotFoundException ignored) {
                            System.err.println("This really should never happen...");
                        }
                        break;
                    default:
                        break;
                }
                return Phase.Construction;

            } else if(gameData.getCurrentPlayer().getConstructionList().size() > 1 &&
                    gameData.getCurrentPlayer().getConstructionList().size() < 4) {
                switch (currentPlayerGodCard.getName()) {
                    case Poseidon:
                        return Phase.Construction;
                    default:
                        return gameData.getCurrentPhase().next();
                }
            }
        }

        return gameData.getCurrentPhase().next();
    }

    /**
     * This method allows you to be in the Movement phase twice in a row, injecting the constraints connected to the player's GodCard
     * @param gameData the gameData all players are currently playing
     * @return Phase.Movement if you called the method while in your first Movement phase. Otherwise, whatever is the next phase
     * @author Mattia Valassi, Marco Riva
     */
    public static Phase extendsMovement(GameDataAccessor gameData){

        GodCard currentPlayerGodCard = gameData.getCurrentPlayer().getGodCard();
        int size = gameData.getCurrentPlayer().getMovementList().size();

        switch (currentPlayerGodCard.getName()) {
            case Triton:
                if (gameData.getCurrentPhase().equals(Phase.Movement) &&
                        gameData.getCurrentPlayer().getMovementList().get(size - 1).getEnd().isPerimeterSpace()) {
                    return Phase.Movement;
                }

                break;
            case Artemis:
                if (gameData.getCurrentPhase().equals(Phase.Movement) && size == 1) {
                    currentPlayerGodCard.getMovementConstraints().add(ReachabilityDecider::previousBoxDenied);
                    GameEventManager.raise(new GodCardUpdateEvent(currentPlayerGodCard, gameData.getCurrentPlayer()));
                    return Phase.Movement;
                }

                break;
            default:
                break;
        }
        return gameData.getCurrentPhase().next();
    }

    /**
     * This method allows you to be in the Construction phase before and after the Movement phase, if certain conditions connected to the player's GodCard are verified
     * @param gameData the gameData all players are currently playing
     * @return Phase.Construction if you are in the Start Phase. Otherwise, whatever is the next phase
     * @author Mattia Valassi
     */
    public static Phase buildBeforeAndAfter(GameDataAccessor gameData){
        if (gameData.getCurrentPhase().equals(Phase.Start)){
            try {
                gameData.getCurrentPlayer().getConstructionList().add(new Construction(gameData.getBoard(), new Block(Block.MAX_LEVEL), new Point(0, 0), true));
                GameEventManager.raise(new PlayerInfoUpdateEvent(gameData.getCurrentPlayer()));
            } catch (InvalidLevelException ignored) {}
            return Phase.Construction;
        }

        if (
            gameData.getCurrentPhase().equals(Phase.Construction) &&
            gameData.getCurrentPlayer().getConstructionList().size() == 1 &&
            gameData.getCurrentPlayer().getConstructionList().get(0).isForced()
        ){
            gameData.getCurrentPlayer().getConstructionList().clear();
            GameEventManager.raise(new PlayerInfoUpdateEvent(gameData.getCurrentPlayer()));
            return Phase.Movement;
        }
        else if (gameData.getCurrentPlayer().getConstructionList().size() == 2
                && gameData.getCurrentPhase().equals(Phase.Construction)){
          gameData.getCurrentPlayer().getConstructionList().removeIf(Move::isForced);
          GameEventManager.raise(new PlayerInfoUpdateEvent(gameData.getCurrentPlayer()));
          gameData.getCurrentPlayer().getGodCard().getMovementConstraints().add(ReachabilityDecider::cannotMoveUp);
          GameEventManager.raise(new GodCardUpdateEvent(gameData.getCurrentPlayer().getGodCard(), gameData.getCurrentPlayer()));
          return Phase.Movement;
        }
        else {
            return gameData.getCurrentPhase().next();
        }

    }

    /**
     * This method allows you to apply a malus to the players following you, all during your End phase, if certain conditions connected to the player's GodCard are verified
     * @param gameData the gameData all players are currently playing
     * @return Whatever is the next phase
     * @author Mattia Valassi
     */
    public static Phase applyMalus(GameDataAccessor gameData){
        if (gameData.getCurrentPhase().equals(Phase.End)){
            if (gameData.getCurrentPlayer().hasPlayerRisen(gameData)){
                for (Player otherPlayer : gameData.getPlayers()){
                    if (!otherPlayer.equals(gameData.getCurrentPlayer())){
                        switch (gameData.getCurrentPlayer().getGodCard().getName()){
                            case Athena -> {
                                otherPlayer.getGodCard().getMovementConstraints().add(ReachabilityDecider::cannotMoveUp);
                                GameEventManager.raise(new GodCardUpdateEvent(otherPlayer.getGodCard(), otherPlayer));
                                return Phase.Start;
                            }
                        }
                    }
                }
            }
        }

        return gameData.getCurrentPhase().next();
    }

}
