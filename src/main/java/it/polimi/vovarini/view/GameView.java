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
import it.polimi.vovarini.model.godcards.GodCard;
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

public class GameView extends View{
  
  private final GameClient client;

  private boolean reRenderNeeded;

  private final BoardRenderer boardRenderer;

  private Terminal terminal;
  private Reader reader;

  private int printedLineCount;

  public GameView() throws IOException {
    super();

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
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e){
    data.setCurrentPlayer(e.getNewPlayer());
  }

  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e){
    data.setCurrentPhase(e.getNewPhase());
    if (data.getCurrentPhase() == Phase.Construction){
        boardRenderer.markPoints(data.getOwner().getGodCard().computeBuildablePoints());
        if (data.getOwner().isHasLost()){
          handlePlayerLoss();
        }
    }
    reRenderNeeded = true;
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

      Point dest = boardRenderer.getCursorLocation();

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
      if (boardRenderer.getCursorLocation().equals(data.getCurrentStart())) {
        deSelect();
      } else {
        try {
          Item item = data.getBoard().getItems(boardRenderer.getCursorLocation()).peek();
          if (data.getOwner().isHasLost()){
            handlePlayerLoss();
          }

          if (data.getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
            data.setCurrentStart(boardRenderer.getCursorLocation());
            data.setSelectedWorker((Worker) item);
            // mark points reachable by the selected worker
            boardRenderer.markPoints(
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
      if (boardRenderer.getCursorLocation().equals(data.getCurrentStart())){
        deSelect();
      } else if (boardRenderer.getMarkedPoints().contains(boardRenderer.getCursorLocation())){
        boardRenderer.resetMarkedPoints();
        client.raise(new MovementEvent(
                data.getOwner().getNickname(),
                boardRenderer.getCursorLocation())
        );
      }
    } else {
      // check if one of the player's workers is under the cursor
      try {
        Item item = data.getBoard().getItems(boardRenderer.getCursorLocation()).peek();
        if (data.getOwner().isHasLost()){
          handlePlayerLoss();
        }

        if (data.getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
          data.setCurrentStart(boardRenderer.getCursorLocation());
          data.setSelectedWorker((Worker) item);
          client.raise(
                  new WorkerSelectionEvent(
                          data.getOwner().getNickname(),
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
    try {

      PlayerRenderer.getInstance().setPlayers(data.getPlayers().toArray(new Player[0]));

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

      gameLoop();

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
    client.raise(new RegistrationEvent(client.getIPv4Address(), nickname));
    data.setOwner(new Player(nickname));
    System.out.println("Now waiting for other players...");
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
