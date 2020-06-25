package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.NewPlayerEvent;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public class RegistrationController extends GUIController {

    @FXML
    private Label error;

    @FXML
    private TextField nicknameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField portField;

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

        String nickname = nicknameField.getText();
        String serverIP = addressField.getText();
        String serverPort = portField.getText();

        if ((nickname == null) || !nickname.matches("[A-Za-z0-9_]{4,16}$")) {
            error.setText("Invalid nickname, type a new one.");
        } else {
            error.setText("");
            submit.setDisable(true);

            try {
                guiManager.createConnection(nickname, serverIP, Integer.parseInt(serverPort));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleNewPlayer(NewPlayerEvent e) {
        if (e.getNewPlayer().equals(GuiManager.getInstance().getData().getOwner())) {
            GuiManager.getInstance().setCurrentScene(mainPane.getScene());
            GuiManager.getInstance().setLayout(Settings.WAIT_SCENE_FXML);
        }
    }

    void onInvalidNickname() {
        error.setText("Nickname already exists!");
    }
}
