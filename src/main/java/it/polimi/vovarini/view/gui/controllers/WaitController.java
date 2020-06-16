package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.controllers.GUIController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


public class WaitController extends GUIController {

    @FXML
    private Label label;

    private GuiManager guiManager;

    @FXML
    public void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setWaitController(this);
    }

    public void changeLayout(String path) {
        GuiManager.getInstance().setCurrentScene(mainPane.getScene());
        GuiManager.getInstance().setLayout(path);
    }

    public void waitMessage(String electedPlayerNickname) {
        label.setText("Waiting for " + electedPlayerNickname + " to choose which God Cards will be available...");
    }


}
