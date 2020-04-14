package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.controller.Controller;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public class GameView {

  private Game game;

  private Player player;
  private Player[] otherPlayers;
  private Phase currentPhase;

  private Worker selectedWorker;
  private Point currentStart;
  private Point currentEnd;

  private BoardRenderer boardRenderer;

  private Terminal terminal;
  private Reader reader;

  private int printedLineCount;

  public GameView(Game game){
    GameEventManager.bindListeners(this);

    this.game = game;
    //this.player = player;
    this.currentPhase = Phase.Start;

    boardRenderer = new BoardRenderer();

    printedLineCount = 0;
    try {
      terminal = TerminalBuilder.builder()
              .jna(true)
              .system(true)
              .build();
      terminal.enterRawMode();

      reader = terminal.reader();
    } catch (IOException e){
      System.err.println("Could not allocate terminal.\n");
      e.printStackTrace();
    }

  }

  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e){
    currentPhase = e.getNewPhase();
  }

  private String getPhasePrompt(Phase phase){
    switch (phase){
      case Start:{
        break;
      }
      case Movement: {
        return "Select a Worker, then select a destination.";
      }
      case Construction: {
        return "Where do you want to build?";
      }
      case End: {
        break;
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

  public void render(){
    for (Player player: game.getPlayers()){
      if (player.equals(game.getCurrentPlayer())){
        printLine("*" + PlayerRenderer.getInstance().render(player) + "*");
      } else {
        printLine(PlayerRenderer.getInstance().render(player));
      }
    }
    printLine(boardRenderer.render(game.getBoard()));
    printLine(getPhasePrompt(game.getCurrentPhase()));
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
  }

  private void select(){
    if (selectedWorker != null){
      if (boardRenderer.getCursorLocation().equals(currentStart)){
        deSelect();
      } else {
        GameEventManager.raise(new MovementEvent(this,
                game.getCurrentPlayer(),
                boardRenderer.getCursorLocation())
        );
        deSelect();
      }
    } else {
      // check if one of the player's workers is under the cursor
      try{
        Item item = game.getBoard().getItems(boardRenderer.getCursorLocation()).pop();
        if (game.getCurrentPlayer().getWorkers().values().stream().anyMatch(w -> w.equals(item))){
          currentStart = boardRenderer.getCursorLocation();
          selectedWorker = (Worker)item;
          GameEventManager.raise(
                  new WorkerSelectionEvent(this,
                          game.getCurrentPlayer(),
                          selectedWorker.getSex())
          );
          // mark points reachable by the selected worker
          boardRenderer.markPoints(
                  game.getCurrentPlayer().getGodCard().computeReachablePoints()
          );
        }
      } catch (BoxEmptyException ignored){
      } catch (InvalidPositionException ignored){
      } catch (CurrentPlayerLosesException ignored){
      }

    }
  }

  public void handleInput() throws IOException{
    int input = reader.read();

    switch (input){
      case 97: { //A
        boardRenderer.moveCursor(Direction.Left);
        break;
      }
      case 100: { //D
        boardRenderer.moveCursor(Direction.Right);
        break;
      }
      case 119: { //W
        boardRenderer.moveCursor(Direction.Up);
        break;
      }
      case 115: { //S
        boardRenderer.moveCursor(Direction.Down);
        break;
      }
      case 32: { //space
        select();
        break;
      }
      default: {
        break;
      }
    }
  }

  public static void main(String[] args){
    try {
      Game game = new Game(2);
      GameView view = new GameView(game);
      Controller controller = new Controller(game);

      PlayerRenderer.getInstance().setPlayers(game.getPlayers());

      // some initialization for testing purposes
      for (Player player: game.getPlayers()){
        player.getGodCard().setGame(game);
      }

      GameEventManager.raise(new WorkerSelectionEvent(view, game.getCurrentPlayer(), Sex.Female));
      GameEventManager.raise(new SpawnWorkerEvent(view, game.getCurrentPlayer(), new Point(0, 0)));
      GameEventManager.raise(new WorkerSelectionEvent(view, game.getCurrentPlayer(), Sex.Male));
      GameEventManager.raise(new SpawnWorkerEvent(view, game.getCurrentPlayer(), new Point (2, 0)));

      GameEventManager.raise(new SkipEvent(view, game.getCurrentPlayer()));

      view.clearScreen();
      view.render();
      while (true) {
        view.handleInput();
        view.clearScreen();
        view.render();
      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}
