package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.FirstPlayerEvent;
import it.polimi.vovarini.common.events.NumberOfPlayersChoiceEvent;
import it.polimi.vovarini.common.events.RegistrationEvent;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.stream.IntStream;

public class RegistrationController extends GUIController {

    @FXML
    private Label error;

    @FXML
    private TextField nicknameField;

    @FXML
    private Button submit;

    private GuiManager guiManager;

    @FXML
    public void initialize() {
      guiManager = GuiManager.getInstance();

      nicknameField.setOnKeyPressed(keyEvent -> {
        if (keyEvent.getCode() == KeyCode.ENTER) {
          onButtonClick(null);
        }
      });
    }

    @FXML
    private void onButtonClick(ActionEvent event) {

      String nickname = nicknameField.getText();

      if ((nickname == null) || !nickname.matches("[A-Za-z0-9_]{4,16}$")) {
        error.setText("Invalid nickname, type a new one.");
      } else {
        error.setText("");
        submit.setDisable(true);

        guiManager.getData().setOwner(new Player(nickname));
        guiManager.getClient().raise(new RegistrationEvent("player", nickname));
        guiManager.setLayout(Settings.WAIT_SCENE_FXML);
        ((WaitController)guiManager.getCurrentController()).setWaitMessage("Waiting for all players to register...");
      }
    }
}
