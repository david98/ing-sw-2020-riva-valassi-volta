package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.events.GodCardUpdateEvent;
import it.polimi.vovarini.common.events.PlayerInfoUpdateEvent;
import it.polimi.vovarini.common.events.WorkerSelectionEvent;
import it.polimi.vovarini.common.exceptions.InvalidLevelException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.GameDataAccessor;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Move;
import it.polimi.vovarini.model.moves.Movement;

/**
 * FlowDecider is an extension of Decider. It decides what is the next phase the player should be into,
 * based upon card effects
 *
 * @author Mattia Valassi
 * @version 2.0
 * @since 1.0
 */
public class FlowDecider extends Decider {

  /**
   * This method allows you to be in the Construction phase twice in a row, injecting the constraints connected to the player's GodCard
   *
   * @param gameData the gameData all players are currently playing
   * @return Phase.Construction if you called the method while in your first Construction phase. Otherwise, whatever is the next phase
   * @author Mattia Valassi, Marco Riva
   */
  public static Phase extendsConstruction(GameDataAccessor gameData, Boolean skipIfPossible) {
    GodCard currentPlayerGodCard = gameData.getCurrentPlayer().getGodCard();

    switch (gameData.getCurrentPhase()) {
      case Start -> {
        return GodCard.normalNextPhaseFromStart(gameData);
      }
      case Movement -> {
        return GodCard.normalNextPhaseFromMovement(gameData);
      }
      case Construction -> {

        if (!gameData.getCurrentPlayer().getConstructionList().isEmpty() && (skipIfPossible ||
                gameData.getCurrentPlayer().getGodCard().computeBuildablePoints().isEmpty())) {
          return Phase.End;
        }

        if (gameData.getCurrentPlayer().getConstructionList().size() == 1) {
          switch (currentPlayerGodCard.getName()) {
            case Demeter:
              currentPlayerGodCard.getConstructionConstraints().add(BuildabilityDecider::denyPreviousTarget);
              GameEventManager.raise(new GodCardUpdateEvent(gameData, currentPlayerGodCard, gameData.getCurrentPlayer()));
              break;
            case Hephaestus:
              currentPlayerGodCard.getConstructionConstraints().add(BuildabilityDecider::buildOnSameTarget);
              GameEventManager.raise(new GodCardUpdateEvent(gameData, currentPlayerGodCard, gameData.getCurrentPlayer()));
              break;
            case Hestia:
              currentPlayerGodCard.getConstructionConstraints().add(BuildabilityDecider::denyPerimeterSpace);
              GameEventManager.raise(new GodCardUpdateEvent(gameData, currentPlayerGodCard, gameData.getCurrentPlayer()));
              break;
                        /*case Poseidon:
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
                            break;*/
            default:
              break;
          }
          return Phase.Construction;
        } else if (gameData.getCurrentPlayer().getConstructionList().size() > 1 &&
                gameData.getCurrentPlayer().getConstructionList().size() < 4) {
          switch (currentPlayerGodCard.getName()) {
                        /*case Poseidon:
                            if (skipIfPossible ||
                                    gameData.getCurrentPlayer().getGodCard().computeReachablePoints().isEmpty()) {
                                return Phase.End;
                            }
                            return Phase.Construction;*/
            default:
              return gameData.getCurrentPhase().next();
          }
        } else if (gameData.getCurrentPlayer().getConstructionList().isEmpty()) {
          return Phase.Construction;
        }
      }
      case End -> {
        return GodCard.normalNextPhaseFromEnd(gameData);
      }
    }
    return Phase.Start; // will never be reached
  }

  /**
   * This method allows you to be in the Movement phase twice in a row, injecting the constraints connected to the player's GodCard
   *
   * @param gameData the gameData all players are currently playing
   * @return Phase.Movement if you called the method while in your first Movement phase. Otherwise, whatever is the next phase
   * @author Mattia Valassi, Marco Riva
   */
  public static Phase extendsMovement(GameDataAccessor gameData, Boolean skipIfPossible) {

    GodCard currentPlayerGodCard = gameData.getCurrentPlayer().getGodCard();

    switch (gameData.getCurrentPhase()) {
      case Start -> {
        return GodCard.normalNextPhaseFromStart(gameData);
      }
      case Movement -> {
        int size = gameData.getCurrentPlayer().getMovementList().size();

        if (size >= 1 && (currentPlayerGodCard.computeReachablePoints().isEmpty() || skipIfPossible)) {
          return Phase.Construction;
        }

        switch (currentPlayerGodCard.getName()) {
          case Triton:
            if (gameData.getCurrentPlayer().getMovementList().get(size - 1).getEnd().isPerimeterSpace()) {
              return Phase.Movement;
            } else if (gameData.getCurrentPlayer().getMovementList().isEmpty()) {
              return Phase.Movement;
            } else {
              return Phase.Construction;
            }
          case Artemis:
            if (size == 0) {
              return Phase.Movement;
            } else if (size == 1 && gameData.getCurrentPlayer().getGodCard().computeReachablePoints().size() > 1) {
              currentPlayerGodCard.getMovementConstraints().add(ReachabilityDecider::previousBoxDenied);
              GameEventManager.raise(new GodCardUpdateEvent(gameData, currentPlayerGodCard, gameData.getCurrentPlayer()));
              return Phase.Movement;
            } else {
              return Phase.Construction;
            }
          default:
            break;
        }
      }
      case Construction -> {
        return GodCard.normalNextPhaseFromConstruction(gameData);
      }
      case End -> {
        return GodCard.normalNextPhaseFromEnd(gameData);
      }
    }

    return gameData.getCurrentPhase().next();
  }

