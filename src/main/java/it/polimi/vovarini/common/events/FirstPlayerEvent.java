package it.polimi.vovarini.common.events;

/**
 * Raised after the first client connects.
 */
public class FirstPlayerEvent extends GameEvent {

  /**
   * Builds a FirstPlayerEvent
   * @param source  is the source object of the event
   */
  public FirstPlayerEvent(Object source) {
    super(source);
  }
}
