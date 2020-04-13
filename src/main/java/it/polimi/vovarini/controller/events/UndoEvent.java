package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Player;

public class UndoEvent extends GameEvent {

  public UndoEvent(Object source, Player player) {
    super(source, player);
  }
}
