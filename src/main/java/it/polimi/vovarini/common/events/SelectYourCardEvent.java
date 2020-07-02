package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.godcards.GodName;

/**
 * Signals to the target player that he has to choose a god
 * among the available ones.
 *
 * @author Davide Volta
 * @version 0.2
 * @since 0.2
 */
public class SelectYourCardEvent extends GameEvent {

  private final Player targetPlayer;
  private final GodName[] godsLeft;

  /**
   * Builds a SelectYourCardEvent
   * @param source is the source object of the event
   * @param targetPlayer is the player who should select his card next
   * @param godsLeft are the GodCards that have not been chosen
   */
  public SelectYourCardEvent(Object source, Player targetPlayer, GodName[] godsLeft) {
    super(source);
    this.targetPlayer = targetPlayer;
    this.godsLeft = godsLeft;
  }

  public Player getTargetPlayer() {
    return targetPlayer;
  }

  public GodName[] getGodsLeft() {
    return godsLeft;
  }
}
