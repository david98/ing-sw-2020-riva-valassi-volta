package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.view.gui.GuiManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class WaitController extends GUIController {

    @FXML
    private Label label;

    private GuiManager guiManager;

    @FXML
    public void initialize() {
        guiManager = GuiManager.getInstance();
    }

    public void setWaitMessage(String message) {
        label.setText(message);
    }


}
