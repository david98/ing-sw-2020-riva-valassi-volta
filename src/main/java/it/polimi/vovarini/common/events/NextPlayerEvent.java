package it.polimi.vovarini.common.events;

/**
 * Represents that the current player's turn has ended
 * and control should be given to the next one.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class NextPlayerEvent extends GameEvent {

  public NextPlayerEvent(Object source) {
    super(source);
  }
}
