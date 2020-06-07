package it.polimi.vovarini.view.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Gui extends Application {

    @Override
    public void start(Stage stage) {
        stage.setMaximized(true);
        //.setFullScreen(true);
        //stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        stage.setScene(new Scene(new Pane()));

        GuiManager.setLayout(stage.getScene(), "/fxml/registrationScene.fxml");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}