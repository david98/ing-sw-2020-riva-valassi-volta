package it.polimi.vovarini.common.events;


import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.godcards.GodName;

import java.util.Arrays;

/**
 * Represents that all players have registered and the selection/assignment
 * of the god cards can now begin.
 *
 * @author Davide Volta
 * @version 0.2
 * @since 0.2
 */
public class GodSelectionStartEvent extends GameEvent {

  private final Player[] players;
  private final Player electedPlayer;
  private final GodName[] allGods;

  /**
   * @param source        The event source object.
   * @param players       An array containing all players that have registered.
   * @param electedPlayer The player who will choose what cards will be available.
   * @param allGods       The names of the gods from which the elected player can choose.
   */
  public GodSelectionStartEvent(Object source, Player[] players, Player electedPlayer, GodName[] allGods) {
    super(source);
    this.players = Arrays.stream(players).map(Player::new).toArray(Player[]::new); //clone
    this.electedPlayer = electedPlayer;
    this.allGods = allGods;
  }

  public Player[] getPlayers() {
    return players;
  }

  public Player getElectedPlayer() {
    return electedPlayer;
  }

  public GodName[] getAllGods() {
    return allGods;
  }
}
