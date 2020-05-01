package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Represents the start of the game (all players have chosen
 * both a nickname and a {@link it.polimi.vovarini.model.godcards.GodCard}.
 * It also includes the players, so that even the last one to have connected
 * can know about the others.
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class GameStartEvent extends GameEvent{

  private final Player[] players;

  public GameStartEvent(Object source, Player[] players){
    super(source);
    this.players = players;
  }

  public Player[] getPlayers() {
    return players;
  }
}
