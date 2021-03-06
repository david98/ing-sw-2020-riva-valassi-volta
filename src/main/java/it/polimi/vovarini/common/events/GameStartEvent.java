package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Represents the start of the game (all players have chosen
 * both a nickname and a {@link it.polimi.vovarini.model.godcards.GodCard},
 * then they placed their workers on the board).
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class GameStartEvent extends GameEvent {

  private final Player[] players;

  /**
   * This method creates a GameStartEvent
   * @param source the source of the event
   * @param players the players taking part in the game
   */
  public GameStartEvent(Object source, Player[] players) {
    super(source);
    this.players = players;
  }

  public Player[] getPlayers() {
    return players;
  }
}
