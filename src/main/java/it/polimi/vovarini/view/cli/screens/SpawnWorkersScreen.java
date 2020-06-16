package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.BoardUpdateEvent;
import it.polimi.vovarini.common.events.SpawnWorkerEvent;
import it.polimi.vovarini.common.events.WorkerSelectionEvent;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.Direction;
import it.polimi.vovarini.view.cli.elements.BoardElement;
import it.polimi.vovarini.view.cli.elements.Text;
import it.polimi.vovarini.view.cli.input.Key;
import it.polimi.vovarini.view.cli.styling.Color;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpawnWorkersScreen extends Screen {

  private final List<Sex> sexes = new LinkedList<>(Arrays.asList(Sex.values()));

  private final BoardElement boardElement;
  private final Text message;

  public SpawnWorkersScreen(ViewData data, GameClient client){
    super(data, client);
    boardElement = new BoardElement(data.getBoard(), data.getPlayerSet(), data.getPlayersColors(), Color.Green);
    markFreePoints();
    client.raise(new WorkerSelectionEvent(data.getOwner(), sexes.get(0)));
    message = new Text("Place your " + sexes.get(0) + " worker.");
  }

  private void markFreePoints(){
    boardElement.resetMarkedPoints();
    List<Point> freePoints = IntStream.range(0, data.getBoard().getSize())
            .boxed()
            .flatMap(x -> IntStream.range(0, data.getBoard().getSize())
                    .mapToObj(y -> new Point(x, y)))
            .filter(p -> data.getBoard().getItems(p).isEmpty())
            .collect(Collectors.toList());
    boardElement.markPoints(freePoints);
    needsRender = true;
  }

  private void spawnCurrent(){
    if (!sexes.isEmpty()) {
      Point cursor = boardElement.getCursorLocation();
      if (boardElement.getMarkedPoints().contains(cursor)) {
        client.raise(new SpawnWorkerEvent(data.getOwner(), cursor));
        sexes.remove(0);

        if (sexes.isEmpty()){
          handlesInput = false;
        }
      }
    }
  }

  @Override
  public void handleBoardUpdate(BoardUpdateEvent e) {
    boardElement.setBoard(e.getNewBoard());
    if (!sexes.isEmpty()){
      client.raise(new WorkerSelectionEvent(data.getOwner(), sexes.get(0)));
      message.setContent("Place your " + sexes.get(0) + " worker.");
      markFreePoints();
    }
    needsRender = true;
  }

  @Override
  public void handleKeyPress(Key key) {
    switch (key) {
      case W -> boardElement.moveCursor(Direction.Up);
      case A -> boardElement.moveCursor(Direction.Left);
      case S -> boardElement.moveCursor(Direction.Down);
      case D -> boardElement.moveCursor(Direction.Right);
      case SPACEBAR -> spawnCurrent();
      default -> {
      }
    }
    needsRender = true;
  }

  @Override
  public String render() {
    needsRender = false;
    return boardElement.render() + "\n" + message.render();
  }
}
