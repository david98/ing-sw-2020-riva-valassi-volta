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

  /**
   * Builds an AvailableCardsEvent
   * @param source  is the source object of the event
   * @param selectedGods are the GodCards selected by the elected player
   */
  public AvailableCardsEvent(Object source, GodName[] selectedGods) {
    super(source);
    this.selectedGods = selectedGods;
  }

  /**
   * Getter method for the selected GodCards
   * @return the selected GodCards
   */
  public GodName[] getSelectedGods() {
    return selectedGods;
  }
}
