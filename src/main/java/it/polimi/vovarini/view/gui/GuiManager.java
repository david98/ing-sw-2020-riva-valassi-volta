package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.View;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.gui.controllers.GUIController;
import it.polimi.vovarini.view.gui.controllers.GodCardSelectionController;
import it.polimi.vovarini.view.gui.controllers.SpawnWorkerController;
import it.polimi.vovarini.view.gui.controllers.WaitController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiManager extends View {

    private static GuiManager instance = null;

    private GameClient client;

    private Stage stage;

    private Scene currentScene;
    private GUIController currentController;

    private boolean godSelectionStarted;
    private boolean placeWorkersStarted;

    private Thread guiEventListenerThread;

    private GuiManager() {
        super();

        data = new ViewData();

        instance = this;
        godSelectionStarted = false;
        placeWorkersStarted = false;
    }

    public static GuiManager getInstance() {
        if (instance == null)
            instance = new GuiManager();
        return instance;
    }

    @Override
    @GameEventListener
    public void handleNewPlayer(NewPlayerEvent e) {
        super.handleNewPlayer(e);
        Platform.runLater(() -> currentController.handleNewPlayer(e));
    }

    @Override
    @GameEventListener
    public void handleBoardUpdate(BoardUpdateEvent e) {
        data.setBoard(e.getNewBoard());
        Platform.runLater(() -> currentController.handleBoardUpdate(e));
    }

    @Override
    @GameEventListener
    public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {
        data.setCurrentPlayer(e.getNewPlayer());
        Platform.runLater(() -> currentController.handleCurrentPlayerUpdate(e));
    }

    @Override
    @GameEventListener
    public void handlePhaseUpdate(PhaseUpdateEvent e) {
        data.setCurrentPhase(e.getNewPhase());
        Platform.runLater(() -> currentController.handlePhaseUpdate(e));
    }

    @Override
    @GameEventListener
    public void handleGameStart(GameStartEvent e) {
        Platform.runLater(() ->
                setLayout(Settings.GAME_SCENE_FXML)
        );
    }

    @Override
    @GameEventListener
    public void handleGodSelectionStart(GodSelectionStartEvent e) {
        super.handleGodSelectionStart(e);

        if (e.getElectedPlayer().equals(data.getOwner())) {
            Platform.runLater(() -> setLayout(Settings.ELECTED_PLAYER_SCENE_PATH));
        } else {
            Platform.runLater(() -> {
                setLayout(Settings.WAIT_SCENE_FXML);
                ((WaitController) currentController).setWaitMessage("Waiting for " + e.getElectedPlayer().getNickname() + " to choose which" +
                        "God Cards will be available...");
            });
        }

        Platform.runLater(() -> currentController.handleGodSelectionStart(e));
    }

    @Override
    @GameEventListener
    public void handleSelectYourCard(SelectYourCardEvent e) {
        if (!godSelectionStarted) {
            Platform.runLater(() -> setLayout(Settings.GODCARD_SELECTION_SCENE_PATH));
            godSelectionStarted = true;
        }
        Platform.runLater(() -> currentController.handleSelectYourCard(e));
    }

    @Override
    @GameEventListener
    public void handleCardAssignment(CardAssignmentEvent e) {
        super.handleCardAssignment(e);
        Platform.runLater(() -> currentController.handleCardAssignment(e));
    }

    @Override
    @GameEventListener
    public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {
        if (!placeWorkersStarted) {
            Platform.runLater(() -> setLayout(Settings.SPAWN_WORKER_SCENE_PATH));
            placeWorkersStarted = true;
        }
        Platform.runLater(() -> currentController.handlePlaceYourWorkers(e));
    }

    @Override
    @GameEventListener
    public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e) {
        super.handlePlayerInfoUpdate(e);
        Platform.runLater(() -> currentController.handlePlayerInfoUpdate(e));
    }

    @Override
    @GameEventListener
    public void handleGodCardUpdate(GodCardUpdateEvent e) {
        super.handleGodCardUpdate(e);
        Platform.runLater(() -> currentController.handleGodCardUpdate(e));
    }

    @Override
    @GameEventListener
    public void handleVictory(VictoryEvent e) {
        Platform.runLater(() -> currentController.handleVictory(e));
    }

    @Override
    @GameEventListener
    public void handleLoss(LossEvent e) {
        Platform.runLater(() -> currentController.handleLoss(e));
    }

    public void gameSetup() {
        javafx.application.Application.launch(Gui.class);
        Settings.load();
    }

    /**
     * Sets a layout from FXML file
     *
     * @param path  path of the FXML file
     */
    public void setLayout(String path) {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(GuiManager.class.getResource(path));
        Pane pane;
        try {
            pane = loader.load();
            currentScene.setRoot(pane);
            this.currentController = loader.getController();
            stage.setMinWidth(0);
            stage.setMinHeight(0);
            stage.sizeToScene();
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createConnection(String nickname, String serverIP, int serverPort) throws IOException {
        client = new GameClient(serverIP, serverPort);
        client.raise(new RegistrationEvent(client.getIPv4Address(), nickname));
        data.setOwner(new Player(nickname));

        guiEventListenerThread = new Thread(new GuiEventListener(client));
        guiEventListenerThread.start();
    }

    public ViewData getData() {
        return data;
    }

    public GameClient getClient() {
        return client;
    }

    public int getNumberOfPlayers() {
        return data.getPlayerSet().size();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene scene) {
        this.currentScene = scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

