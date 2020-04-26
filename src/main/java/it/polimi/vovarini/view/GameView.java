package it.polimi.vovarini.view;

import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.CurrentPlayerLosesException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.controller.Controller;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.model.moves.Movement;
import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.server.Server;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Scanner;

import static com.sun.jna.platform.win32.Wincon.ENABLE_LINE_INPUT;

public class GameView {

  private ViewData data;

  private GameClient client;

  private boolean reRenderNeeded;

  private BoardRenderer boardRenderer;

  private Terminal terminal;
  private Reader reader;

  private int printedLineCount;

  public GameView() throws IOException {
    GameEventManager.bindListeners(this);

    data = new ViewData();

    this.reRenderNeeded = true;

    boardRenderer = new BoardRenderer();

    printedLineCount = 0;

    client = new GameClient("127.0.0.1", Server.DEFAULT_PORT);
  }

  private void handlePlayerLoss(){
    printLine("Hai perso coglione!");
  }

  @GameEventListener
  public void handleBoardUpdate(BoardUpdateEvent e){
    data.setBoard(e.getNewBoard());
    reRenderNeeded = true;
  }

  @GameEventListener
  public void handlePlayerUpdate(CurrentPlayerChangedEvent e){
    data.setCurrentPlayer(e.getNewPlayer());
    // for the purpose of this demo, we also update owner so that the game can continue
    data.setOwner(e.getNewPlayer());
  }

  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e){
    data.setCurrentPhase(e.getNewPhase());
    if (data.getCurrentPhase() == Phase.Construction){
      try {
        boardRenderer.markPoints(data.getOwner().getGodCard().computeBuildablePoints());
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
    switch (phase) {
      case Start -> {
      }
      case Movement -> {
        return "MOVEMENT - Select a Worker, then select a destination.";
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
    return "";
  }

  private void printLine(String line){
    System.out.println(line);
    long newLines = line.chars().filter(ch -> ch == '\n').count();
    printedLineCount += newLines + 1;
  }

  private void renderPlayers(){
    for (Player player: data.getPlayers()){
      StringBuilder playerLine = new StringBuilder();
      playerLine.append(PlayerRenderer.getInstance().render(player));
      if (player.equals(data.getOwner())){
        playerLine.insert(0, "YOU --> ");
      }
      if (player.equals(data.getCurrentPlayer())){
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
      printLine(boardRenderer.render(data.getBoard()));
      if (data.getOwner().equals(data.getCurrentPlayer())) {
        printLine(getPhasePrompt(data.getCurrentPhase()));
      } else {
        printLine("It's " + data.getCurrentPlayer().getNickname() + "'s turn.");
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
    data.setSelectedWorker(null);
    data.setCurrentStart(null);
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
      Collection<Point> buildablePoints = data.getOwner().getGodCard().computeBuildablePoints();
      if (buildablePoints.contains(dest)){
        int nextLevel = data.getBoard().getBox(dest).getLevel() + 1;
        deSelect();
        GameEventManager.raise(new BuildEvent(data.getOwner(), dest, nextLevel));
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
    if (data.getSelectedWorker() != null){
      if (boardRenderer.getCursorLocation().equals(data.getCurrentStart())){
        deSelect();
      } else if (boardRenderer.getMarkedPoints().contains(boardRenderer.getCursorLocation())){
        boardRenderer.resetMarkedPoints();
        GameEventManager.raise(new MovementEvent(
                data.getOwner(),
                boardRenderer.getCursorLocation())
        );
      }
    } else {
      // check if one of the player's workers is under the cursor
      try {
        Item item = data.getBoard().getItems(boardRenderer.getCursorLocation()).peek();

        if (data.getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
          data.setCurrentStart(boardRenderer.getCursorLocation());
          data.setSelectedWorker((Worker) item);
          GameEventManager.raise(
                  new WorkerSelectionEvent(
                          data.getOwner(),
                          data.getSelectedWorker().getSex())
          );
          // mark points reachable by the selected worker
          boardRenderer.markPoints(
                  data.getOwner().getGodCard().computeReachablePoints()
          );

          reRenderNeeded = true;
        }
      } catch (BoxEmptyException ignored) {
      } catch (InvalidPositionException ignored) {
      } catch (CurrentPlayerLosesException ignored) {
        handlePlayerLoss();
      }

    }
  }

  /**
   * This method handles a spacebar press. The outcome depends
   * on the current Phase and the game status.
   */
  private void select(){
    switch (data.getCurrentPhase()){
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
    if (data.getOwner().equals(data.getCurrentPlayer())) {
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
          GameEventManager.raise(new SkipEvent(data.getCurrentPlayer()));
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

      if(System.getProperty("os.name").startsWith("Windows"))
      {
        // Set output mode to handle virtual terminal sequences
        Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
        DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
        HANDLE hOut = (HANDLE)GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

        DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
        Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
        GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});

        int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
        DWORD dwMode = p_dwMode.getValue();
        dwMode.setValue((dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING) &~ENABLE_LINE_INPUT);
        Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
        SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});
      }

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

      view.data.setPlayers(game.getPlayers().clone());
      view.data.setOwner(view.data.getPlayers()[0].clone());
      view.data.setCurrentPlayer(view.data.getPlayers()[0].clone());
      view.data.setBoard(game.getBoard().clone());

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
