package it.polimi.vovarini.controller.events;


import java.util.EventObject;

public abstract class GameEvent extends EventObject {

  // private Player playerSource; (Capire se è necessario. Può darsi che sia raggiungibile tramite
  // RemoteView oppure tramite altri mezzi)

  public GameEvent(Object source) {
    super(source);
  }
}
