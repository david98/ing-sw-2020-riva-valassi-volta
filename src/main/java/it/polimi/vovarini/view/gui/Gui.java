package it.polimi.vovarini.view.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Gui extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Pane()));

        stage.sizeToScene();

        GuiManager.getInstance().setCurrentScene(stage.getScene());
        GuiManager.getInstance().setLayout("/fxml/registrationScene.fxml");
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}