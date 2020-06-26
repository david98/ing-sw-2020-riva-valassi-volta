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
  public void handleBoardUpdate(BoardUpdateEvent e) {

  }

  @Override
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {

  }

  @Override
  public void handlePhaseUpdate(PhaseUpdateEvent e) {

  }

  @Override
  public void handleGameStart(GameStartEvent e) {

  }

  @Override
  public void handleNewPlayer(NewPlayerEvent e) {

  }

  @Override
  public void handleGodSelectionStart(GodSelectionStartEvent e) {

  }

  @Override
  public void handleSelectYourCard(SelectYourCardEvent e) {

  }

  @Override
  public void handleCardAssignment(CardAssignmentEvent e) {

  }

  @Override
  public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {

  }

  @Override
  public void handleVictory(VictoryEvent e) {

  }

  @Override
  public void handleKeyPress(Key key) {

  }

  @Override
  public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e) {

  }

  @Override
  public void handleGodCardUpdate(GodCardUpdateEvent e) {

  }

  @Override
  public void handleLoss(LossEvent e) {
  
  }

  @Override
  public void handleAbruptEnd(AbruptEndEvent e) {

  }

  @Override
  public void handleFirstPlayer(FirstPlayerEvent e) {

  }

  @Override
  public void handleRegistrationStart(RegistrationStartEvent e) {

  }
}
