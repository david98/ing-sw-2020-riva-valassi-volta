package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.godcards.GodCard;

/**
 * Represents that a Player has been assigned a specific GodCard.
 *
 * @author Davide Volta
 * @version 0.2
 * @since 0.2
 */
public class CardAssignmentEvent extends GameEvent {

  private final Player targetPlayer;
  private final GodCard assignedCard;

  /**
   * Builds a CardAssignmentEvent
   * @param source is the source object of the event
   * @param targetPlayer is the Player who chose the card
   * @param assignedCard is the card the Player chose
   */
  public CardAssignmentEvent(Object source, Player targetPlayer, GodCard assignedCard) {
    super(source);
    this.targetPlayer = targetPlayer;
    this.assignedCard = assignedCard;
  }

  public Player getTargetPlayer() {
    return targetPlayer;
  }

  public GodCard getAssignedCard() {
    return assignedCard;
  }
}
