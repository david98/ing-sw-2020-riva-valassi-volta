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
  public void handle(PlayerInfoUpdateEvent e) {

  }

  @Override
  public void handle(GodCardUpdateEvent e) {

  }

  @Override
  public void handle(VictoryEvent e) {

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
