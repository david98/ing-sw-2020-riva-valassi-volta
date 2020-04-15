package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.godcards.GodName;

import java.util.ArrayList;

// evento che viene scatenato dopo che l'utente eletto casualmente seleziona le carte che vuole
// siano comprese nella partita
public class CardChoiceEvent extends GameEvent {

  // contiene le carte "in gioco" scelte dall'utente "eletto" casualmente. In quanto l'evento viene
  // lanciato 4 volte max, abbiamo deciso di "trasportare" l'informazione
  private ArrayList<GodName> selectedCards;

  public CardChoiceEvent(Object source) {
    super(source);
  }

  public ArrayList<GodName> getSelectedCards() {
    return selectedCards;
  }
}
