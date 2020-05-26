package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.Direction;
import it.polimi.vovarini.view.cli.elements.BoardElement;
import it.polimi.vovarini.view.cli.elements.PhasePrompt;
import it.polimi.vovarini.view.cli.elements.PlayerList;
import it.polimi.vovarini.view.cli.elements.Text;
import it.polimi.vovarini.view.cli.input.Key;
import it.polimi.vovarini.view.cli.styling.Color;

import java.util.Collection;

public class MatchScreen extends Screen {

  private final PlayerList playerList;
  private final BoardElement boardElement;
  private final PhasePrompt phasePrompt;
  private final Text message;

  private boolean reRenderNeeded;

  private String lastContent;

  public MatchScreen(ViewData data, GameClient client){
    super(data, client);

    playerList = new PlayerList(data.getPlayerSet(), data.getOwner(), data.getPlayersColors());
    playerList.setCurrentPlayer(data.getCurrentPlayer());
    boardElement = new BoardElement(data.getBoard(), data.getPlayerSet(), data.getPlayersColors(), Color.Green);
    phasePrompt = new PhasePrompt(data.getCurrentPhase());
    message = new Text("");

    reRenderNeeded = true;
  }

  @Override
  public void handleKeyPress(Key key) {
    switch (key) {
      case A: {
        boardElement.moveCursor(Direction.Left);
        reRenderNeeded = true;
        break;
      }
      case D: {
        boardElement.moveCursor(Direction.Right);
        reRenderNeeded = true;
        break;
      }
      case W: {
        boardElement.moveCursor(Direction.Up);
        reRenderNeeded = true;
        break;
      }
      case S: {
        boardElement.moveCursor(Direction.Down);
        reRenderNeeded = true;
        break;
      }
      case Spacebar: {
        select();
        break;
      }
      case N: {
        client.raise(new SkipEvent(data.getOwner()));
        handlesInput = false;
        break;
      }
      case O: {
        confirm();
      }
      default: {
        break;
      }
    }
  }

  @Override
  public String render() {
    StringBuilder content = new StringBuilder();
    if (reRenderNeeded) {
      content.append(playerList.render())
              .append("\n")
              .append(boardElement.render())
              .append("\n")
              .append(data.getCurrentPlayer().equals(data.getOwner()) ? phasePrompt.render() :
                      "It's " + data.getCurrentPlayer().getNickname() + "'s turn.")
              .append("\n")
              .append(message.render());

      reRenderNeeded = false;
      lastContent = content.toString();
      return content.toString();
    } else {
      return lastContent;
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
      System.exit(1);
    }
    if (buildablePoints.contains(dest)){
      int nextLevel = data.getBoard().getBox(dest).getLevel() + 1;
      boardElement.resetMarkedPoints();
      client.raise(new BuildEvent(data.getOwner(), dest, nextLevel));
      handlesInput = false;
    }
  }

  private void selectWorker(){
    if (data.getSelectedWorker() == null){
      if (boardElement.getCursorLocation().equals(data.getCurrentStart())) {
        deSelect();
      } else {
        Item item = data.getBoard().getItems(boardElement.getCursorLocation()).peek();
        if (data.getOwner().isHasLost()){
          System.exit(1);
        }

        if (data.getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
          data.setCurrentStart(boardElement.getCursorLocation());
          data.setSelectedWorker((Worker) item);
          data.getOwner().setCurrentSex(((Worker) item).getSex());
          data.getCurrentPlayer().setCurrentSex(((Worker) item).getSex());
          // mark points reachable by the selected worker
          boardElement.markPoints(
                  data.getOwner().getGodCard().computeReachablePoints()
          );
          message.setContent("Press O to confirm your choice.");
          reRenderNeeded = true;
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
    if (data.getSelectedWorker() != null &&
      boardElement.getMarkedPoints().contains(boardElement.getCursorLocation())){
        boardElement.resetMarkedPoints();
        client.raise(new MovementEvent(
                data.getOwner(),
                boardElement.getCursorLocation())
        );
        handlesInput = false;
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
      case Start -> {
        if (data.getSelectedWorker() != null){
          client.raise(
                  new WorkerSelectionEvent(data.getOwner(), data.getSelectedWorker().getSex()));
          handlesInput = false;
        }
      }
    }
  }

  @Override
  public void handleBoardUpdate(BoardUpdateEvent e) {
    boardElement.setBoard(e.getNewBoard());
    reRenderNeeded = true;
  }

  @Override
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {
    playerList.setCurrentPlayer(e.getNewPlayer());
    message.setContent("");
    phasePrompt.setCurrentPhase(Phase.Start);

    if (e.getNewPlayer().equals(data.getCurrentPlayer())){
      handlesInput = true;
    }

    reRenderNeeded = true;
  }

  @Override
  public void handlePhaseUpdate(PhaseUpdateEvent e) {
    handlesInput = true;
    phasePrompt.setCurrentPhase(e.getNewPhase());
    message.setContent("");
    switch (data.getCurrentPhase()) {
      case Movement -> {
        if (data.getCurrentPlayer().equals(data.getOwner())) {
          boardElement.markPoints(data.getOwner().getGodCard().computeReachablePoints());
        }
      }
      case Construction -> {
        if (data.getCurrentPlayer().equals(data.getOwner())) {
          boardElement.markPoints(data.getOwner().getGodCard().computeBuildablePoints());
          if (data.getOwner().isHasLost()) {
            System.exit(1);
          }
        }
      }
      case End -> {
        if (data.getCurrentPlayer().equals(data.getOwner())) {
          deSelect();
          message.setContent("Press N to end your turn.");
        }
      }
    }
    reRenderNeeded = true;
  }
}
