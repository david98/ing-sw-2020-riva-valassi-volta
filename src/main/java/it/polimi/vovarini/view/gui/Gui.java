package it.polimi.vovarini.view.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Gui extends Application {

    private final KeyCombination FullScreenKeyCombo =
            new KeyCodeCombination(KeyCode.ENTER, KeyCombination.ALT_DOWN);

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Pane()));

        stage.sizeToScene();

        GuiManager.getInstance().setStage(stage);
        GuiManager.getInstance().setCurrentScene(stage.getScene());
        GuiManager.getInstance().setLayout(Settings.SPAWN_WORKER_SCENE_PATH);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setTitle("Santorini");
        stage.setFullScreen(true);

        stage.maximizedProperty().addListener((obs, wasMaximized, willBeMaximized) -> {
            if (wasMaximized && !willBeMaximized) {
                stage.sizeToScene();
                stage.setMinWidth(stage.getWidth());
                stage.setMinHeight(stage.getHeight());
            }
        });

        // toggle full-screen when alt + enter is pressed
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if(FullScreenKeyCombo.match(event)) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.setOnCloseRequest((windowEvent) -> {
                Platform.exit();
                System.exit(0);
            });
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