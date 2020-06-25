package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.network.GameClient;

/**
 * Listens for events from the server.
 */
public class GuiEventListener implements Runnable {

  private boolean running;
  private final GameClient client;

  public GuiEventListener(GameClient client) {
    running = true;
    this.client = client;
  }

  public void stop() {
    running = false;
  }

  @Override
  public void run() {
    while (running) {
      try {
        GameEvent evtFromServer = client.getServerEvents().take();
        System.out.println(evtFromServer.toString());
        GameEventManager.raise(evtFromServer);
      } catch (InterruptedException e){
        Thread.currentThread().interrupt();
        running = false;
      }
    }
  }
}
