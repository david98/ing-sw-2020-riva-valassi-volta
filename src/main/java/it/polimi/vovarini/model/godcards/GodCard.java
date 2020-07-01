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
   *
   * @param name Name of the Card I want to create, must be a value of the GodName enumeration
   */
  GodCard(GodName name) {
    this.name = name;
    initCollections();
  }

  /**
   * Initialization of all the Collections containing the different Lambda functions to evaluate
   */
  private void initCollections() {
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


  SerializableBiFunction<GameDataAccessor, Point, Boolean> isPointBuildable =
          (GameDataAccessor gameData, Point point) -> {
            try {
              Worker currentWorker = gameData.getCurrentPlayer().getCurrentWorker();
              Point currentWorkerPosition = gameData.getBoard().getItemPosition(currentWorker);
              if (!point.isAdjacent(currentWorkerPosition)) {
                return false;
              }

              var destinationItems = gameData.getBoard().getBox(point).getItems();
              return Arrays.stream(Block.blocks)
                      .anyMatch(block -> block.canBePlacedOn(destinationItems.peek()));

            } catch (ItemNotFoundException | InvalidPositionException e) {
              System.err.println("This really should never happen...");
            }
            return false;
          };

  /**
   * Computes the normal flow of the game from Phase.Start
   * @param gameData is the Data of the game all players are currently playing
   * @return Phase.Movement if the player selected one of his workers, Phase.Start if he did not
   */
  public static Phase normalNextPhaseFromStart(GameDataAccessor gameData) {
    if (gameData.getCurrentPlayer().isWorkerSelected()) {
      return Phase.Movement;
    } else {
      return Phase.Start;
    }
  }

  /**
   * Computes the normal flow of the game from Phase.Movement
   * @param gameData is the Data of the game all players are currently playing
   * @return Phase.Construction if the player's movementList is not empty, Phase.Movement otherwise
   */
  public static Phase normalNextPhaseFromMovement(GameDataAccessor gameData) {
    if (gameData.getCurrentPlayer().getMovementList().isEmpty()) {
      return Phase.Movement;
    } else {
      return Phase.Construction;
    }
  }

  /**
   * Computes the normal flow of the game from Phase.Construction
   * @param gameData is the Data of the game all players are currently playing
   * @return Phase.End if the player's constructionList is not empty, Phase.Construction otherwise
   */
  public static Phase normalNextPhaseFromConstruction(GameDataAccessor gameData) {
    if (gameData.getCurrentPlayer().getConstructionList().isEmpty()) {
      return Phase.Construction;
    } else {
      return Phase.End;
    }
  }

  /**
   * Computes the normal flow of the game from Phase.End
   * @param gameData is the Data of the game all players are currently playing
   * @return Phase.Start
   */
  public static Phase normalNextPhaseFromEnd(GameDataAccessor gameData) {
    return Phase.Start;
  }


  SerializableBiFunction<GameDataAccessor, Boolean, Phase> nextPhase =
          (GameDataAccessor gameData, Boolean skipIfPossible) -> {
            switch (gameData.getCurrentPhase()) {
              case Start -> {
                return normalNextPhaseFromStart(gameData);
              }
              case Movement -> {
                return normalNextPhaseFromMovement(gameData);
              }
              case Construction -> {
                return normalNextPhaseFromConstruction(gameData);
              }
              case End -> {
                return normalNextPhaseFromEnd(gameData);
              }
            }
            return Phase.Start; //will never be reached
          };


  SerializableBiFunction<GameDataAccessor, Movement, List<Movement>> listMovementEffects =
          (GameDataAccessor gameData, Movement movement) -> {
            List<Movement> movementList = new LinkedList<>();
            movementList.add(movement);
            return movementList;
          };


  SerializableBiFunction<GameDataAccessor, Construction, List<Construction>> listConstructionEffects =
          (GameDataAccessor gameData, Construction construction) -> {
            List<Construction> constructionList = new LinkedList<>();
            constructionList.add(construction);
            return constructionList;
          };


  SerializableBiFunction<List<Point>, Movement, Boolean> validateMovement =
          (List<Point> list, Movement movement) -> list.contains(movement.getEnd());


  SerializableBiFunction<List<Point>, Construction, Boolean> validateConstruction =
          (List<Point> list, Construction construction) -> {
            Block b = construction.getBlock();
            Point t = construction.getTarget();
            var s = gameData.getBoard().getBox(t).getItems();
            return list.contains(construction.getTarget()) &&
                    b.canBePlacedOn(s.peek());
          };


  SerializablePredicate<Movement> isMovementWinning =
          (Movement movement) -> {
            int endLevel = movement.getBoard().getBox(movement.getEnd()).getLevel();
            if (endLevel != Block.WIN_LEVEL) {
              return false;
            }
            int currentLevel = movement.getBoard().getBox(movement.getStart()).getLevel();

            return currentLevel < Block.WIN_LEVEL;
          };


  SerializableBiFunction<GameDataAccessor, Point, Boolean> constraintMovement =
          (GameDataAccessor gameData, Point p) -> true;

  private HashSet<SerializableBiFunction<GameDataAccessor, Point, Boolean>> movementConditions;
  private HashSet<SerializableBiFunction<GameDataAccessor, Point, Boolean>> movementConstraints;

  private HashSet<SerializableBiFunction<GameDataAccessor, Point, Boolean>> constructionConditions;
  private HashSet<SerializableBiFunction<GameDataAccessor, Point, Boolean>> constructionConstraints;

  private HashSet<SerializablePredicate<Movement>> winningConditions;
  private HashSet<SerializablePredicate<Movement>> winningConstraints;

  /**
   * Function that computes a list of all the points where moving is possible
   *
   * @return a list of points that the player can reach from his currentWorker position
   */
  public List<Point> computeReachablePoints() {
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

    return reachablePoints;
  }

  /**
   * Function that computes if a player has won thanks to a valid movement he wants to perform
   *
   * @param movement is the movement move that the player wants to perform
   * @return true if the movement allows the player to win, false otherwise
   */
  public boolean isMovementWinning(Movement movement) {
    return !movement.isForced() && winningConditions.stream().anyMatch(cond -> cond.test(movement)) &&
            winningConstraints.stream().allMatch(cond -> cond.test(movement));
  }

  /**
   * Function that computes a list of all the points where building a block, independently by the level, is possible
   *
   * @return a list of points that the player can build upon from his currentWorker position
   */
  public List<Point> computeBuildablePoints() {
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

    return buildablePoints;
  }

  /**
   * Method to compute the next phase you should go to
   *
   * @param gameData is the gameData all players are currently playing
   * @return the phase subsequent to the one currently in place
   */
  public Phase computeNextPhase(GameDataAccessor gameData) {
    return computeNextPhase(gameData, false);
  }

  /**
   * Computes the next phase based on the current game state.
   *
   * @param gameData An accessor for data pertaining to the game.
   * @param skipIfPossible If the player wants to skip an optional phase even though
   *                       they could perform a move.
   * @return the phase subsequent to the one currently in place
   */
  public Phase computeNextPhase(GameDataAccessor gameData, boolean skipIfPossible) {
    Phase previous = gameData.getCurrentPhase();
    Phase next;

    next = nextPhase.apply(gameData, skipIfPossible);

    if (!previous.equals(Phase.Start) && next.equals(Phase.Start)) {
      resetPlayerInfo(gameData);
      gameData.getCurrentPlayer().getGodCard().movementConstraints.clear();
      gameData.getCurrentPlayer().getGodCard().constructionConstraints.clear();
      GameEventManager.raise(new GodCardUpdateEvent(gameData, this, gameData.getCurrentPlayer()));
      gameData.nextPlayer();
    }

    return next;
  }

  /**
   * This method resets the tracking info of the Player
   *
   * @param gameData is the gameData all players are currently playing
   */
  private void resetPlayerInfo(GameDataAccessor gameData) {
    gameData.getCurrentPlayer().setWorkerSelected(false);
    gameData.getCurrentPlayer().getMovementList().clear();
    gameData.getCurrentPlayer().getConstructionList().clear();
    gameData.getCurrentPlayer().setBoardStatus(gameData.getBoard());
  }

  /**
   * This method computes the consequences of a specific action of a GodCard regarding movement
   * @param movement is the Movement move the GodCard's owner wants to perform
   * @param gameData is the Data of the game all players are currently playing
   * @return a list of Movements to perform sequentially to grant the correct application of the GodCard's own effect
   */
  public List<Movement> consequences(Movement movement, GameDataAccessor gameData) {
    return listMovementEffects.apply(this.gameData, movement);
  }

  /**
   * This method computes the consequences of a specific action of a GodCard regarding construction
   * @param construction is the Movement move the GodCard's owner wants to perform
   * @param gameData is the Data of the game all players are currently playing
   * @return a list of Constructions to perform sequentially to grant the correct application of the GodCard's own effect
   */
  public List<Construction> consequences(Construction construction, GameDataAccessor gameData) {
    return listConstructionEffects.apply(gameData, construction);
  }

  /**
   * This method computes if a Movement move is valid when compared to the rules of the game
   * @param list is a list of all the Reachable points the worker performing the movement can reach
   * @param movement is the Movement move the player wants to perform
   * @return true if the move is valid, false otherwise
   */
  public boolean validate(List<Point> list, Movement movement) {
    return validateMovement.apply(list, movement);
  }

  /**
   * This method computes if a Construction move is valid when compared to the rules of the game
   * @param list is a list of all the Buildable points the worker performing the construction can reach
   * @param construction is the Construction move the player wants to perform
   * @return true if the move is valid, false otherwise
   */
  public boolean validate(List<Point> list, Construction construction) {
    return validateConstruction.apply(list, construction);
  }

  /**
   * Getter method for the GodCard's name
   * @return the name of the GodCard (one of the values of the GodName enumeration)
   */
  public GodName getName() {
    return name;
  }

  /**
   * Setter method for the GodCard's gameData
   * @param gameData is the Data of the game I want to set as focus for the GodCard
   */
  public void setGameData(GameDataAccessor gameData) {
    this.gameData = gameData;
  }

  /**
   * hashCode method for a GodCard
   * @return an hashCode representing the GodCard
   */
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

  /**
   * Method that returns a Set containing all the MovementConditions injected by GodCards
   * @return a Set containing all the MovementConditions injected by GodCards
   */
  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getMovementConditions() {
    return movementConditions;
  }

  /**
   * Method that returns a Set containing all the MovementConstraints injected by GodCards
   * @return a Set containing all the MovementConstraints injected by GodCards
   */
  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getMovementConstraints() {
    return movementConstraints;
  }

  /**
   * Method that returns a Set containing all the ConstructionConditions injected by GodCards
   * @return a Set containing all the ConstructionConditions injected by GodCards
   */
  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getConstructionConditions() {
    return constructionConditions;
  }

  /**
   * Method that returns a Set containing all the ConstructionConstraints injected by GodCards
   * @return a Set containing all the ConstructionConstraints injected by GodCards
   */
  public Set<SerializableBiFunction<GameDataAccessor, Point, Boolean>> getConstructionConstraints() {
    return constructionConstraints;
  }

  /**
   * Method that returns a Set containing all the WinningConditions injected by GodCards
   * @return a Set containing all the WinningConditions injected by GodCards
   */
  public Set<SerializablePredicate<Movement>> getWinningConditions() {
    return winningConditions;
  }

  public Set<SerializablePredicate<Movement>> getWinningConstraints() {
    return winningConstraints;
  }
}
