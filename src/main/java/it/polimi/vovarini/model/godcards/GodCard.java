package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.*;
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

/**
 * @class The GodCard class represents a general GodCard
 * game is the current game played by all the players
 * name references one of the cards available in the base set of Santorini
 */
public class GodCard {

  protected Game game;
  protected GodName name;

  /**
   * Constructor method of GodCard class without game assignment (if the card is created before starting the game)
   * @param name Name of the Card I want to create, must be a value of the GodName enumeration
   */
  public GodCard(GodName name) {
    this.name = name;
  }

  /**
   * Constructor method of GodCard
   * @param name Name of the Card I want to create, must be a value of the GodName enumeration
   * @param game Instance of game currently played by all the players
   */
  public GodCard(GodName name, Game game) {
    this.name = name;
    this.game = game;
  }

  /**
   * Lambda function presenting the base Behavior for Reachability. Gets injected dynamically by code in the Reachability class
   * @param game Instance of game currently played by all the players
   * @param point Candidate to be a Movement destination
   * @return if the candidate point can be reached returns true, false otherwise
   */
  BiFunction<Game, Point, Boolean> isPointReachable =
      (Game game, Point point) -> {
        try {
          Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
          Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);
          if (!point.isAdjacent(currentWorkerPosition)) {
            return false;
          }

          try {
            Box destinationBox = game.getBoard().getBox(point);
            Stack<Item> destinationItems = destinationBox.getItems();
            int destinationLevel = destinationBox.getLevel();
            int currentWorkerLevel = game.getBoard().getBox(currentWorkerPosition).getLevel();
            return (destinationLevel - currentWorkerLevel <= 1)
                && currentWorker.canBePlacedOn(destinationItems.peek());
          } catch (BoxEmptyException ignored) {
            return true;
          }

        } catch (ItemNotFoundException ignored) {
          System.err.println("This really should never happen...");
        }
        return false;
      };

  /**
   * Lambda function presenting the base Behavior for Buildability. Gets injected dynamically by code in the Buildability class
   * @param game Instance of game currently played by all the players
   * @param point Candidate to be a Construction destination
   * @return if the candidate point can be built upon returns true, false otherwise
   */
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


  /**
   * Predicate for checking if a player has won with the Movement he wants to perform (applied before the movement itself)
   * @param movement The Movement move the player wants to execute.
   * @return A predicate always return true or false. It will return true if the movement leads to victory after execution, false otherwise
   * A Forced movement always return false (the system itself must not make a player win)
   */
  Predicate<Movement> isMovementWinning =
      (Movement movement) -> {
        // this needs to be called BEFORE calling movement.execute()
        if (movement.isForced()) {
          return false;
        }
        int endLevel = movement.getBoard().getBox(movement.getEnd()).getLevel();
        if (endLevel != Block.WIN_LEVEL) {
          return false;
        }
        int currentLevel = movement.getBoard().getBox(movement.getStart()).getLevel();

        return currentLevel < Block.WIN_LEVEL;
      };

  /**
   *
   * @return a list of points that the player can reach from his currentWorker position
   * @throws CurrentPlayerLosesException if the list of points is empty it means that the current player cannot move, thus losing the game
   */
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

  /**
   *
   * @return a list of points that the player can build upon from his currentWorker position
   * @throws CurrentPlayerLosesException if the list of points is empty it means that the current player cannot build, thus losing the game
   */
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

  public GodName getName(){
    return name;
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
