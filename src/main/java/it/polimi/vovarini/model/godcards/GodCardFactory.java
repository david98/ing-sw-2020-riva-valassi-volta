package it.polimi.vovarini.model.godcards;

/**
 * @class GodCardFactory creates a specific GodCard injecting dynamically particular methods that pertain to a particular card
 */
public class GodCardFactory {

    /**
     * This method applies a switch-case structure to create all the cards currently supported by our game
     * @param name part of the GodName enumeration, it specifies what card we want to create, example Apollo, Artemis...
     * @return the created GodCard with all the dynamic assignments of methods
     */
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

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Apollo
     * @return an instance of a GodCard in the mold of Santorini's Apollo card
     */
  private static GodCard createApollo() {
    GodCard apollo = new GodCard(GodName.Apollo);
    apollo.isPointReachable = Reachability::isPointReachableCanExchangeWithWorker;
    return apollo;
  }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Minotaur
     * @return an instance of a GodCard in the mold of Santorini's Minotaur card
     */
  private static GodCard createMinotaur() {
      GodCard minotaur = new GodCard(GodName.Minotaur);
      minotaur.isPointReachable = Reachability::isPointReachableMinotaur;
      return minotaur;
  }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Pan
     * @return an instance of a GodCard in the mold of Santorini's Pan card
     */
  private static GodCard createPan() {
    GodCard pan = new GodCard(GodName.Pan);
    pan.isMovementWinning = WinningCondition::isWinningPan;
    return pan;
  }

    /**
     * This method just generates an empty card without any effect on the game
     * @return a generic instance of an "empty" GodCard, if someone wants to play with the std set of rules and without the influence of a card
     */
  private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
