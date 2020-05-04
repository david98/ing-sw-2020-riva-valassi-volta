package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.view.EventsForViewListener;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.KeyPressListener;
import it.polimi.vovarini.view.cli.elements.Renderable;

public abstract class Screen implements Renderable, EventsForViewListener, KeyPressListener {
  protected final ViewData data;

  protected Screen(ViewData data) {
    this.data = data;
  }
}
