package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.view.gui.GuiManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

/**
 * Controller for the connection scene.
 */
public class ConnectionController extends GUIController {

  @FXML
  private Label error;

  @FXML
  private TextField addressField;

  @FXML
  private TextField portField;

  @FXML
  private Button submit;

  private GuiManager guiManager;

  @FXML
  @Override
  public void initialize() {
    guiManager = GuiManager.getInstance();

    addressField.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        onButtonClick(null);
      }
    });

    portField.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        onButtonClick(null);
      }
    });
  }

  @FXML
  private void onButtonClick(ActionEvent event) {

    String serverIP = addressField.getText();
    String serverPort = portField.getText();

    if (serverIP == null && serverPort == null) {
      error.setText("Invalid nickname, type a new one.");
    } else {
      error.setText("");
      submit.setDisable(true);

      try {
        guiManager.createConnection(serverIP, Integer.parseInt(serverPort));
      } catch (IOException e) {
        error.setText("Server unreachable or first player still choosing size.");
        submit.setDisable(false);
      }
    }
  }
}
