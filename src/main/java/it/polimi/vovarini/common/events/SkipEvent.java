package it.polimi.vovarini.common.events;

/**
 * Represents a {@link it.polimi.vovarini.model.Phase} skip.
 * It should make the game transition to the next phase.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class SkipEvent extends GameEvent {

  /**
   * Builds a SkipEvent
   * @param source is the source object of the event
   */
  public SkipEvent(Object source) {
    super(source);
  }
}
