package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.RegistrationEvent;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class RegistrationController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label label;

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
        guiManager.setRegistrationController(this);
    }


    @FXML
    private void onButtonClick(ActionEvent event) {

        String nickname = nicknameField.getText();
        String serverIP = addressField.getText();
        String serverPort = portField.getText();

        if ((nickname == null) || !nickname.matches("[A-Za-z0-9_]{4,16}$")) {
            label.setText("Invalid nickname, type a new one.");
        } else {
            label.setText("");
            submit.setDisable(true);

            try {
                guiManager.createConnection(nickname, serverIP, Integer.parseInt(serverPort));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void onConnectionResponse() {
        GuiManager.setLayout(mainPane.getScene(), "/fxml/waitScene.fxml");
    }

    void onInvalidNickname() {
        label.setText("Nickname already exists!");
    }
}
