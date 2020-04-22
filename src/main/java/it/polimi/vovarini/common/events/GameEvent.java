package it.polimi.vovarini.common.events;

import java.util.EventObject;

public abstract class GameEvent extends EventObject {

  public GameEvent(Object source){
    super(source);
  }

}
