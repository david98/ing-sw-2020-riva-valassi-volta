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

  protected Screen(ViewData data, GameClient client) {
    this.client = client;
    this.data = data;
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
  public void handleKeyPress(Key key) {

  }
}
