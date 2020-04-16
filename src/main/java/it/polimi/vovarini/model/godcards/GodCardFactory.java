package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Movement;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class GodCardFactory {

  private static BiFunction<Game, Point, Boolean> isPointReachableCanExchangeWithWorker =
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
            Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

            return (destinationLevel - currentWorkerLevel <= 1)
                && (currentWorker.canBePlacedOn(destinationItems.peek())
                    || (destinationItems.peek().canBeRemoved() && !destinationItems.peek().equals(otherWorker)));
          } catch (BoxEmptyException ignored) {
            return true;
          }

        } catch (ItemNotFoundException ignored) {
          System.err.println("This really should never happen...");
        }
        return false;
      };

    private static BiFunction<Game, Point, Boolean> isPointReachableMinotaur =
            (Game game, Point point) -> {
                try {
                    Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
                    Point currentWorkerPosition = game.getBoard().getItemPosition(currentWorker);
                    if (!point.isAdjacent(currentWorkerPosition)) {
                        return false;
                    }

                    Box destinationBox = game.getBoard().getBox(point);
                    Stack<Item> destinationItems = null;

                    try {
                        destinationItems = destinationBox.getItems();
                    } catch (BoxEmptyException ignored) {
                        // la casella di destinazione è adiacente e vuota
                        return true;
                    }

                    int destinationLevel = destinationBox.getLevel();
                    int currentWorkerLevel = game.getBoard().getBox(currentWorkerPosition).getLevel();


                    if(destinationLevel - currentWorkerLevel <= 1) {

                        // General rules
                        if(currentWorker.canBePlacedOn(destinationItems.peek())) {
                            // la casella di destinazione è raggiungibile (adiacenza e livello ok) e libera
                            return true;
                        }

                        // Minotaur's rules
                        Worker otherWorker = game.getCurrentPlayer().getOtherWorker();
                        // se nella casella di destinazione c'è un worker nemico
                        if(destinationItems.peek().canBeRemoved() && !destinationItems.peek().equals(otherWorker)) {
                            Worker enemysWorker = (Worker) destinationItems.peek();
                            int x = 2*point.getX() - currentWorkerPosition.getX();
                            int y = 2*point.getY() - currentWorkerPosition.getY();
                            Point forcedDestination = new Point(x,y);
                            Box forcedDestinationBox = game.getBoard().getBox(forcedDestination);
                            Item forcedDestinationItem = null;

                            // se la casella successiva nella stessa direzione del movimento è vuota
                            try { forcedDestinationItem = forcedDestinationBox.getItems().peek();
                            } catch (BoxEmptyException ignored) {
                                return true;
                            }

                            // se la casella successiva nella stessa direzione del movimento è libera
                            if(enemysWorker.canBePlacedOn(forcedDestinationItem)) {
                                return true;
                            }
                        }
                    }

                } catch (ItemNotFoundException ignored) {
                    System.err.println("This really should never happen...");
                }
                return false;
            };

  private static Predicate<Movement> isMovementWinningPan =
          (Movement movement) -> {
            if (movement.isForced()) {
              return false;
            }
            int endLevel = movement.getBoard().getBox(movement.getEnd()).getLevel();
            int currentLevel = movement.getBoard().getBox(movement.getStart()).getLevel();

            if (endLevel != Block.WIN_LEVEL) {
              return currentLevel - endLevel >= 2;
            }

            return currentLevel < Block.WIN_LEVEL;
          };

  public static GodCard create(GodName name) {
    switch (name) {
      case Apollo:
        {
          return createApollo();
        }
        case Minotaur:
        {
            return createMinotaur();
        }
      case Pan:
      {
        return createPan();
      }
      case Nobody:
      default:
        {
          return createNobody();
        }
    }
  }

  private static GodCard createApollo() {
    GodCard apollo = new GodCard(GodName.Apollo);
    apollo.isPointReachable = isPointReachableCanExchangeWithWorker;
    return apollo;
  }

  private static GodCard createMinotaur() {
      GodCard minotaur = new GodCard(GodName.Minotaur);
      minotaur.isPointReachable = isPointReachableMinotaur;
      return minotaur;
  }

  private static GodCard createPan() {
    GodCard pan = new GodCard(GodName.Pan);
    pan.isMovementWinning = isMovementWinningPan;
    return pan;
  }

  private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
