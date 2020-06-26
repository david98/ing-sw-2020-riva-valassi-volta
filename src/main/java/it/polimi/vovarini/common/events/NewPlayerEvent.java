package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;


/**
 * Represents that a registration was successful
 * and a new {@link it.polimi.vovarini.model.Player} was added to the game.
 *
 * @author Davide Volta
 */
public class NewPlayerEvent extends GameEvent {

  private final Player newPlayer;

  /**
   *
   * @param source The event source (it should be a {@link it.polimi.vovarini.model.Game object}.
   * @param newPlayer The new player.
   */
  public NewPlayerEvent(Object source, Player newPlayer){
    super(source);
    this.newPlayer = newPlayer;
  }

  public Player getNewPlayer() {
    return newPlayer;
  }
}
