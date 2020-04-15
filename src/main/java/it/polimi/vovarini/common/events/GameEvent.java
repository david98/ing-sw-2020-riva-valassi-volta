package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

import java.util.EventObject;

public abstract class GameEvent extends EventObject {

  public GameEvent(Object source){
    super(source);
  }

}
