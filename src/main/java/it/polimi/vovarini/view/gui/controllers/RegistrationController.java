package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

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
        error.setText(Settings.INVALID_NICKNAME);
      } else {
        error.setText("");
        submit.setDisable(true);

        guiManager.getData().setOwner(new Player(nickname));
        guiManager.getClient().raise(new RegistrationEvent("player", nickname));
      }
    }

    @Override
    public void handle(InvalidNicknameEvent e) {
        super.handle(e);

        if(guiManager.getData().getOwner() != null && e.getNickname().equals(guiManager.getData().getOwner().getNickname())) {
            if(e.getErrorCode() == 0) {
                error.setText(Settings.DUPLICATE_NICKNAME);
            } else {
                error.setText(Settings.INVALID_NICKNAME);
            }

            submit.setDisable(false);
        }
    }

    @Override
    public void handle(NewPlayerEvent e) {
        super.handle(e);
        if(e.getNewPlayer().equals(guiManager.getData().getOwner())) {
            guiManager.setLayout(Settings.WAIT_SCENE_FXML);
            ((WaitController)guiManager.getCurrentController()).setWaitMessage("Waiting for all players to register...");
        }
    }
}
