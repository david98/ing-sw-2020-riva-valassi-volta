package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.CurrentPlayerLosesException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The GodCard class represents a general GodCard
 * game is the current game played by all the players
 * name references one of the cards available in the base set of Santorini
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @author Davide Volta
 * @version 0.2
 * @since 0.1
 */
public class GodCard implements Cloneable{
  protected Game game;
  protected GodName name;

  /**
   * Constructor method of GodCard class without game assignment (if the card is created before starting the game)
   * @param name Name of the Card I want to create, must be a value of the GodName enumeration
   */
  public GodCard(GodName name) {
    this.name = name;
    initCollections();
  }

  /**
   * Constructor method of GodCard
   * @param name Name of the Card I want to create, must be a value of the GodName enumeration
   * @param game Instance of game currently played by all the players
   */
  public GodCard(GodName name, Game game) {
    this.name = name;
    this.game = game;
    initCollections();
  }

  /**
   * Initialization of tall the Collections containing the different Lambda functions to evaluate
   */
  private void initCollections(){
    movementConditions = new HashSet<>();
    movementConstraints = new HashSet<>();

    buildingConditions = new HashSet<>();
    buildingConstraints = new HashSet<>();

    winningConditions = new HashSet<>();
    winningConstraints = new HashSet<>();

    movementConditions.add(isPointReachable);
    buildingConditions.add(isPointBuildable);
    winningConditions.add(isMovementWinning);
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
   * Lambda function that returns the next phase of the turn following the standard flow
   * @param game Instance of game currently played by all the players
   * @return the next phase to play, according to the normal flow of the game
   */
  Function<Game, Phase> nextPhase =
          (Game game) -> game.getCurrentPhase().next();


  /**
   * Lambda function presenting the base Behavior for consequences, regarding the Movements
   * @param game Instance of game currently played by all the players
   * @param movement is the movement move the player wants to perform, which is already been validated
   * @return list of moves to execute
   */
  BiFunction<Game, Movement, List<Movement>> listMovementEffects =
          (Game game, Movement movement) -> {
            List<Movement> movementList = new LinkedList<>();
            movementList.add(movement);
            return movementList;
          };

  /**
   * Lambda function presenting the base Behavior for consequences, regarding the Constructions
   * @param game Instance of game currently played by all the players
   * @param construction is the construction move the player wants to perform, which is already been validated
   * @return list of moves to execute
   */
  BiFunction<Game, Construction, List<Construction>> listConstructionEffects =
          (Game game, Construction construction) -> {
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
  BiFunction<List<Point>, Movement, Boolean> validateMovement =
          (List<Point> list, Movement movement) -> {
            return list.contains(movement.getEnd());
  };

  /**
   * Lambda function with base validation of constructions
   * @param list is the list of points computed by the pre-move method {@link #computeBuildablePoints()}
   * @param construction is the construction move the player wants to perform
   * @return if the move that the player wants to perform is valid returns true, false otherwise
   */
  BiFunction<List<Point>, Construction, Boolean> validateConstruction =
          (List<Point> list, Construction construction) -> {
            try {
                  return list.contains(construction.getTarget()) && construction.getBlock().canBePlacedOn(game.getBoard().getItems(construction.getTarget()).peek());
            } catch (InvalidPositionException ignored){
              System.err.println("This should really never happen...");
            } catch (BoxEmptyException e){
              return construction.getBlock().getLevel() == 1;
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
        int endLevel = movement.getBoard().getBox(movement.getEnd()).getLevel();
        if (endLevel != Block.WIN_LEVEL) {
          return false;
        }
        int currentLevel = movement.getBoard().getBox(movement.getStart()).getLevel();

        return currentLevel < Block.WIN_LEVEL;
      };

  /**
   * Lambda function with base constraint of movements
   * @param game Instance of game currently played by all the players
   * @param point is the destination of movement selected by the current player
   * @return if the move that the player wants to perform is valid returns true, false otherwise
   */
  BiFunction<Game, Point, Boolean> constraintMovement = (Game game, Point p) -> {
    return true;
  };

    Collection<BiFunction<Game, Point, Boolean>> movementConditions;
    Collection<BiFunction<Game, Point, Boolean>> movementConstraints;

    Collection<BiFunction<Game, Point, Boolean>> buildingConditions;
    Collection<BiFunction<Game, Point, Boolean>> buildingConstraints;

    Collection<Predicate<Movement>> winningConditions;
    Collection<Predicate<Movement>> winningConstraints;

  /**
   * Function that computes a list of all the points where moving is possible
   * @return a list of points that the player can reach from his currentWorker position
   */
  public List<Point> computeReachablePoints()   {
    List<Point> reachablePoints = new LinkedList<>();

    try {
      Player player = game.getCurrentPlayer();
      Board board = game.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      reachablePoints =
          candidatePositions.stream()
              .filter(p -> movementConditions.stream().anyMatch(cond -> cond.apply(game, p)))
                  .filter(p -> movementConstraints.stream().allMatch(cond -> cond.apply(game, p)))
              .collect(Collectors.toList());
    } catch (ItemNotFoundException ignored) {
    }

    if (reachablePoints.isEmpty()) {
      game.getCurrentPlayer().setHasLost(true);
    }
    return reachablePoints;
  }

  /**
<<<<<<< HEAD
   * Function that computes if a player has won thanks to a valid movement he wants to perform
   * @param movement is the movement move that the player wants to perform
   * @return true if the movement allows the player to win, false otherwise
=======
   *
   * @param movement The move to be analysed
   * @return true if the movement Move leads to victory, false otherwise
>>>>>>> godcards/mengi
   */
  public boolean isMovementWinning(Movement movement) {
    return !movement.isForced() && winningConditions.stream().anyMatch(cond -> cond.test(movement)) &&
            winningConstraints.stream().noneMatch(cond -> cond.test(movement));
  }

  /**
   * Function that computes a list of all the points where building a block, independently by the level, is possible
   * @return a list of points that the player can build upon from his currentWorker position
   */
  public List<Point> computeBuildablePoints()   {
    List<Point> buildablePoints = new LinkedList<>();

    try {
      Player player = game.getCurrentPlayer();
      Board board = game.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      buildablePoints =
          candidatePositions.stream()
                .filter(p -> buildingConditions.stream().anyMatch(cond -> cond.apply(game, p)))
                .filter(p -> buildingConstraints.stream().allMatch(cond -> cond.apply(game, p)))
                .collect(Collectors.toList());

    } catch (ItemNotFoundException ignored) {
    }

    if (buildablePoints.isEmpty()) {
      game.getCurrentPlayer().setHasLost(true);
    }
    return buildablePoints;
  }

  /**
   * Function that computes the next phase of the current player's turn
   * @param game is the game all players are currently playing
   * @return the phase subsequent to the one currently in place
   */
  public Phase computeNextPhase(Game game){

    Phase next = nextPhase.apply(game);

    if(next.equals(Phase.Start)){

      game.nextPlayer();
      resetPlayerInfo(game);
    }

    return next;
  }

  /**
   * This method resets the tracking info of the Player
   * @param game is the game all players are currently playing
   */
  private void resetPlayerInfo(Game game){
    game.getCurrentPlayer().setWorkerSelected(false);
    game.getCurrentPlayer().getMovementList().clear();
    game.getCurrentPlayer().getConstructionList().clear();
    game.getCurrentPlayer().setBoardStatus(game.getBoard().clone());
  }

  public List<Movement> consequences(Movement movement) {
    return listMovementEffects.apply(game, movement);
  }

  public List<Construction> consequences(Construction construction){
    return listConstructionEffects.apply(game, construction);
  }

  public boolean validate(List<Point> list, Movement movement){
    return validateMovement.apply(list, movement);
  }

  public boolean validate(List<Point> list, Construction construction){
    return validateConstruction.apply(list, construction);
  }

  /**
   * Messaggio per Valas: alla fine questo è solo un contenitore di vincoli relativi ai movement
   * @param game
   * @param p
   * @return
   */
  public boolean constraintMovement(Game game, Point p) {
    return constraintMovement.apply(game, p);
  }

  public GodName getName(){
    return name;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public GodCard clone(){
    try{
      return (GodCard) super.clone();
    } catch (CloneNotSupportedException e){
      throw new RuntimeException(e);
    }
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
