package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

public class RegistrationEvent extends GameEvent {

  private final String nickname;

  public RegistrationEvent(Object source, Player player, String nickname) {
    super(source, player);
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}
