package it.polimi.vovarini.view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


public class WaitController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label label;

    private GuiManager guiManager;

    @FXML
    public void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setWaitController(this);
    }

    void changeLayout(String path) {
        GuiManager.setLayout(mainPane.getScene(), path);
    }

    void waitMessage(String electedPlayerNickname) {
        label.setText("Waiting for " + electedPlayerNickname + " to choose which God Cards will be available...");
    }


}
