package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.common.network.server.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for events from the server.
 */
public class GuiEventListener implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  private boolean running;
  private final GameClient client;

  /**
   * Builds a new GuiEventListner and sets it as running
   * @param client is the client host who triggers the events
   */
  public GuiEventListener(GameClient client) {
    running = true;
    this.client = client;
  }

  /**
   * Sets running as false, stopping the GameEventListener from listening
   */
  public void stop() {
    running = false;
  }

  /**
   * Runs the GameEventListner
   */
  @Override
  public void run() {
    while (running) {
      try {
        GameEvent evtFromServer = client.getServerEvents().take();
        LOGGER.log(Level.INFO, "Received event: " + evtFromServer.toString());
        GameEventManager.raise(evtFromServer);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        running = false;
      }
    }
  }
}
