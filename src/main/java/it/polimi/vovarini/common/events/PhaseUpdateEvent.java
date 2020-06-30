package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Phase;

/**
 * Represents that the current {@link Phase} of the {@link it.polimi.vovarini.model.Game}
 * has changed.
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class PhaseUpdateEvent extends GameEvent {
  private Phase newPhase;

  /**
   * @param source   The event source, it should be a {@link it.polimi.vovarini.model.Game} object.
   * @param newPhase The new current phase.
   */
  public PhaseUpdateEvent(Object source, Phase newPhase) {
    super(source);
    this.newPhase = newPhase;
  }

  public Phase getNewPhase() {
    return newPhase;
  }
}
