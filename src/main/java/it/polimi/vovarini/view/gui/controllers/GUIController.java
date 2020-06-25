package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.view.EventsForViewListener;
import it.polimi.vovarini.view.gui.GuiManager;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class GUIController implements EventsForViewListener {

  @FXML
  protected BorderPane mainPane;

  @FXML
  public void initialize() {
    /* altre eventuali inizializzazioni */
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
  public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e) {

  }

  @Override
  public void handleGodCardUpdate(GodCardUpdateEvent e) {

  }

  @Override
  public void handleVictory(VictoryEvent e) {

  }

  @Override
  public void handleLoss(LossEvent e) {

  }
}
