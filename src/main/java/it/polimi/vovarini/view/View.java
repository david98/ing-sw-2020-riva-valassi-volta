package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.GameEventManager;

public abstract class View implements EventsForViewListener {
  protected ViewData data;

  public View(){
    GameEventManager.bindListeners(this);
    System.out.println("bind");
    data = new ViewData();
  }
}
