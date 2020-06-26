package it.polimi.vovarini.common.events;

/**
 * Raised when the RemoteView loses connection with the client.
 */
public class AbruptEndEvent extends GameEvent {
  public AbruptEndEvent(Object source) {
    super(source);
  }
}
