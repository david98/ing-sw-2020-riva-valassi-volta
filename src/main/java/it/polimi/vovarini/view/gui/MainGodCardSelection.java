package it.polimi.vovarini.view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class MainGodCardSelection extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/godCardSelectionScene.fxml"));
        stage.setTitle("Cards choice");
        stage.setScene(new Scene(root));
        stage.setMinHeight(300);
        stage.setMinWidth(500);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
