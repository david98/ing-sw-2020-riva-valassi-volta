package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.events.GodCardUpdateEvent;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.GameDataAccessor;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The GodCard class represents a general GodCard
 * game is the current game played by all the players
 * name references one of the cards available in the base set of Santorini
 *
 * @author Mattia Valassi
 * @author Davide Volta
 * @version 0.2
 * @since 0.1
 */
public class GodCard implements Serializable {
  protected transient GameDataAccessor gameData;
  protected GodName name;

  /**
   * Constructor method of GodCard class without game assignment (if the card is created before starting the game)
   * @param name Name of the Card I want to create, must be a value of the GodName enumeration
   */
  GodCard(GodName name) {
    this.name = name;
    initCollections();
  }

  /**
   * Initialization of tall the Collections containing the different Lambda functions to evaluate
   */
  private void initCollections(){
    movementConditions = new HashSet<>();
    movementConstraints = new HashSet<>();

    constructionConditions = new HashSet<>();
    constructionConstraints = new HashSet<>();

    winningConditions = new HashSet<>();
    winningConstraints = new HashSet<>();

    movementConditions.add(isPointReachable);
    constructionConditions.add(isPointBuildable);
    winningConditions.add(isMovementWinning);
  }

  /**
   * Lambda function presenting the base Behavior for Reachability. Gets injected dynamically by code in the Reachability class
   * @param gameData Instance of gameData currently played by all the players
   * @param point Candidate to be a Movement destination
   * @return if the candidate point can be reached returns true, false otherwise
   */
  SerializableBiFunction<GameDataAccessor, Point, Boolean> isPointReachable =
      (GameDataAccessor gameData, Point point) -> {
        try {
          Worker currentWorker = gameData.getCurrentPlayer().getCurrentWorker();
          Point currentWorkerPosition = gameData.getBoard().getItemPosition(currentWorker);
          if (!point.isAdjacent(currentWorkerPosition)) {
            return false;
          }

          Box destinationBox = gameData.getBoard().getBox(point);
          var destinationItems = destinationBox.getItems();
          int destinationLevel = destinationBox.getLevel();
          int currentWorkerLevel = gameData.getBoard().getBox(currentWorkerPosition).getLevel();
          return (destinationLevel - currentWorkerLevel <= 1)
              && currentWorker.canBePlacedOn(destinationItems.peek());

        } catch (ItemNotFoundException ignored) {
          System.err.println("This really should never happen...");
        }
        return false;
      };

  /**
   * Lambda function presenting the base Behavior for Buildability. Gets injected dynamically by code in the Buildability class
   * @param gameData Instance of gameData currently played by all the players
   * @param point Candidate to be a Construction destination
   * @return if the candidate point can be built upon returns true, false otherwise
   */
  SerializableBiFunction<GameDataAccessor, Point, Boolean> isPointBuildable =
      (GameDataAccessor gameData, Point point) -> {
        try {
          Worker currentWorker = gameData.getCurrentPlayer().getCurrentWorker();
          Point currentWorkerPosition = gameData.getBoard().getItemPosition(currentWorker);
          if (!point.isAdjacent(currentWorkerPosition)) {
            return false;
          }

          var destinationItems = gameData.getBoard().getItems(point);
          return Arrays.stream(Block.blocks)
              .anyMatch(block -> block.canBePlacedOn(destinationItems.peek()));

        } catch (ItemNotFoundException | InvalidPositionException e) {
          System.err.println("This really should never happen...");
        }
        return false;
      };

  /**
   * Lambda function that returns the next phase of the turn following the standard flow
   * @param gameData Instance of gameData currently played by all the players
   * @return the next phase to play, according to the normal flow of the gameData
   */
  SerializableFunction<GameDataAccessor, Phase> nextPhase =
          (GameDataAccessor gameData) -> gameData.getCurrentPhase().next();


