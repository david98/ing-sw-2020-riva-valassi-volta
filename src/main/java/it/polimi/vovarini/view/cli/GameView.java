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
import it.polimi.vovarini.view.cli.elements.CliElement;
import it.polimi.vovarini.view.cli.elements.PlayerList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class GameView extends View {

  private final GameClient client;

  private boolean reRenderNeeded;

  private final BoardElement boardElement;
  private final PlayerList playerList;

  private final Console console;

  public GameView() throws IOException {
    super();

    data = new ViewData();

    this.reRenderNeeded = true;

    boardElement = new BoardElement(data, Color.Green);
    playerList = new PlayerList(data);

    console = new FullScreenConsole();

    client = new GameClient("127.0.0.1", Server.DEFAULT_PORT);
  }

  private void handlePlayerLoss(){
    console.println("Hai perso coglione!");
  }

  @GameEventListener
  public void handleBoardUpdate(BoardUpdateEvent e){
    data.setBoard(e.getNewBoard());
    reRenderNeeded = true;
  }

  @GameEventListener
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e){
    data.setCurrentPlayer(e.getNewPlayer());
  }

  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e){
    data.setCurrentPhase(e.getNewPhase());
    if (data.getCurrentPhase() == Phase.Construction){
        boardElement.markPoints(data.getOwner().getGodCard().computeBuildablePoints());
        if (data.getOwner().isHasLost()){
          handlePlayerLoss();
        }
    }
  }

  @GameEventListener
  public void handleGameStart(GameStartEvent e){
    for (Player p: e.getPlayers()){
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

  private String getPhasePrompt(Phase phase){
    switch (phase) {
      case Start -> {
        return "START - Select a Worker";
      }
      case Movement -> {
        return "MOVEMENT - Select a destination.";
      }
      case Construction -> {
        return "CONSTRUCTION - Where do you want to build?";
      }
      case End -> {
        return "END";
      }
      default -> {
        return "";
      }
    }
  }

  private void renderPlayers(){
    console.println(playerList.render());
  }

  public void render(){
    if (reRenderNeeded) {
      console.clear();
      renderPlayers();
      console.println(boardElement.render());
      if (data.getOwner().equals(data.getCurrentPlayer())) {
        console.println(getPhasePrompt(data.getCurrentPhase()));
      } else {
        console.println("It's " + data.getCurrentPlayer().getNickname() + "'s turn.");
      }

      reRenderNeeded = false;
    }
  }

  private void deSelect(){
    data.setSelectedWorker(null);
    data.setCurrentStart(null);
    boardElement.resetMarkedPoints();

    reRenderNeeded = true;
  }

  /**
   * This method handles a spacebar press when
   * the current phase is Construction.
   */
  private void selectWhenConstructionPhase(){

      Point dest = boardElement.getCursorLocation();

      Collection<Point> buildablePoints = data.getOwner().getGodCard().computeBuildablePoints();

      if (data.getOwner().isHasLost()){
        handlePlayerLoss();
      }
      if (buildablePoints.contains(dest)){
        int nextLevel = data.getBoard().getBox(dest).getLevel() + 1;
        deSelect();
        client.raise(new BuildEvent(data.getOwner().getNickname(), dest, nextLevel));
      }



  }

  private void selectWorker(){
    if (data.getSelectedWorker() == null){
      if (boardElement.getCursorLocation().equals(data.getCurrentStart())) {
        deSelect();
      } else {
        try {
          Item item = data.getBoard().getItems(boardElement.getCursorLocation()).peek();
          if (data.getOwner().isHasLost()){
            handlePlayerLoss();
          }

          if (data.getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
            data.setCurrentStart(boardElement.getCursorLocation());
            data.setSelectedWorker((Worker) item);
            // mark points reachable by the selected worker
            boardElement.markPoints(
                    data.getOwner().getGodCard().computeReachablePoints()
            );

            reRenderNeeded = true;
          }
        } catch (BoxEmptyException ignored) {
        } catch (InvalidPositionException ignored) {
        }

      }
    } else {
      deSelect();
    }
  }

  /**
   * This method handles a spacebar press when
   * the current phase is Movement.
   */
  private void selectWhenMovementPhase(){
    if (data.getSelectedWorker() != null){
      if (boardElement.getCursorLocation().equals(data.getCurrentStart())){
        deSelect();
      } else if (boardElement.getMarkedPoints().contains(boardElement.getCursorLocation())){
        boardElement.resetMarkedPoints();
        client.raise(new MovementEvent(
                data.getOwner().getNickname(),
                boardElement.getCursorLocation())
        );
      }
    } else {
      // check if one of the player's workers is under the cursor
      try {
        Item item = data.getBoard().getItems(boardElement.getCursorLocation()).peek();
        if (data.getOwner().isHasLost()){
          handlePlayerLoss();
        }

        if (data.getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
          data.setCurrentStart(boardElement.getCursorLocation());
          data.setSelectedWorker((Worker) item);
          client.raise(
                  new WorkerSelectionEvent(
                          data.getOwner().getNickname(),
                          data.getSelectedWorker().getSex())
          );
          // mark points reachable by the selected worker
          boardElement.markPoints(
                  data.getOwner().getGodCard().computeReachablePoints()
          );

          reRenderNeeded = true;
        }
      } catch (BoxEmptyException ignored) {
      } catch (InvalidPositionException ignored) {
      }

    }
  }

  /**
   * This method handles a spacebar press. The outcome depends
   * on the current Phase and the game status.
   */
  private void select(){
    switch (data.getCurrentPhase()){
      case Start ->
        selectWorker();
      case Movement ->
        selectWhenMovementPhase();
      case Construction ->
        selectWhenConstructionPhase();
    }
  }

  /**
   * This method handles a
   */
  private void confirm(){
    switch (data.getCurrentPhase()){
      case Start:
      case Movement:
      case Construction:
      case End:
      default:
    }
  }

  public void handleInput() throws IOException{
    if (data.getOwner().equals(data.getCurrentPlayer())) {
      int input = console.getReader().read();

      switch (input) {
        case 97: { //A
          boardElement.moveCursor(Direction.Left);
          reRenderNeeded = true;
          break;
        }
        case 100: { //D
          boardElement.moveCursor(Direction.Right);
          reRenderNeeded = true;
          break;
        }
        case 119: { //W
          boardElement.moveCursor(Direction.Up);
          reRenderNeeded = true;
          break;
        }
        case 115: { //S
          boardElement.moveCursor(Direction.Down);
          reRenderNeeded = true;
          break;
        }
        case 32: { //space
          select();
          break;
        }
        case 110: { //N
          client.raise(new SkipEvent(data.getOwner()));
          break;
        }
        case 79: { //O
          confirm();
        }
        default: {
          break;
        }
      }
    }
  }

  public void startMatch() {
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
        handleInput();
      } catch (IOException e){
        e.printStackTrace();
      }
      render();
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
