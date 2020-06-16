package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.View;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.gui.controllers.*;
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

    private RegistrationController registrationController;
    private WaitController waitController;
    private ElectedPlayerController electedPlayerController;
    private GodCardSelectionController godCardSelectionController;
    private SpawnWorkerController spawnWorkerController;
    private GameController gameController;

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

        if(e.getNewPlayer().equals(data.getOwner())) {
            registrationController.onConnectionResponse();
        }

    }

    @Override
    @GameEventListener
    public void handleBoardUpdate(BoardUpdateEvent e) {
        data.setBoard(e.getNewBoard());
        if(gameController == null) {
            Platform.runLater(() -> spawnWorkerController.boardUpdate());
        }
    }

    @Override
    @GameEventListener
    public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {
        data.setCurrentPlayer(e.getNewPlayer());
        // se sei in GameScene, aggiorna il currentPlayer a video
    }

    @Override
    @GameEventListener
    public void handlePhaseUpdate(PhaseUpdateEvent e) {
        data.setCurrentPhase(e.getNewPhase());
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
        Player[] players = e.getPlayers();
        for (int i = 0; i < players.length; i++){
            for (Player p: data.getPlayerSet()){
                if (players[i].equals(p)){
                    players[i] = p;
                }
            }
        }
        data.getPlayerSet().clear();
        for (Player p: players){
            data.addPlayer(p);
        }
        data.setCurrentPlayer(e.getElectedPlayer());

        if (e.getElectedPlayer().equals(data.getOwner())) {
            Platform.runLater(() -> waitController.changeLayout("/fxml/electedPlayerScene.fxml"));
            Platform.runLater(() -> electedPlayerController.addImages(e.getAllGods()));
        } else {
            Platform.runLater(() -> waitController.waitMessage(e.getElectedPlayer().getNickname()));
        }
    }

    @Override
    @GameEventListener
    public void handleSelectYourCard(SelectYourCardEvent e) {
        if(godCardSelectionController == null) {
            Platform.runLater(() -> waitController.changeLayout("/fxml/godCardSelectionScene.fxml"));
            Platform.runLater(() -> godCardSelectionController.addImages(e.getGodsLeft(), !e.getTargetPlayer().equals(data.getOwner())));
        } else {
            Platform.runLater(() -> godCardSelectionController.changeVisibility(e.getGodsLeft(), !e.getTargetPlayer().equals(data.getOwner())));
        }
    }

    @Override
    @GameEventListener
    public void handleCardAssignment(CardAssignmentEvent e) {
        for (Player p: data.getPlayerSet()){
            if (p.equals(e.getTargetPlayer())){
                e.getAssignedCard().setGameData(data);
                p.setGodCard(e.getAssignedCard());
            }
        }
        Platform.runLater(() -> godCardSelectionController.showChoice(e.getTargetPlayer(), e.getAssignedCard()));
    }

    @Override
    @GameEventListener
    public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {

        if(spawnWorkerController == null) {
            Platform.runLater(() -> godCardSelectionController.changeLayout());
            Platform.runLater(() -> spawnWorkerController.addImages(data.getPlayers()));
        }

        Platform.runLater(() -> spawnWorkerController.changeVisibility(!e.getTargetPlayer().equals(data.getOwner()), e.getTargetPlayer().getNickname()));
    }

    @Override
    @GameEventListener
    public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e) {
        super.handlePlayerInfoUpdate(e);
        currentController.handlePlayerInfoUpdate(e);
    }

    @Override
    @GameEventListener
    public void handleGodCardUpdate(GodCardUpdateEvent e) {
        super.handleGodCardUpdate(e);
        currentController.handleGodCardUpdate(e);
    }

    @Override
    @GameEventListener
    public void handleVictory(VictoryEvent e) {
        currentController.handleVictory(e);
    }

    /*public void render(){
        console.clear();
        console.println(currentScreen.render());
    }

    public void handleInput() throws IOException{
        int input = console.getReader().read();
        Key key = KeycodeToKey.map.get(input);
        if (key != null) {
            currentScreen.handleKeyPress(key);
        }
    }*/

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

    public void setRegistrationController(RegistrationController registrationController) {
        this.registrationController = registrationController;
    }

    public void setWaitController(WaitController waitController) {
        this.waitController = waitController;
    }

    public void setElectedPlayerController(ElectedPlayerController electedPlayerController) {
        this.electedPlayerController = electedPlayerController;
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
