package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Represents the fact a certain player (losingPlayer) has lost inside the game
 *
 * @author Mattia Valassi
 * @version 0.1
 */
public class LossEvent extends GameEvent {

  private final Player losingPlayer;

  /**
   * Builds a LossEvent
   * @param source  is the source object of the event
   * @param losingPlayer The Player who has lost and triggers the event with his loss
   */
  public LossEvent(Object source, Player losingPlayer) {
    super(source);
    this.losingPlayer = losingPlayer;
  }

  public Player getLosingPlayer() {
    return losingPlayer;
  }
}
