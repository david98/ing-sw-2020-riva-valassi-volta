package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.FirstPlayerEvent;
import it.polimi.vovarini.common.events.NumberOfPlayersChoiceEvent;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;

import java.util.stream.IntStream;


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

  @Override
  public void handle(FirstPlayerEvent e) {
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
    ((WaitController) guiManager.getCurrentController()).setWaitMessage("Waiting for other players to connect...");
  }
}
