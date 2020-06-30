package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.common.network.server.Server;
import it.polimi.vovarini.view.EventsForViewListener;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.Renderable;
import it.polimi.vovarini.view.cli.input.Key;
import it.polimi.vovarini.view.cli.input.KeyPressListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Screen implements Renderable, EventsForViewListener, KeyPressListener {

  protected static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  protected final GameClient client;

  protected final ViewData data;

  protected boolean handlesInput;
  protected boolean needsRender;

  protected Screen(ViewData data, GameClient client) {
    this.client = client;
    this.data = data;
    this.handlesInput = true;
    this.needsRender = true;
  }

  public boolean isHandlesInput() {
    return handlesInput;
  }

  public boolean isNeedsRender() {
    return needsRender;
  }

  private void methodNotImplemented() {
    LOGGER.log(Level.FINE, "This method hasn't been overridden by this controller");
  }

  @Override
  public void handle(BoardUpdateEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(CurrentPlayerChangedEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(PhaseUpdateEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(GameStartEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(NewPlayerEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(GodSelectionStartEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(SelectYourCardEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(CardAssignmentEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(PlaceYourWorkersEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(VictoryEvent e) {
    methodNotImplemented();  }

  @Override
  public void handleKeyPress(Key key) {
    methodNotImplemented();  }

  @Override
  public void handle(PlayerInfoUpdateEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(GodCardUpdateEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(LossEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(AbruptEndEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(FirstPlayerEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(RegistrationStartEvent e) {
    methodNotImplemented();  }

  @Override
  public void handle(InvalidNicknameEvent e) {
    methodNotImplemented();  }
}
