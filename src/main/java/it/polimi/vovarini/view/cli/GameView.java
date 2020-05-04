package it.polimi.vovarini.view.cli;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.server.Server;
import it.polimi.vovarini.view.View;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.console.Console;
import it.polimi.vovarini.view.cli.console.FullScreenConsole;
import it.polimi.vovarini.view.cli.elements.BoardElement;
import it.polimi.vovarini.view.cli.elements.PlayerList;
import it.polimi.vovarini.view.cli.screens.MatchScreen;
import it.polimi.vovarini.view.cli.screens.Screen;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class GameView extends View {

  private final GameClient client;

  private boolean reRenderNeeded;

  private Screen currentScreen;

  private final Console console;

  public GameView() throws IOException {
    super();

    data = new ViewData();

    this.reRenderNeeded = true;

    console = new FullScreenConsole();

    client = new GameClient("127.0.0.1", Server.DEFAULT_PORT);
  }


  @GameEventListener
  public void handleBoardUpdate(BoardUpdateEvent e){
    data.setBoard(e.getNewBoard());
    currentScreen.handleBoardUpdate(e);
  }

  @GameEventListener
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e){
    data.setCurrentPlayer(e.getNewPlayer());
    currentScreen.handleCurrentPlayerUpdate(e);
  }

  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e){
    data.setCurrentPhase(e.getNewPhase());
    currentScreen.handlePhaseUpdate(e);
  }

  @GameEventListener
  public void handleGameStart(GameStartEvent e){
    Player[] players = e.getPlayers();
    for (int i = 0; i < players.length; i++){
      for (Player p: data.getPlayers()){
        if (players[i].equals(p)){
          players[i] = p;
        }
      }
    }
    data.getPlayers().clear();
    for (Player p: players){
      data.addPlayer(p);
    }
    startMatch();
  }

  @Override
  @GameEventListener
  public void handleNewPlayer(NewPlayerEvent e) {
    Player p = e.getNewPlayer().clone();
    if (p.equals(data.getOwner())) {
      data.setOwner(p);
    }
    data.addPlayer(p);
  }

  @Override
  @GameEventListener
  public void handleGodSelectionStart(GodSelectionStartEvent e) {

  }

  @Override
  @GameEventListener
  public void handleSelectYourCard(SelectYourCardEvent e) {

  }

  @Override
  @GameEventListener
  public void handleCardAssignment(CardAssignmentEvent e) {

  }

  @Override
  @GameEventListener
  public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {

  }

  public void render(){
    console.println(currentScreen.render());
  }

  public void handleInput() throws IOException{
    int input = console.getReader().read();
    console.println(Integer.toString(input));
    currentScreen.handleKeyPress(input);
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
    try {
      while (true) {
        GameEvent evtFromServer = client.getServerEvents().take();
        GameEventManager.raise(evtFromServer);
      }
    } catch (InterruptedException e){
      e.printStackTrace();
    }
  }

  public void gameLoop(){
    render();
    while (true) {
      GameEvent evt;
      // consume events from the server
      while ( (evt = client.getServerEvents().peek()) != null) {
        GameEventManager.raise(evt);
      }
      try {
        if (data.getOwner().equals(data.getCurrentPlayer())) {
          handleInput();
          render();
        }
      } catch (IOException e){
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args){
    try {
      GameView view = new GameView();
      view.gameSetup();
    } catch (IOException e){
      e.printStackTrace();
    }
  }
}