  /**
   * This method allows you to be in the Construction phase before and after the Movement phase, if certain conditions connected to the player's GodCard are verified
   *
   * @param gameData the gameData all players are currently playing
   * @return Phase.Construction if you are in the Start Phase. Otherwise, whatever is the next phase
   * @author Mattia Valassi
   */
  public static Phase buildBeforeAndAfter(GameDataAccessor gameData, Boolean skipIfPossible) {
    switch (gameData.getCurrentPhase()) {
      case Start -> {
        try {
          // it's like a flag
          gameData.getCurrentPlayer().getConstructionList().add(new Construction(gameData.getBoard(), new Block(Block.MAX_LEVEL), new Point(0, 0), true));
          GameEventManager.raise(new PlayerInfoUpdateEvent(gameData, gameData.getCurrentPlayer()));
        } catch (InvalidLevelException ignored) {
        }
        return Phase.Construction;
      }
      case Movement -> {
        return Phase.Construction;
      }
      case Construction -> {
        if (gameData.getCurrentPlayer().getConstructionList().size() == 1) {
          if (gameData.getCurrentPlayer().getConstructionList().get(0).isForced()) {
            // fase bonus, ma non ha costruito
            gameData.getCurrentPlayer().getConstructionList().removeIf(Move::isForced);
            return Phase.Movement;
          } else {
            // costruzione solo in fase normale
            return Phase.End;
          }
        } else if (gameData.getCurrentPlayer().getConstructionList().size() == 2) {
          if (gameData.getCurrentPlayer().getConstructionList().get(0).isForced()) {
            // fase bonus, ha costruito
            gameData.getCurrentPlayer().getConstructionList().removeIf(Move::isForced);
            gameData.getCurrentPlayer().getGodCard().getMovementConstraints().add(ReachabilityDecider::cannotMoveUp);
            GameEventManager.raise(new GodCardUpdateEvent(gameData, gameData.getCurrentPlayer().getGodCard(), gameData.getCurrentPlayer()));
            return Phase.Movement;
          } else {
            // costruzione sia in fase normale che bonus
            return Phase.End;
          }
        } else {
          return Phase.Construction;
        }
      }
      case End -> {
        gameData.getCurrentPlayer().getConstructionList().removeIf(Move::isForced);
        GameEventManager.raise(new PlayerInfoUpdateEvent(gameData, gameData.getCurrentPlayer()));
        return GodCard.normalNextPhaseFromEnd(gameData);
      }
    }

    return gameData.getCurrentPhase().next();
  }

  /**
   * This method allows you to apply a malus to the players following you, all during your End phase, if certain conditions connected to the player's GodCard are verified
   *
   * @param gameData the gameData all players are currently playing
   * @return Whatever is the next phase
   * @author Mattia Valassi
   */
  public static Phase applyMalus(GameDataAccessor gameData, Boolean skipIfPossible) {
    switch (gameData.getCurrentPhase()) {
      case Start -> {
        if (gameData.getCurrentPlayer().isHasLost()) {
          for (Player otherPlayer : gameData.getPlayers()) {
            if (!otherPlayer.equals(gameData.getCurrentPlayer())) {
              switch (gameData.getCurrentPlayer().getGodCard().getName()) {
                case Athena -> {
                  otherPlayer.getGodCard().getMovementConstraints().clear();
                  GameEventManager.raise(new GodCardUpdateEvent(gameData, otherPlayer.getGodCard(), otherPlayer));
                }

                case Limus -> {
                  otherPlayer.getGodCard().getConstructionConstraints().clear();
                  GameEventManager.raise(new GodCardUpdateEvent(gameData, otherPlayer.getGodCard(), otherPlayer));
                }
              }
            }
          }
        }
        return Phase.Movement;
      }
      case Movement -> {
        if (gameData.getCurrentPlayer().hasPlayerRisen(gameData)) {
          for (Player otherPlayer : gameData.getPlayers()) {
            if (!otherPlayer.equals(gameData.getCurrentPlayer())) {
              switch (gameData.getCurrentPlayer().getGodCard().getName()) {
                case Athena -> {
                  otherPlayer.getGodCard().getMovementConstraints().add(ReachabilityDecider::cannotMoveUp);
                  GameEventManager.raise(new GodCardUpdateEvent(gameData, otherPlayer.getGodCard(), otherPlayer));
                }
              }
            }
          }
        }
        return GodCard.normalNextPhaseFromMovement(gameData);
      }
      case Construction -> {
        return GodCard.normalNextPhaseFromConstruction(gameData);
      }
      case End -> {
        for (Player otherPlayer : gameData.getPlayers()) {
          if (!otherPlayer.equals(gameData.getCurrentPlayer())) {
            switch (gameData.getCurrentPlayer().getGodCard().getName()) {
              case Limus -> {
                otherPlayer.getGodCard().getConstructionConstraints().add(BuildabilityDecider::mustBuildDomes);
                GameEventManager.raise(new GodCardUpdateEvent(gameData, otherPlayer.getGodCard(), otherPlayer));
              }
            }
          }
        }
        return Phase.Start;
      }
    }

    return gameData.getCurrentPhase().next();
  }

}
