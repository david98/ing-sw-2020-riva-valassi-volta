package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.view.EventsForViewListener;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.input.KeyPressListener;
import it.polimi.vovarini.view.cli.elements.Renderable;

public abstract class Screen implements Renderable, EventsForViewListener, KeyPressListener {

  protected final GameClient client;

  protected final ViewData data;

  protected Screen(ViewData data, GameClient client) {
    this.client = client;
    this.data = data;
  }
}
