package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.godcards.GodName;

/**
 * Represents that a player has chosen his god.
 *
 * @author Davide Volta
 * @author Mattia Valassi
 *
 * @version 0.2
 * @version 0.1
 */
public class CardChoiceEvent extends GameEvent {

  private final GodName selectedGod;

  public CardChoiceEvent(Object source, GodName selectedGod) {
    super(source);
    this.selectedGod = selectedGod;
  }

  public GodName getSelectedGod() {
    return selectedGod;
  }
}
