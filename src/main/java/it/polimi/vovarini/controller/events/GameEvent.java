package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Player;

import java.util.EventObject;

public abstract class GameEvent extends EventObject {

  //informazione fornita dal client in base all'indirizzo ip associato al giocatore
  private final Player playerSource;

  public GameEvent(Object source, Player player) {
    super(source);
    playerSource = player;
  }

  public Player getPlayerSource() {
    return playerSource;
  }
}
