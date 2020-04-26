package it.polimi.vovarini.common.events;

import java.io.Serializable;
import java.util.EventObject;

public abstract class GameEvent extends EventObject implements Serializable {

  public GameEvent(Object source){
    super(source);
  }

}
