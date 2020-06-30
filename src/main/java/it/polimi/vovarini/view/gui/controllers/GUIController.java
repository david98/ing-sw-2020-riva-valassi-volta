package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.view.EventsForViewListener;
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
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(CurrentPlayerChangedEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(PhaseUpdateEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(GameStartEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(NewPlayerEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(GodSelectionStartEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(SelectYourCardEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(CardAssignmentEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(PlaceYourWorkersEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(PlayerInfoUpdateEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(GodCardUpdateEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(VictoryEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(LossEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(AbruptEndEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(FirstPlayerEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(RegistrationStartEvent e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handle(InvalidNicknameEvent e) {
    throw new UnsupportedOperationException();
  }
}
