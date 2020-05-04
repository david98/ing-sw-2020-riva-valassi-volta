package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.Color;
import it.polimi.vovarini.view.cli.Direction;
import it.polimi.vovarini.view.cli.elements.BoardElement;
import it.polimi.vovarini.view.cli.elements.PhasePrompt;
import it.polimi.vovarini.view.cli.elements.PlayerList;

import java.util.Collection;

public class MatchScreen extends Screen {

  private final GameClient client;

  private final PlayerList playerList;
  private final BoardElement boardElement;
  private final PhasePrompt phasePrompt;

  private boolean reRenderNeeded;

  private String lastContent;

  public MatchScreen(ViewData data, GameClient client){
    super(data);

    this.client = client;

    playerList = new PlayerList(data.getPlayers(), data.getOwner(), data.getPlayersColors());
    playerList.setCurrentPlayer(data.getCurrentPlayer());
    boardElement = new BoardElement(data.getBoard(), data.getPlayers(), data.getPlayersColors(), Color.Green);
    phasePrompt = new PhasePrompt(data.getCurrentPhase());

    reRenderNeeded = true;
  }

  @Override
  public void handleKeyPress(int keycode) {
    switch (keycode) {
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
              .append("\n");
      /*
      if (data.getOwner().equals(data.getCurrentPlayer())) {
        console.println(getPhasePrompt(data.getCurrentPhase()));
      } else {
        console.println("It's " + data.getCurrentPlayer().getNickname() + "'s turn.");
      }*/

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
            System.exit(1);
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
          System.exit(1);
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

  @Override
  public void handleBoardUpdate(BoardUpdateEvent e) {
    boardElement.setBoard(e.getNewBoard());
  }

  @Override
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {
    playerList.setCurrentPlayer(e.getNewPlayer());
  }

  @Override
  public void handlePhaseUpdate(PhaseUpdateEvent e) {
    if (data.getCurrentPhase() == Phase.Construction){
      boardElement.markPoints(data.getOwner().getGodCard().computeBuildablePoints());
      phasePrompt.setCurrentPhase(e.getNewPhase());
      if (data.getOwner().isHasLost()){
        System.exit(1);
      }
    }
  }

  @Override
  public void handleGameStart(GameStartEvent e) {

  }

  @Override
  public void handleNewPlayer(NewPlayerEvent e) {

  }

  @Override
  public void handleGodSelectionStart(GodSelectionStartEvent e) {

  }

  @Override
  public void handleSelectYourCard(SelectYourCardEvent e) {

  }

  @Override
  public void handleCardAssignment(CardAssignmentEvent e) {

  }

  @Override
  public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {

  }
}
