package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Signals to the target player that he can place his workers
 * on the board.
 *
 * @author Davide Volta
 * @version 0.2
 * @since 0.2
 */
public class PlaceYourWorkersEvent extends GameEvent {

  private final Player targetPlayer;

  public PlaceYourWorkersEvent(Object source, Player targetPlayer){
    super(source);
    this.targetPlayer = targetPlayer;
  }

  public Player getTargetPlayer() {
    return targetPlayer;
  }
}
