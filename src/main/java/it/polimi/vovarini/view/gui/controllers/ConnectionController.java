package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.FirstPlayerEvent;
import it.polimi.vovarini.common.events.NewPlayerEvent;
import it.polimi.vovarini.common.events.NumberOfPlayersChoiceEvent;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.stream.IntStream;

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
                error.setText("Server unreachable or game not initialized yet.");
                submit.setDisable(false);
            }
        }
    }


    @Override
    public void handleFirstPlayer(FirstPlayerEvent e) {
        super.handleFirstPlayer(e);
        // items for the dialog
        String nPlayers[] = IntStream.range(Game.MIN_PLAYERS, Game.MAX_PLAYERS + 1).mapToObj(i -> "" + i).toArray(String[]::new);

        // create a choice dialog
        ChoiceDialog<String> d = new ChoiceDialog<>(nPlayers[0], nPlayers);
        d.setTitle("Game size choice");
        d.setHeaderText("Game size choice");
        d.setContentText("Choose the number of players for this game");
        d.showAndWait();
        guiManager.getClient().raise(new NumberOfPlayersChoiceEvent("firstPlayer", Integer.parseInt(d.getSelectedItem())));
        guiManager.setLayout(Settings.WAIT_SCENE_FXML);
        ((WaitController)guiManager.getCurrentController()).setWaitMessage("Waiting for other players to connect...");
    }
}
