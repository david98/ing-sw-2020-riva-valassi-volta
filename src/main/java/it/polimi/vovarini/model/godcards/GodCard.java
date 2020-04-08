package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GodCard {

  protected Game game;
  protected GodName name;

  public GodCard(GodName name) {
    this.name = name;
  }

  BiFunction<Game, Point, Boolean> isPointReachable =
      (Game game, Point point) -> {
        try {
          Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
          Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);
          if (!point.isAdjacent(currentWorkerPosition)) {
            return false;
          }

          try {
            Stack<Item> destinationItems = game.getBoard().getItems(point);
            /* Here we assume that if the Block below a Worker is, say,
             * a level 3 block, then below it you have a level 2 block
             * and a level 1 block. Blocks must be stacked according to
             * their level. Also, with Atlas you can build level 4 blocks
             * anywhere, but no Worker can stand on top of a level 4 Block
             * so this assumption is still valid.
             */
            int destinationLevel = destinationItems.size();
            int currentWorkerLevel = game.getBoard().getItems(currentWorkerPosition).size();
            return (destinationLevel - currentWorkerLevel <= 1)
                && currentWorker.canBePlacedOn(destinationItems.peek());
          } catch (BoxEmptyException ignored) {
            return true;
          }

        } catch (ItemNotFoundException | InvalidPositionException ignored) {
          System.err.println("This really should never happen...");
        }
        return false;
      };

  BiFunction<Game, Point, Boolean> isPointBuildable =
      (Game game, Point point) -> {
        try {
          Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
          Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);
          if (!point.isAdjacent(currentWorkerPosition)) {
            return false;
          }

          try {
            Stack<Item> destinationItems = game.getBoard().getItems(point);
            return Arrays.stream(Block.blocks)
                .anyMatch(block -> block.canBePlacedOn(destinationItems.peek()));
          } catch (BoxEmptyException ignored) {
            return true;
          }
        } catch (ItemNotFoundException | InvalidPositionException ignored) {
          System.err.println("This really should never happen...");
        }
        return false;
      };

  Predicate<Movement> isMovementWinning =
      (Movement movement) -> {
        // this needs to be called BEFORE calling movement.execute()
        if (movement.isForced()) {
          return false;
        }
        try {
          Stack<Item> destinationItems = movement.getBoard().getItems(movement.getEnd());
          int endLevel = destinationItems.size();
          if (endLevel != Block.WIN_LEVEL) {
            return false;
          }
          int currentLevel = movement.getBoard().getItems(movement.getStart()).size() - 1;

          return currentLevel < Block.WIN_LEVEL;
        } catch (BoxEmptyException e) {
          return false;
        } catch (InvalidPositionException ignored) {
          System.err.println("This really should never happen...");
        }
        return false;
      };

  public List<Point> computeReachablePoints() throws CurrentPlayerLosesException {
    List<Point> reachablePoints = new LinkedList<>();

    try {
      Player player = game.getCurrentPlayer();
      Board board = game.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      reachablePoints =
          candidatePositions.stream()
              .filter(p -> isPointReachable.apply(game, p))
              .collect(Collectors.toList());
    } catch (ItemNotFoundException ignored) {
    }

    if (reachablePoints.isEmpty()) {
      throw new CurrentPlayerLosesException();
    }
    return reachablePoints;
  }

  public boolean isMovementWinning(Movement movement) {
    return isMovementWinning.test(movement);
  }

  public List<Point> computeBuildablePoints() throws CurrentPlayerLosesException {
    List<Point> buildablePoints = new LinkedList<>();

    try {
      Player player = game.getCurrentPlayer();
      Board board = game.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      buildablePoints =
          candidatePositions.stream()
              .filter(p -> isPointBuildable.apply(game, p))
              .collect(Collectors.toList());

    } catch (ItemNotFoundException ignored) {
    }

    if (buildablePoints.isEmpty()) {
      throw new CurrentPlayerLosesException();
    }
    return buildablePoints;
  }

  public void consequences(Game game) {}

  public void setGame(Game game) {
    this.game = game;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GodCard) {
      return name.equals(((GodCard) obj).name);
    } else {
      return super.equals(obj);
    }
  }
}
