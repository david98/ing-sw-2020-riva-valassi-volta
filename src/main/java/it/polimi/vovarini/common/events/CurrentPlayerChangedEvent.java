package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Represents that the current player has changed.
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class CurrentPlayerChangedEvent extends GameEvent{

  private Player newPlayer;

  /**
   *
   * @param source The event source (it should be a {@link it.polimi.vovarini.model.Game} object).
   * @param newPlayer The new current player.
   */
  public CurrentPlayerChangedEvent(Object source, Player newPlayer){
    super(source);
    this.newPlayer = newPlayer;
  }

  public Player getNewPlayer() {
    return newPlayer;
  }
}
