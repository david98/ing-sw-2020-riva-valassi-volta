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

            return (destinationLevel - currentWorkerLevel <= 1)
                && (currentWorker.canBePlacedOn(destinationItems.peek())
                    || destinationItems.peek().canBeRemoved());
          } catch (BoxEmptyException ignored) {
            return true;
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

  private static GodCard createPan() {
    GodCard pan = new GodCard(GodName.Pan);
    pan.isMovementWinning = isMovementWinningPan;
    return pan;
  }

  private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
