package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.godcards.GodName;

// risponde alla scelta del giocatore della propria carta, successiva alla selezione
public class CardChoiceEvent extends GameEvent {

  private GodName assignedCard;

  public CardChoiceEvent(Object source) {
    super(source);
  }

  public GodName getAssignedCard() {
    return assignedCard;
  }
}
