package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.common.network.server.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for events from the server.
 *
 * @author Davide Volta
 */
public class GuiEventListener implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  private boolean running;
  private final GameClient client;

  /**
   * Creates a new GuiEventListener using the given client.
   * @param client The client through which communication with the server happens.
   */
  public GuiEventListener(GameClient client) {
    running = true;
    this.client = client;
  }

  /**
   * Stops the GameEventListener.
   */
  public void stop() {
    running = false;
  }

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
