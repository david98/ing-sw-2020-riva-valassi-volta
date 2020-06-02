package it.polimi.vovarini.view.cli;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.view.View;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.console.Console;
import it.polimi.vovarini.view.cli.console.FullScreenConsole;
import it.polimi.vovarini.view.cli.input.Key;
import it.polimi.vovarini.view.cli.input.KeycodeToKey;
import it.polimi.vovarini.view.cli.screens.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class GameView extends View {

  private Screen currentScreen;

  private final Console console;

  private boolean running;

  public GameView(String serverIP, int serverPort) throws IOException {
    super();

    data = new ViewData();

    console = new FullScreenConsole();

    client = new GameClient(serverIP, serverPort);

    running = true;
  }

  private void waitForEvent(){
    try {
      GameEvent evtFromServer = client.getServerEvents().take();
      GameEventManager.raise(evtFromServer);
    } catch (InterruptedException e){
      Thread.currentThread().interrupt();
    }
  }


  @GameEventListener
  public void handleBoardUpdate(BoardUpdateEvent e){
    data.setBoard(e.getNewBoard());
    if (currentScreen != null) {
      currentScreen.handleBoardUpdate(e);
    }
  }

  @GameEventListener
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e){
    data.setCurrentPlayer(e.getNewPlayer());
    if (currentScreen != null) {
      currentScreen.handleCurrentPlayerUpdate(e);
    }
  }

  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e){
    data.setCurrentPhase(e.getNewPhase());
    if (currentScreen != null) {
      currentScreen.handlePhaseUpdate(e);
    }
  }

  @GameEventListener
  public void handleGameStart(GameStartEvent e){
    startMatch();
  }

  @Override
  @GameEventListener
  public void handleNewPlayer(NewPlayerEvent e) {
    super.handleNewPlayer(e);
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
      currentScreen = new ElectedPlayerScreen(data, client, Arrays.asList(e.getAllGods()));
      gameLoop();
    } else {
      currentScreen = new WaitScreen(data, client,
              "Waiting for elected player to choose which God Cards will be available...");
      render();
      waitForEvent();
    }
  }

  @Override
  @GameEventListener
  public void handleSelectYourCard(SelectYourCardEvent e) {
    if (e.getTargetPlayer().equals(data.getOwner())){
      currentScreen = new GodCardSelectionScreen(data, client, Arrays.asList(e.getGodsLeft()));
      gameLoop();
    } else {
      currentScreen = new WaitScreen(data, client,
              "Waiting for all players to choose their card...");
      render();
      waitForEvent();
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
  }

  @Override
  @GameEventListener
  public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {
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
  }

  @Override
  @GameEventListener
  public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e) {
    super.handlePlayerInfoUpdate(e);
    currentScreen.handlePlayerInfoUpdate(e);
  }

  @Override
  @GameEventListener
  public void handleGodCardUpdate(GodCardUpdateEvent e) {
    super.handleGodCardUpdate(e);
    currentScreen.handleGodCardUpdate(e);
  }

  @Override
  @GameEventListener
  public void handleVictory(VictoryEvent e) {
    //TODO

  }

  public void render(){
    if (currentScreen.isNeedsRender()) {
      console.clear();
      console.println(currentScreen.render());
    }
  }

  public void handleInput() throws IOException{
    int input = console.getReader().read();
    Key key = KeycodeToKey.map.get(input);
    if (key != null) {
      currentScreen.handleKeyPress(key);
    }
  }

  public void startMatch() {
    currentScreen = new MatchScreen(data, client);
    gameLoop();
  }

  public void gameSetup(){
    // ask for nickname
    Scanner sc = console.getScanner();
    console.clear();
    String nickname;
    System.out.print("Type your nickname: ");
    nickname = sc.next();
    while ((nickname == null) || !nickname.matches("[A-Za-z0-9_]{4,16}$")){
      System.out.print("Invalid nickname, type a new one: ");
      nickname = sc.next();
    }
    client.raise(new RegistrationEvent(client.getIPv4Address(), nickname));
    data.setOwner(new Player(nickname));
    System.out.println("Now waiting for other players...");
    console.enterRawMode();
    while (running) {
      try {
        GameEvent evtFromServer = client.getServerEvents().take();
        GameEventManager.raise(evtFromServer);
      } catch (InterruptedException e){
        Thread.currentThread().interrupt();
      }
    }
  }

  public void gameLoop(){
    render();
    while (running) {
      GameEvent evt;
      // consume events from the server
      while ( client.getServerEvents().peek() != null) {
        evt = client.getServerEvents().poll();
        GameEventManager.raise(evt);
      }
      try {
        render();
        if (data.getOwner().equals(data.getCurrentPlayer()) && currentScreen.isHandlesInput()) {
          handleInput();
        } else {
          // wait for event
          GameEventManager.raise(client.getServerEvents().take());
        }
      } catch (IOException e){
        e.printStackTrace();
      } catch (InterruptedException e){
        Thread.currentThread().interrupt();
      }
    }
  }

  public void stop(){
    running = false;
  }
}
