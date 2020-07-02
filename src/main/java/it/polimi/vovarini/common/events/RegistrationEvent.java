package it.polimi.vovarini.common.events;

import it.polimi.vovarini.view.cli.GameView;

/**
 * Represents a request to be registered as a player
 * with the given nickname.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class RegistrationEvent extends GameEvent {

  private final String nickname;

  /**
   * @param source   The event source (ideally, a {@link GameView} object).
   * @param nickname The desired nickname (which has already been validated: an invalid nickname
   *                 should result in an exception being thrown by the handler).
   */
  public RegistrationEvent(Object source, String nickname) {
    super(source);
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}
