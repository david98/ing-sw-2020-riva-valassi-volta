package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Player;

public class NextPlayerEvent extends GameEvent {

  public NextPlayerEvent(Object source, Player player) {
    super(source, player);
  }
}
