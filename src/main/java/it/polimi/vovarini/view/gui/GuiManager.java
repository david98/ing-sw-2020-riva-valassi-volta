package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.View;
import it.polimi.vovarini.view.ViewData;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class GuiManager extends View {

    private static GuiManager instance = null;

    private GameClient client;
    private boolean running;
    private boolean waiting;

    private RegistrationController registrationController;
    private WaitController waitController;
    private ElectedPlayerController electedPlayerController;
    private GodCardSelectionController godCardSelectionController;

    private GuiManager() {
        super();

        data = new ViewData();

        running = true;

        waiting = true;

        instance = this;
    }

    public static GuiManager getInstance() {
        if (instance == null)
            instance = new GuiManager();
        return instance;
    }

    public GameClient getClient() {
        return client;
    }

    private void waitForEvent(){
        try {
            GameEvent evtFromServer = client.getServerEvents().take();
            GameEventManager.raise(evtFromServer);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @GameEventListener
    public void handleNewPlayer(NewPlayerEvent e) {
        super.handleNewPlayer(e);

        System.out.println("nuovo giocatore!");
        // se sono stato accettato, passo alla schermata di wait
        if(e.getNewPlayer().equals(data.getOwner())) {
            System.out.println("Ti ordino di passare alla wait");
            Platform.runLater(() -> registrationController.onConnectionResponse());
            System.out.println("Sei passato alla wait");
        }

    }

    @Override
    @GameEventListener
    public void handleBoardUpdate(BoardUpdateEvent e) {

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
        waiting = false;
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
            //gameLoop();
        } else {
            Platform.runLater(() -> waitController.waitMessage(e.getElectedPlayer().getNickname()));
            //waitForEvent();
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

        /*

        if (e.getTargetPlayer().equals(data.getOwner())){
            currentScreen = new GodCardSelectionScreen(data, client, Arrays.asList(e.getGodsLeft()));
            gameLoop();
        } else {
            currentScreen = new WaitScreen(data, client,
                    "Waiting for all players to choose their card...");
            render();
            waitForEvent();
        }

        */
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
    }

    @Override
    @GameEventListener
    public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {

        /*

        if (e.getTargetPlayer().equals(data.getOwner())){
          currentScreen = new SpawnWorkersScreen(data, client);
          gameLoop();
        } else {
          // maybe we should show the board
          currentScreen = new WaitScreen(data, client,
                  "Waiting for all players to place their workers...");

          render();
          waitForEvent();
        }

         */
    }

    @Override
    @GameEventListener
    public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e) {
        super.handlePlayerInfoUpdate(e);
        //currentScreen.handlePlayerInfoUpdate(e);
    }

    @Override
    @GameEventListener
    public void handleGodCardUpdate(GodCardUpdateEvent e) {
        super.handleGodCardUpdate(e);
        //currentScreen.handleGodCardUpdate(e);
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

        // METTO IN SCENA GAMESCENE

        System.out.println("Carte scelte, finalmente si gioca!");
        //setto scena iniziale
        //currentScreen = new MatchScreen(data, client);
        //gameLoop();
    }

    public void gameSetup() {
        javafx.application.Application.launch(Gui.class);
    }

    /**
     * Sets a layout form FXML file and returns the scene controller
     *
     * @param scene scene where to set the layout
     * @param path  path of the FXML file
     * @param <T>   type of the scene controller
     * @return the scene controller
     */
    static <T> T setLayout(Scene scene, String path) {

        FXMLLoader loader = new FXMLLoader(GuiManager.class.getResource(path));
        Pane pane;
        try {
            pane = loader.load();
            scene.setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loader.getController();
    }

    public void gameLoop(){
        //render();
        while (running) {
            GameEvent evt;
            // consume events from the server
            while ( client.getServerEvents().peek() != null) {
                evt = client.getServerEvents().poll();
                GameEventManager.raise(evt);
            }
            try {
                //render();
                if (data.getOwner().equals(data.getCurrentPlayer())) { // && currentScreen.isHandlesInput()) {
                    //handleInput();
                } else {
                    // wait for event
                    GameEventManager.raise(client.getServerEvents().take());
                }
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public void createConnection(String nickname, String serverIP, int serverPort) throws IOException {
        client = new GameClient(serverIP, serverPort);
        client.raise(new RegistrationEvent(client.getIPv4Address(), nickname));
        data.setOwner(new Player(nickname));

        attendiZioCane();
    }

    public void attendiZioCane() {
        System.out.println("attendo ziocane");
        while (waiting) {
            try {
                GameEvent evtFromServer = client.getServerEvents().take();
                System.out.println(evtFromServer.toString());
                GameEventManager.raise(evtFromServer);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
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

    public ViewData getData() {
        return data;
    }

    public int numerOfPlayers() {
        return data.getPlayerSet().size();
    }

    public void stop(){
        running = false;
    }

}
