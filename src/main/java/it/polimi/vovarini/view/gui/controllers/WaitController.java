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
        if (!GuiManager.isPlayingBackground()) {
            GuiManager.playBackgroundSound("bgm/reg.mp3", true);
        }
    }

    public void setWaitMessage(String message) {
        label.setText(message);
    }


}
