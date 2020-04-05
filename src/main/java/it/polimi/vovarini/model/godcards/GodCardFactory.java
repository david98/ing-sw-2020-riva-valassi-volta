package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.Stack;
import java.util.function.BiFunction;

public class GodCardFactory {

  private static BiFunction<Game, Point, Boolean> isPointReachableCanExchangeWithWorker =
          (Game game, Point point) -> {
            try {
              Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
              Point currentWorkerPosition = game.getBoard()
                      .getItemPosition(currentWorker);
              if (!point.isAdjacent(currentWorkerPosition)){
                return false;
              }

              try {
                Stack<Item> destinationItems = game.getBoard().getItems(point);

                int destinationLevel = destinationItems.size();
                int currentWorkerLevel = game.getBoard().getItems(currentWorkerPosition).size();
                return (destinationLevel - currentWorkerLevel <= 1) &&
                        (currentWorker.canBePlacedOn(destinationItems.peek()) ||
                                destinationItems.peek().canBeRemoved());
              } catch (BoxEmptyException ignored){
                return true;
              }

            } catch (ItemNotFoundException | InvalidPositionException ignored){
              System.err.println("This really should never happen...");
            }
            return false;

  };
  
  public static GodCard create(GodName name) {
    switch (name) {
      case Apollo:
        {
          return createApollo();
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

  private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
