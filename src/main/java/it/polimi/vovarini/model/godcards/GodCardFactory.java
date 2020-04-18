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

    protected Reachability reachability;
    protected Buildability buildability;
    protected WinningCondition winningCondition;
    protected TurnFlow turnFlow;

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
    apollo.isPointReachable = Reachability::isPointReachableCanExchangeWithWorker;
    return apollo;
  }

  private static GodCard createMinotaur() {
      GodCard minotaur = new GodCard(GodName.Minotaur);
      minotaur.isPointReachable = Reachability::isPointReachableMinotaur;
      return minotaur;
  }

  private static GodCard createPan() {
    GodCard pan = new GodCard(GodName.Pan);
    pan.isMovementWinning = WinningCondition::isWinningPan;
    return pan;
  }

  private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
