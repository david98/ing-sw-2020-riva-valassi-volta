package it.polimi.vovarini.common.events;

/**
 * Raised after the first client connects.
 */
public class FirstPlayerEvent extends GameEvent {
  public FirstPlayerEvent(Object source) {
    super(source);
  }
}
