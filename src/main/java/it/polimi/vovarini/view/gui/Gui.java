package it.polimi.vovarini.view.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Gui extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Pane()));

        stage.sizeToScene();

        GuiManager.getInstance().setStage(stage);
        GuiManager.getInstance().setCurrentScene(stage.getScene());
        GuiManager.getInstance().setLayout(Settings.REGISTRATION_SCENE_PATH);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setTitle("Santorini");
        stage.setFullScreen(true);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        GuiManager.getInstance().stopEventListener();
    }

    public static void main(String[] args) {
        launch(args);
    }
}