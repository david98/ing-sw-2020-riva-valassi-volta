package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.model.godcards.GodName;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.util.Arrays;

public class MainElectedPlayer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/electedPlayerScene.fxml"));
        stage.setTitle("Available Cards choice");
        stage.setScene(new Scene(root));
        stage.setMinHeight(600);
        stage.setMinWidth(1000);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
