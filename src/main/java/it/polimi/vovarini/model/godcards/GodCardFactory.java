package it.polimi.vovarini.model.godcards;

/**
 * GodCardFactory creates a specific GodCard injecting dynamically
 * particular methods that pertain to a particular card
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
      case Athena:
      {
        return createAthena();
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
      apollo.movementConditions.add(Reachability::isPointReachableCanExchangeWithWorker);
      return apollo;
    }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Athena
     * @return an instance of a GodCard in the mold of Santorini's Athena card
     */
    private static GodCard createAthena() {
      GodCard athena = new GodCard(GodName.Athena);
      return athena;
    }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Minotaur
     * @return an instance of a GodCard in the mold of Santorini's Minotaur card
     */
    private static GodCard createMinotaur() {
        GodCard minotaur = new GodCard(GodName.Minotaur);
        minotaur.movementConditions.add(Reachability::isPointReachableConditionedExchange);
        minotaur.listEffects = ListEffects::listEffectsMinotaur;
        return minotaur;
    }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Pan
     * @return an instance of a GodCard in the mold of Santorini's Pan card
     */
    private static GodCard createPan() {
      GodCard pan = new GodCard(GodName.Pan);
      pan.winningConditions.add(WinningCondition::isWinningPan);
      return pan;
    }

    private static GodCard createDemeter() {
      GodCard demeter = new GodCard(GodName.Demeter);
      demeter.nextPhase = TurnFlow::nextPhaseExtendsConstruction;
      return demeter;
    }

    private static GodCard createHephaestus(){
      GodCard hephy = new GodCard(GodName.Hephaestus);
      hephy.nextPhase = TurnFlow::nextPhaseExtendsConstruction;
      return hephy;
    }

    private static GodCard createArtemis(){
      GodCard artemis = new GodCard(GodName.Artemis);
      artemis.nextPhase = TurnFlow::nextPhaseExtendsMovement;
      artemis.movementConditions.add(Reachability::isPointReachablePreviousBoxDenied);
      return artemis;
    }

    private static GodCard createPrometheus(){
      GodCard prometheus = new GodCard(GodName.Prometheus);
      prometheus.nextPhase = TurnFlow::nextPhaseConstructionTwice;
      return prometheus;
    }

    /**
     * This method just generates an empty card without any effect on the game
     * @return a generic instance of an "empty" GodCard, if someone wants to play with the std set of rules and without the influence of a card
     */
    private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
