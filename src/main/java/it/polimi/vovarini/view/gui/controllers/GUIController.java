package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.server.Server;
import it.polimi.vovarini.view.EventsForViewListener;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GUIController implements EventsForViewListener {

  protected static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  @FXML
  protected BorderPane mainPane;

  @FXML
  public void initialize() {
    /* altre eventuali inizializzazioni */
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
    methodNotImplemented();
  }

  @Override
  public void handle(PhaseUpdateEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(GameStartEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(NewPlayerEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(GodSelectionStartEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(SelectYourCardEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(CardAssignmentEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(PlaceYourWorkersEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(PlayerInfoUpdateEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(GodCardUpdateEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(VictoryEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(LossEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(AbruptEndEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(FirstPlayerEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(RegistrationStartEvent e) {
    methodNotImplemented();
  }

  @Override
  public void handle(InvalidNicknameEvent e) {
    methodNotImplemented();
  }
}
