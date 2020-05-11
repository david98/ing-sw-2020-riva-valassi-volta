package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.godcards.GodName;

/**
 * Represents that the elected player has chosen which cards will be
 * available during the game.
 *
 * @author Davide Volta
 * @author Mattia Valassi
 * @version 0.2
 * @since 0.1
 */
public class AvailableCardsEvent extends GameEvent {


  private final GodName[] selectedGods;

  public AvailableCardsEvent(Object source, GodName[] selectedGods) {
    super(source);
    this.selectedGods = selectedGods;
  }

  public GodName[] getSelectedGods() {
    return selectedGods;
  }
}