  /**
   * Lambda function presenting the base Behavior for consequences, regarding the Movements
   * @param gameData Instance of gameData currently played by all the players
   * @param movement is the movement move the player wants to perform, which is already been validated
   * @return list of moves to execute
   */
  SerializableBiFunction<GameDataAccessor, Movement, List<Movement>> listMovementEffects =
          (GameDataAccessor gameData, Movement movement) -> {
            List<Movement> movementList = new LinkedList<>();
            movementList.add(movement);
            return movementList;
          };

  /**
   * Lambda function presenting the base Behavior for consequences, regarding the Constructions
   * @param gameData Instance of gameData currently played by all the players
   * @param construction is the construction move the player wants to perform, which is already been validated
   * @return list of moves to execute
   */
  SerializableBiFunction<GameDataAccessor, Construction, List<Construction>> listConstructionEffects =
          (GameDataAccessor gameData, Construction construction) -> {
            List<Construction> constructionList = new LinkedList<>();
            constructionList.add(construction);
            return constructionList;
          };

  /**
   * Lambda function with base validation of movements
   * @param list is the list of points computed by the pre-move method {@link #computeReachablePoints()} ()}
   * @param movement is the movement move the player wants to perform
   * @return if the move that the player wants to perform is valid returns true, false otherwise
   */
  SerializableBiFunction<List<Point>, Movement, Boolean> validateMovement =
          (List<Point> list, Movement movement) -> list.contains(movement.getEnd());

  /**
   * Lambda function with base validation of constructions
   * @param list is the list of points computed by the pre-move method {@link #computeBuildablePoints()}
   * @param construction is the construction move the player wants to perform
   * @return if the move that the player wants to perform is valid returns true, false otherwise
   */
  SerializableBiFunction<List<Point>, Construction, Boolean> validateConstruction =
          (List<Point> list, Construction construction) -> {
              Block b = construction.getBlock();
              Point t = construction.getTarget();
              var s = gameData.getBoard().getBox(t).getItems();
              return list.contains(construction.getTarget()) &&
                          b.canBePlacedOn(s.peek());
          };

  /**
   * Predicate for checking if a player has won with the Movement he wants to perform (applied before the movement itself)
   * @param movement The Movement move the player wants to execute.
   * @return A predicate always return true or false. It will return true if the movement leads to victory after execution, false otherwise
   * A Forced movement always return false (the system itself must not make a player win)
   */
  SerializablePredicate<Movement> isMovementWinning =
      (Movement movement) -> {
        int endLevel = movement.getBoard().getBox(movement.getEnd()).getLevel();
        if (endLevel != Block.WIN_LEVEL) {
          return false;
        }
        int currentLevel = movement.getBoard().getBox(movement.getStart()).getLevel();

        return currentLevel < Block.WIN_LEVEL;
      };

  /**
   * Lambda function with base constraint of movements
   * @param gameData Instance of gameData currently played by all the players
   * @param point is the destination of movement selected by the current player
   * @return if the move that the player wants to perform is valid returns true, false otherwise
   */
  SerializableBiFunction<GameDataAccessor, Point, Boolean> constraintMovement =
          (GameDataAccessor gameData, Point p) -> true;

  Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> movementConditions;
  Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> movementConstraints;

  Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> constructionConditions;
  Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> constructionConstraints;

  Set<SerializablePredicate<Movement>> winningConditions;
  Set<SerializablePredicate<Movement>> winningConstraints;

  /**
   * Function that computes a list of all the points where moving is possible
   * @return a list of points that the player can reach from his currentWorker position
   */
  public List<Point> computeReachablePoints()   {
    List<Point> reachablePoints = new LinkedList<>();

    try {
      Player player = gameData.getCurrentPlayer();
      Board board = gameData.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      reachablePoints =
          candidatePositions.stream()
              .filter(p -> movementConditions.stream().anyMatch(cond -> cond.apply(gameData, p)))
                  .filter(p -> movementConstraints.stream().allMatch(cond -> cond.apply(gameData, p)))
              .collect(Collectors.toList());
    } catch (ItemNotFoundException ignored) {
    }

    if (reachablePoints.isEmpty()) {
      gameData.getCurrentPlayer().setHasLost(true);
    }
    return reachablePoints;
  }

