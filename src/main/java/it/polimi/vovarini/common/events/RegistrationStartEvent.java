package it.polimi.vovarini.common.events;

public class RegistrationStartEvent extends GameEvent {
  /**
   * Builds a RegistrationStartEvent
   * @param source is the source object of the event
   */
  public RegistrationStartEvent(Object source) {
    super(source);
  }
}
