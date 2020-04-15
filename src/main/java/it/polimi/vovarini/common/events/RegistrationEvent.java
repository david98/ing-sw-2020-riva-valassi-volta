package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

public class RegistrationEvent extends GameEvent {

  private final String nickname;

  public RegistrationEvent(Object source, String nickname) {
    super(source);
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}