  /**
   * Function that computes if a player has won thanks to a valid movement he wants to perform
   * @param movement is the movement move that the player wants to perform
   * @return true if the movement allows the player to win, false otherwise
   */
  public boolean isMovementWinning(Movement movement) {
    return !movement.isForced() && winningConditions.stream().anyMatch(cond -> cond.test(movement)) &&
            winningConstraints.stream().allMatch(cond -> cond.test(movement));
  }

  /**
   * Function that computes a list of all the points where building a block, independently by the level, is possible
   * @return a list of points that the player can build upon from his currentWorker position
   */
  public List<Point> computeBuildablePoints()   {
    List<Point> buildablePoints = new LinkedList<>();

    try {
      Player player = gameData.getCurrentPlayer();
      Board board = gameData.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);
      candidatePositions.add(workerPosition);

      buildablePoints =
          candidatePositions.stream()
                .filter(p -> constructionConditions.stream().anyMatch(cond -> cond.apply(gameData, p)))
                .filter(p -> constructionConstraints.stream().allMatch(cond -> cond.apply(gameData, p)))
                .collect(Collectors.toList());

    } catch (ItemNotFoundException ignored) {
    }

    if (buildablePoints.isEmpty()) {
      gameData.getCurrentPlayer().setHasLost(true);
    }
    return buildablePoints;
  }

  /**
   * Function that computes the next phase of the current player's turn
   * @param gameData is the gameData all players are currently playing
   * @return the phase subsequent to the one currently in place
   */
  public Phase computeNextPhase(GameDataAccessor gameData){

    Phase next = nextPhase.apply(gameData);

    if(next.equals(Phase.Start)){

      resetPlayerInfo(gameData);
      gameData.getCurrentPlayer().getGodCard().movementConstraints.clear();
      gameData.getCurrentPlayer().getGodCard().constructionConstraints.clear();
      GameEventManager.raise(new GodCardUpdateEvent(this, gameData.getCurrentPlayer()));
      gameData.nextPlayer();
    }

    return next;
  }

  /**
   * This method resets the tracking info of the Player
   * @param gameData is the gameData all players are currently playing
   */
  private void resetPlayerInfo(GameDataAccessor gameData){
    gameData.getCurrentPlayer().setWorkerSelected(false);
    gameData.getCurrentPlayer().getMovementList().clear();
    gameData.getCurrentPlayer().getConstructionList().clear();
    gameData.getCurrentPlayer().setBoardStatus(gameData.getBoard());
  }

  public List<Movement> consequences(Movement movement) {
    return listMovementEffects.apply(gameData, movement);
  }

  public List<Construction> consequences(Construction construction){
    return listConstructionEffects.apply(gameData, construction);
  }

  public boolean validate(List<Point> list, Movement movement){
    return validateMovement.apply(list, movement);
  }

  public boolean validate(List<Point> list, Construction construction){
    return validateConstruction.apply(list, construction);
  }

  public GodName getName(){
    return name;
  }

  public void setGameData(GameDataAccessor gameData) {
    this.gameData = gameData;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Two GodCard objects are equal if they have the same name
   *
   * @param obj The object that this should be compared to.
   * @return If this object is equal to obj.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GodCard) {
      GodCard other = (GodCard) obj;
      return name.equals(other.name);
    } else {
      return super.equals(obj);
    }
  }

  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getMovementConditions() {
    return movementConditions;
  }

  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getMovementConstraints() {
    return movementConstraints;
  }

  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getConstructionConditions() {
    return constructionConditions;
  }

  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getConstructionConstraints() {
    return constructionConstraints;
  }

  public Set<SerializablePredicate<Movement>> getWinningConditions() {
    return winningConditions;
  }

  public Set<SerializablePredicate<Movement>> getWinningConstraints() {
    return winningConstraints;
  }
}
