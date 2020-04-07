package it.polimi.vovarini.controller.events;

public class RegistrationEvent extends GameEvent {

  private final String nickname;

  public RegistrationEvent(Object source) {
    super(source);
    this.nickname = null;
  }

  public RegistrationEvent(Object source, String nickname) {
    super(source);
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}