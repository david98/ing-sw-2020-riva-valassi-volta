package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.GameDataAccessor;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.Objects;

/**
 * ReachabilityDecider is an extension of Decider. It decides what boxes a player can move to
 *
 * @author Mattia Valassi
 * @version 2.0
 * @since 1.0
 */
public class ReachabilityDecider extends Decider {

  /**
   * This method represents an alternative condition on reachability, allowing you to reach an adjacent box occupied by an opponent's worker
   *
   * @param gameData is the gameData currently played by all the players
   * @param point    is the destination of movement selected by the current player
   * @return true if the chosen position is reachable, false if it isn't
   * @author Davide Volta
   * @author Marco Riva
   */
  public static boolean canExchangeWithWorker(GameDataAccessor gameData, Point point) throws RuntimeException {
    try {
      Worker currentWorker = gameData.getCurrentPlayer().getCurrentWorker();
      Point currentWorkerPosition = gameData.getBoard().getItemPosition(currentWorker);
      if (!point.isAdjacent(currentWorkerPosition)) {
        return false;
      }

      Box destinationBox = gameData.getBoard().getBox(point);
      var destinationItems = destinationBox.getItems();
      Worker otherWorker = gameData.getCurrentPlayer().getOtherWorker();

      return (destinationItems.peek() != null && destinationItems.peek().canBeRemoved() &&
              !Objects.equals(destinationItems.peek(), otherWorker));


    } catch (ItemNotFoundException ignored) {
      throw new RuntimeException();
    }

  }

  /**
   * This method represents an alternative condition on reachability, allowing you to reach an adjacent box occupied by an opponent's worker
   * if conditions connected to the player's GodCard are verified
   *
   * @param gameData is the gameData currently played by all the players
   * @param point    is the destination of movement selected by the current player
   * @return true if there is an opponent's worker on the point chosen as the destination and if the opponent's
   * worker can be forced one space straight backwards to an unoccupied space at any level, false otherwise
   * @author Marco Riva
   */
  public static boolean conditionedExchange(GameDataAccessor gameData, Point point) throws RuntimeException {
    try {
      Worker currentWorker = gameData.getCurrentPlayer().getCurrentWorker();
      Point currentWorkerPosition = gameData.getBoard().getItemPosition(currentWorker);

      if (!point.isAdjacent(currentWorkerPosition)) {
        return false;
      }

      Box destinationBox = gameData.getBoard().getBox(point);
      int destinationLevel = destinationBox.getLevel();
      int currentWorkerLevel = gameData.getBoard().getBox(currentWorkerPosition).getLevel();

      // Minotaur's rules
      if (destinationLevel - currentWorkerLevel <= 1) {
        var destinationItems = destinationBox.getItems();
        Worker otherWorker = gameData.getCurrentPlayer().getOtherWorker();

        // se nella casella di destinazione c'è un worker nemico
        if (destinationItems.peek() != null && destinationItems.peek().canBeRemoved() &&
                !Objects.equals(destinationItems.peek(), otherWorker)) {
          Worker enemysWorker = (Worker) destinationItems.peek();
          assert enemysWorker != null;

          // direzione = end - start
          // destinazioneForzata = end + direzione = (2 * end) - start
          int x = 2 * point.getX() - currentWorkerPosition.getX();
          int y = 2 * point.getY() - currentWorkerPosition.getY();
          Point forcedDestination = new Point(x, y);

          // se il punto calcolato è fuori dalla plancia di gioco
          if (!gameData.getBoard().isPositionValid(forcedDestination)) {
            return false;
          }

          Box forcedDestinationBox = gameData.getBoard().getBox(forcedDestination);
          Item forcedDestinationItem = forcedDestinationBox.getItems().peek();

          return forcedDestinationItem == null ||
                  enemysWorker.canBePlacedOn(forcedDestinationItem);

        } else {
          return false;
        }
      }

    } catch (ItemNotFoundException ignored) {
      throw new RuntimeException();
    }
    return false;
  }


  /**
   * This method represents a constraint on reachability, denying you to reach your previous box if you are
   * performing two movements in a row
   *
   * @param gameData is the gameData currently played by all the players
   * @param point    is the destination of movement selected by the current player
   * @return false if the destination point is the initial point, true otherwise
   * @author Mattia Valassi
   * @author Marco Riva
   */
  public static boolean previousBoxDenied(GameDataAccessor gameData, Point point) {
    // mi fido che arrivati qui, la lista abbia un movimento, quindi non controllo se è vuoto
    // anche se sarebbe buona norma farlo... se è vuota, manda eccezione
    Player currentPlayer = gameData.getCurrentPlayer();
    int size = currentPlayer.getMovementList().size();

    return !currentPlayer.getMovementList().get(size - 1).getStart().equals(point);
  }

  /**
   * This method represents a constraint on reachability, denying the other players to level up with a movement
   * if the player currently playing did not level up in his turn
   *
   * @param gameData is the current gameData played by all the players
   * @param point    is the destination of movement selected by the current player
   * @return false if the destination point is higher than the current worker position, true otherwise
   * @author Mattia Valassi
   * @author Marco Riva
   */
  public static boolean cannotMoveUp(GameDataAccessor gameData, Point point) throws RuntimeException {
    try {
      Worker currentWorker = gameData.getCurrentPlayer().getCurrentWorker();
      Point currentWorkerPosition = gameData.getBoard().getItemPosition(currentWorker);

      Box destinationBox = gameData.getBoard().getBox(point);
      int destinationLevel = destinationBox.getLevel();
      int currentWorkerLevel = gameData.getBoard().getBox(currentWorkerPosition).getLevel();

      return destinationLevel - currentWorkerLevel < 1;

    } catch (ItemNotFoundException ignored) {
      throw new RuntimeException();
    }

  }
}