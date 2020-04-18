package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.controller.Controller;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Scanner;

public class GameView {

  private Player owner;
  private Player currentPlayer;
  private Player[] players;
  private Phase currentPhase;

  private Board board;

  private Worker selectedWorker;
  private Point currentStart;
  private Point currentEnd;

  private boolean reRenderNeeded;

  private BoardRenderer boardRenderer;

  private Terminal terminal;
  private Reader reader;

  private int printedLineCount;

  public GameView(){
    GameEventManager.bindListeners(this);

    this.currentPhase = Phase.Start;
    this.reRenderNeeded = true;

    boardRenderer = new BoardRenderer();

    printedLineCount = 0;
  }

  private void handlePlayerLoss(){
    printLine("Hai perso coglione!");
  }

  @GameEventListener
  public void handleBoardUpdate(BoardUpdateEvent e){
    board = e.getNewBoard();
    reRenderNeeded = true;
  }

  @GameEventListener
  public void handlePlayerUpdate(CurrentPlayerChangedEvent e){
    currentPlayer = e.getNewPlayer();
    // for the purpose of this demo, we also update owner so that the game can continue
    owner = e.getNewPlayer();
  }

  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e){
    currentPhase = e.getNewPhase();
    if (currentPhase == Phase.Construction){
      try {
        boardRenderer.markPoints(owner.getGodCard().computeBuildablePoints());
      } catch (CurrentPlayerLosesException ex){
        handlePlayerLoss();
      }
    }
    reRenderNeeded = true;
  }

  @GameEventListener
  public void handleGameStart(GameStartEvent e){
    startMatch();
  }

  private String getPhasePrompt(Phase phase){
    switch (phase){
      case Start:{
        break;
      }
      case Movement: {
        return "MOVEMENT - Select a Worker, then select a destination.";
      }
      case Construction: {
        return "CONSTRUCTION - Where do you want to build?";
      }
      case End: {
        return "END";
      }
      default: {
        return "";
      }
    }
    return "";
  }

  private void printLine(String line){
    System.out.println(line);
    long newLines = line.chars().filter(ch -> ch == '\n').count();
    printedLineCount += newLines + 1;
  }

  private void renderPlayers(){
    for (Player player: players){
      StringBuilder playerLine = new StringBuilder();
      playerLine.append(PlayerRenderer.getInstance().render(player));
      if (player.equals(owner)){
        playerLine.insert(0, "YOU --> ");
      }
      if (player.equals(currentPlayer)){
        playerLine.insert(0, "*");
        playerLine.append("*");
      }
      printLine(playerLine.toString());
    }
  }

  public void render(){
    if (reRenderNeeded) {
      clearScreen();
      renderPlayers();
      printLine(boardRenderer.render(board));
      if (owner.equals(currentPlayer)) {
        printLine(getPhasePrompt(currentPhase));
      } else {
        printLine("It's " + currentPlayer.getNickname() + "'s turn.");
      }

      reRenderNeeded = false;
    }
  }

  public void clearScreen() {
    for (int i = 0; i < printedLineCount; i++) {
      System.out.print("\033[F\r");
    }
    System.out.print("\033[H\033[2J");
    System.out.flush();
    printedLineCount = 0;
  }

  private void deSelect(){
    selectedWorker = null;
    currentStart = null;
    currentEnd = null;
    boardRenderer.resetMarkedPoints();

    reRenderNeeded = true;
  }

  /**
   * This method handles a spacebar press when
   * the current phase is Construction.
   */
  private void selectWhenConstructionPhase(){
    try {
      Point dest = boardRenderer.getCursorLocation();
      Collection<Point> buildablePoints = owner.getGodCard().computeReachablePoints();
      if (buildablePoints.contains(dest)){
        int nextLevel = board.getBox(dest).getLevel() + 1;
        deSelect();
        GameEventManager.raise(new BuildEvent(owner, dest, nextLevel));
      }
    } catch (CurrentPlayerLosesException e){
      handlePlayerLoss();
    }
  }

  /**
   * This method handles a spacebar press when
   * the current phase is Movement.
   */
  private void selectWhenMovementPhase(){
    if (selectedWorker != null){
      if (boardRenderer.getCursorLocation().equals(currentStart)){
        deSelect();
      } else if (boardRenderer.getMarkedPoints().contains(boardRenderer.getCursorLocation())){
        boardRenderer.resetMarkedPoints();
        GameEventManager.raise(new MovementEvent(
                owner,
                boardRenderer.getCursorLocation())
        );
      }
    } else {
      // check if one of the player's workers is under the cursor
      try{
        Item item = board.getItems(boardRenderer.getCursorLocation()).pop();
        if (owner.getWorkers().values().stream().anyMatch(w -> w.equals(item))){
          currentStart = boardRenderer.getCursorLocation();
          selectedWorker = (Worker)item;
          GameEventManager.raise(
                  new WorkerSelectionEvent(
                          owner,
                          selectedWorker.getSex())
          );
          // mark points reachable by the selected worker
          boardRenderer.markPoints(
                  owner.getGodCard().computeReachablePoints()
          );
          reRenderNeeded = true;
        }
      } catch (BoxEmptyException ignored){
      } catch (InvalidPositionException ignored){
      } catch (CurrentPlayerLosesException ignored){
        handlePlayerLoss();
      }

    }
  }

  /**
   * This method handles a spacebar press. The outcome depends
   * on the current Phase and the game status.
   */
  private void select(){
    switch (currentPhase){
      case Start:
      case Movement:
        selectWhenMovementPhase();
      case Construction:
        selectWhenConstructionPhase();
      case End:
      default:
    }
  }

  public void handleInput() throws IOException{
    if (owner.equals(currentPlayer)) {
      int input = reader.read();

      switch (input) {
        case 97: { //A
          boardRenderer.moveCursor(Direction.Left);
          reRenderNeeded = true;
          break;
        }
        case 100: { //D
          boardRenderer.moveCursor(Direction.Right);
          reRenderNeeded = true;
          break;
        }
        case 119: { //W
          boardRenderer.moveCursor(Direction.Up);
          reRenderNeeded = true;
          break;
        }
        case 115: { //S
          boardRenderer.moveCursor(Direction.Down);
          reRenderNeeded = true;
          break;
        }
        case 32: { //space
          select();
          break;
        }
        case 110: { //N
          GameEventManager.raise(new SkipEvent(currentPlayer));
          break;
        }
        default: {
          break;
        }
      }
    }
  }

  public void startMatch() {
    try {
      terminal = TerminalBuilder.builder()
              .jna(true)
              .system(true)
              .build();
      terminal.enterRawMode();

      reader = terminal.reader();

    } catch (IOException e) {
      System.err.println("Could not allocate terminal.\n");
      e.printStackTrace();
    }
  }

  public void gameSetup(){
    // ask for nickname
    Scanner sc = new Scanner(System.in);
    clearScreen();
    String nickname = null;
    System.out.print("Type your nickname: ");
    nickname = sc.next();
    while ((nickname == null) || !nickname.matches("[A-Za-z0-9_]{4,16}$")){
      System.out.print("Invalid nickname, type a new one: ");
      nickname = sc.next();
    }
    GameEventManager.raise(new RegistrationEvent(this, nickname));
    System.out.println("Now waiting for other players...");
  }

  public static void main(String[] args){
    try {
      Game game = new Game(2);
      GameView view = new GameView();
      Controller controller = new Controller(game);

      view.gameSetup();

      GameEventManager.raise(new RegistrationEvent(view,"Marcantonio"));

      PlayerRenderer.getInstance().setPlayers(game.getPlayers());

      // some initialization for testing purposes
      for (Player player: game.getPlayers()){
        player.setGodCard(GodCardFactory.create(GodName.Nobody));
        player.getGodCard().setGame(game);
      }

      view.players = game.getPlayers();
      view.owner = view.players[0].clone();
      view.currentPlayer = view.players[0].clone();
      view.board = game.getBoard().clone();

      GameEventManager.raise(new WorkerSelectionEvent(game.getCurrentPlayer(), Sex.Female));
      GameEventManager.raise(new SpawnWorkerEvent(game.getCurrentPlayer(), new Point(0, 0)));
      GameEventManager.raise(new WorkerSelectionEvent(game.getCurrentPlayer(), Sex.Male));
      GameEventManager.raise(new SpawnWorkerEvent(game.getCurrentPlayer(), new Point (2, 0)));

      game.nextPlayer();

      GameEventManager.raise(new WorkerSelectionEvent(game.getPlayers()[1], Sex.Female));
      GameEventManager.raise(new SpawnWorkerEvent(game.getPlayers()[1], new Point(4, 0)));
      GameEventManager.raise(new WorkerSelectionEvent(game.getPlayers()[1], Sex.Male));
      GameEventManager.raise(new SpawnWorkerEvent(game.getPlayers()[1], new Point (1, 1)));

      game.nextPlayer();

      view.render();
      while (true) {
        view.handleInput();
        view.render();
      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}
