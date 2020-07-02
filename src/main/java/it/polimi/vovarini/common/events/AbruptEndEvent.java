package it.polimi.vovarini.common.events;

/**
 * Raised when the RemoteView loses connection with the client.
 */
public class AbruptEndEvent extends GameEvent {
  /**
   * Builds an AbruptEndEvent
   * @param source is the source object of the event
   */
  public AbruptEndEvent(Object source) {
    super(source);
  }
}
