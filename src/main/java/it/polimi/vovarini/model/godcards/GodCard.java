package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Movement;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.LinkedList;
import java.util.List;

public abstract class GodCard {

  protected Game game;
  protected GodName name;

  public GodCard(Game game) {
    this.game = game;
    this.name = GodName.Nobody;
  }

  public List<Point> computeReachablePoints() {
    LinkedList<Point> reachablePoints = new LinkedList<>();

    try {
      Player player = game.getCurrentPlayer();
      Board board = game.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      for (Point candidatePosition : candidatePositions) {
        try {
          if (selectedWorker.canBePlacedOn(board.getItems(candidatePosition).peek())) {
            reachablePoints.add(candidatePosition);
          }
        } catch (BoxEmptyException e) {
          reachablePoints.add(candidatePosition);
        } catch (InvalidPositionException ignored) {

        }
      }

    } catch (ItemNotFoundException ignored) {
    }
    return reachablePoints;
  }

  public boolean checkWin(Movement lastMovement) {
    return false;
  }

  public boolean checkLoss(Point workerPosition) {
    return false;
  }

  public List<Point> computeBuildablePoints() {
    LinkedList<Point> buildablePoints = new LinkedList<>();
    LinkedList<Block> blocks = new LinkedList<>();
    try {
      for (int i = Block.MIN_LEVEL; i <= Block.MAX_LEVEL; i++) {
        blocks.add(new Block(i));
      }
    } catch (InvalidLevelException ignored) {

    }

    try {
      Player player = game.getCurrentPlayer();
      Board board = game.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      for (Point candidatePosition : candidatePositions) {
        try {
          Item topmostItem = board.getItems(candidatePosition).peek();
          if (blocks.stream().anyMatch(block -> block.canBePlacedOn(topmostItem))) {
            buildablePoints.add(candidatePosition);
          }
        } catch (BoxEmptyException e) {
          buildablePoints.add(candidatePosition);
        } catch (InvalidPositionException ignored) {

        }
      }

    } catch (ItemNotFoundException ignored) {
    }
    return buildablePoints;
  }

  public void consequences(Game game) {}

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GodCard){
      return name.equals(((GodCard) obj).name);
    } else {
      return super.equals(obj);
    }
  }
}
