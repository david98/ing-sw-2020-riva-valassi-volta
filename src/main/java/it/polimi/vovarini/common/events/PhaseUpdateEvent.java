package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Phase;

public class PhaseUpdateEvent extends GameEvent{
  private Phase newPhase;

  public PhaseUpdateEvent(Object source, Phase newPhase){
    super(source);
    this.newPhase = newPhase;
  }

  public Phase getNewPhase() {
    return newPhase;
  }
}
