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

import java.io.IOException;

public class GuiManager extends View {

    private static GuiManager instance = null;

    private GameClient client;

    private Scene currentScene;
    private GUIController currentController;

    private GodCardSelectionController godCardSelectionController;
    private SpawnWorkerController spawnWorkerController;

    private Thread guiEventListenerThread;

    private GuiManager() {
        super();

        data = new ViewData();

        instance = this;

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
        // se sei in GameScene, aggiorna il currentPlayer a video
    }

    @Override
    @GameEventListener
    public void handlePhaseUpdate(PhaseUpdateEvent e) {
        data.setCurrentPhase(e.getNewPhase());
        Platform.runLater(() -> currentController.handlePhaseUpdate(e));
        //se sei in GameScene, aggiorna currentPhase a video
    }

    @Override
    @GameEventListener
    public void handleGameStart(GameStartEvent e) {
        startMatch();
    }

    @Override
    @GameEventListener
    public void handleGodSelectionStart(GodSelectionStartEvent e) {
        super.handleGodSelectionStart(e);

        if (e.getElectedPlayer().equals(data.getOwner())) {
            setLayout("/fxml/electedPlayerScene.fxml");
        } else {
            setLayout("/fxml/waitScene.fxml");
            // TODO: find a better way
            ((WaitController) currentController).setWaitMessage("Waiting for " + e.getElectedPlayer().getNickname() + " to choose which" +
                    "God Cards will be available...");
        }

        Platform.runLater(() -> currentController.handleGodSelectionStart(e));
    }

    @Override
    @GameEventListener
    public void handleSelectYourCard(SelectYourCardEvent e) {
        // TODO: find a better way
        if(godCardSelectionController == null) {
            setLayout("/fxml/godCardSelectionScene.fxml");
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
        // TODO: find a better way
        if(spawnWorkerController == null) {
            setLayout("/fxml/spawnWorkerScene.fxml");
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

    public void startMatch() {

        System.out.println("Iniziamo la partita!");
        //setto scena iniziale
        //currentScreen = new MatchScreen(data, client);
        //gameLoop();
    }

    public void gameSetup() {
        javafx.application.Application.launch(Gui.class);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.currentController = loader.getController();
    }

    public void createConnection(String nickname, String serverIP, int serverPort) throws IOException {
        client = new GameClient(serverIP, serverPort);
        client.raise(new RegistrationEvent(client.getIPv4Address(), nickname));
        data.setOwner(new Player(nickname));

        guiEventListenerThread = new Thread(new GuiEventListener(client));
        guiEventListenerThread.start();
    }

    public void setGodCardSelectionController(GodCardSelectionController godCardSelectionController) {
        this.godCardSelectionController = godCardSelectionController;
    }

    public void setSpawnWorkerController(SpawnWorkerController spawnWorkerController) {
        this.spawnWorkerController = spawnWorkerController;
    }

    public ViewData getData() {
        return data;
    }

    public GameClient getClient() { return client; }

    public int getNumberOfPlayers() {
        return data.getPlayerSet().size();
    }

    public Scene getCurrentScene() {
         return currentScene;
    }

    public void setCurrentScene(Scene scene) {
         this.currentScene = scene;
     }
}
