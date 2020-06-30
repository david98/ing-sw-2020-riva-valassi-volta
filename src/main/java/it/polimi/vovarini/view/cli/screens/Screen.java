package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.view.EventsForViewListener;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.Renderable;
import it.polimi.vovarini.view.cli.input.Key;
import it.polimi.vovarini.view.cli.input.KeyPressListener;

public abstract class Screen implements Renderable, EventsForViewListener, KeyPressListener {

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

  @Override
  public void handle(BoardUpdateEvent e) {

  }

  @Override
  public void handle(CurrentPlayerChangedEvent e) {

  }

  @Override
  public void handle(PhaseUpdateEvent e) {

  }

  @Override
  public void handle(GameStartEvent e) {

  }

  @Override
  public void handle(NewPlayerEvent e) {

  }

  @Override
  public void handle(GodSelectionStartEvent e) {

  }

  @Override
  public void handle(SelectYourCardEvent e) {

  }

  @Override
  public void handle(CardAssignmentEvent e) {

  }

  @Override
  public void handle(PlaceYourWorkersEvent e) {

  }

  @Override
  public void handle(VictoryEvent e) {

  }

  @Override
  public void handleKeyPress(Key key) {

  }

  @Override
  public void handle(PlayerInfoUpdateEvent e) {

  }

  @Override
  public void handle(GodCardUpdateEvent e) {

  }

  @Override
  public void handle(LossEvent e) {

  }

  @Override
  public void handle(AbruptEndEvent e) {

  }

  @Override
  public void handle(FirstPlayerEvent e) {

  }

  @Override
  public void handle(RegistrationStartEvent e) {

  }

  @Override
  public void handle(InvalidNicknameEvent e) {

  }
}
