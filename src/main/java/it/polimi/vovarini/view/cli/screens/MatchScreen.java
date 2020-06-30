package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.Direction;
import it.polimi.vovarini.view.cli.elements.BoardElement;
import it.polimi.vovarini.view.cli.elements.PhasePrompt;
import it.polimi.vovarini.view.cli.elements.PlayerList;
import it.polimi.vovarini.view.cli.elements.Text;
import it.polimi.vovarini.view.cli.input.Key;
import it.polimi.vovarini.view.cli.styling.Color;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static it.polimi.vovarini.model.Phase.Construction;

public class MatchScreen extends Screen {

  private final PlayerList playerList;
  private final BoardElement boardElement;
  private final PhasePrompt phasePrompt;
  private final Text message;

  public MatchScreen(ViewData data, GameClient client){
    super(data, client);

    playerList = new PlayerList(data.getPlayerSet(), data.getOwner(), data.getPlayersColors());
    playerList.setCurrentPlayer(data.getCurrentPlayer());
    boardElement = new BoardElement(data.getBoard(), data.getPlayerSet(), data.getPlayersColors(), Color.Green);
    phasePrompt = new PhasePrompt(data.getCurrentPhase());
    message = new Text("");
  }

  @Override
  public void handleKeyPress(Key key) {
    switch (key) {
      case A -> {
        boardElement.moveCursor(Direction.Left);
        needsRender = true;
      }
      case D -> {
        boardElement.moveCursor(Direction.Right);
        needsRender = true;
      }
      case W -> {
        boardElement.moveCursor(Direction.Up);
        needsRender = true;
      }
      case S -> {
        boardElement.moveCursor(Direction.Down);
        needsRender = true;
      }
      case SPACEBAR -> {
        select(false);
      }
      case N -> {
        client.raise(new SkipEvent(data.getOwner()));
        handlesInput = false;
      }
      case O -> {
        confirm();
      }
      case FOUR -> {
        if (data.getCurrentPhase().equals(Construction)) {
          select(true);
        }
      }
      default -> {
      }
    }
  }

  @Override
  public String render() {
    StringBuilder content = new StringBuilder();
    content.append(playerList.render())
            .append("\n")
            .append(boardElement.render())
            .append("\n")
            .append(data.getCurrentPlayer().equals(data.getOwner()) ? phasePrompt.render() :
                    "It's " + data.getCurrentPlayer().getNickname() + "'s turn.")
            .append("\n")
            .append(message.render());
    needsRender = false;
    return content.toString();
  }

  private void deSelect(){
    data.setSelectedWorker(null);
    data.setCurrentStart(null);
    boardElement.resetMarkedPoints();
    message.setContent("");

    needsRender = true;
  }

  /**
   * This method handles a spacebar press when
   * the current phase is Construction.
   */
  private void selectWhenConstructionPhase(boolean dome){

    Point dest = boardElement.getCursorLocation();

    Collection<Point> buildablePoints = data.getOwner().getGodCard().computeBuildablePoints();

    if (buildablePoints.contains(dest)){
      int nextLevel = data.getBoard().getBox(dest).getLevel() + 1;
      var sampleMove = new Construction(data.getBoard(), Block.blocks[(dome ? 4 : nextLevel) - 1], dest);
      if (data.getOwner().getGodCard().validate(new LinkedList<>(buildablePoints), sampleMove)) {
        client.raise(new BuildEvent(data.getOwner(), dest, dome ? 4 : nextLevel));
        boardElement.resetMarkedPoints();
        handlesInput = false;
        needsRender = true;
      }
    }
  }

  private void selectWorker(){
    if (data.getSelectedWorker() == null){
      if (boardElement.getCursorLocation().equals(data.getCurrentStart())) {
        deSelect();
      } else {
        Item item = data.getBoard().getBox(boardElement.getCursorLocation()).getItems().peek();

        if (data.getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
          data.getOwner().setCurrentSex(((Worker) item).getSex());
          data.getCurrentPlayer().setCurrentSex(((Worker) item).getSex());
          // mark points reachable by the selected worker
          List<Point> reachablePoints = data.getOwner().getGodCard().computeReachablePoints();
          if (!reachablePoints.isEmpty()) {
            data.setCurrentStart(boardElement.getCursorLocation());
            data.setSelectedWorker((Worker) item);

            boardElement.markPoints(
                    data.getOwner().getGodCard().computeReachablePoints()
            );
            message.setContent("Press O to confirm your choice.");
          }

          needsRender = true;
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
        needsRender = true;
        handlesInput = false;
    }
  }

  /**
   * This method handles a spacebar/four press. The outcome depends
   * on the current Phase and the game status.
   */
  private void select(boolean dome){
    switch (data.getCurrentPhase()){
      case Start ->
              selectWorker();
      case Movement ->
              selectWhenMovementPhase();
      case Construction ->
              selectWhenConstructionPhase(dome);
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
  public void handle(BoardUpdateEvent e) {
    boardElement.setBoard(e.getNewBoard());
    needsRender = true;
  }

  @Override
  public void handle(CurrentPlayerChangedEvent e) {
    playerList.setCurrentPlayer(e.getNewPlayer());
    message.setContent("");
    phasePrompt.setCurrentPhase(Phase.Start);

    if (e.getNewPlayer().equals(data.getCurrentPlayer())){
      handlesInput = true;
    }
    needsRender = true;

  }

  @Override
  public void handle(PhaseUpdateEvent e) {
    handlesInput = true;
    phasePrompt.setCurrentPhase(e.getNewPhase());
    message.setContent("");
    switch (data.getCurrentPhase()) {
      case Movement -> {
        if (data.getCurrentPlayer().equals(data.getOwner())) {
          boardElement.resetMarkedPoints();
          boardElement.markPoints(data.getOwner().getGodCard().computeReachablePoints());
        }
      }
      case Construction -> {
        if (data.getCurrentPlayer().equals(data.getOwner())) {
          boardElement.resetMarkedPoints();
          boardElement.markPoints(data.getOwner().getGodCard().computeBuildablePoints());
        }
      }
      case End -> {
        if (data.getCurrentPlayer().equals(data.getOwner())) {
          deSelect();
          message.setContent("Press N to end your turn.");
        }
      }
    }
    needsRender = true;
  }
}
