package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.godcards.deciders.*;

/**
 * GodCardFactory creates a specific GodCard injecting dynamically
 * particular methods that pertain to a particular card
 *
 * @author Mattia Valassi
 * @author Marco Riva
 */
public class GodCardFactory {

    private GodCardFactory(){
      super();
    }

    /**
     * This method applies a switch-case structure to create all the cards currently supported by our game
     * @param name part of the GodName enumeration, it specifies what card we want to create, example Apollo, Artemis...
     * @return the created GodCard with all the dynamic assignments of methods
     */
    public static GodCard create(GodName name) {
      switch (name) {
        case Apollo -> {
          return createApollo();
        }
        case Artemis -> {
          return createArtemis();
        }
        case Athena -> {
          return createAthena();
        }
        case Atlas -> {
          return createAtlas();
        }
        case Demeter -> {
          return createDemeter();
        }
        case Hephaestus -> {
          return createHephaestus();
        }
        case Hera -> {
          return createHera();
        }
        case Hestia -> {
          return createHestia();
        }
        case Minotaur -> {
          return createMinotaur();
        }
        case Pan -> {
          return createPan();
        }
        case Poseidon -> {
          return createPoseidon();
        }
        case Prometheus -> {
          return createPrometheus();
        }
        case Triton -> {
          return createTriton();
        }
        case Zeus -> {
          return createZeus();
        }
        default -> {
          return createNobody();
        }
      }
  }

  /**
   * Creates a deep clone of a GodCard.
   * @param c The card to be cloned.
   * @return A clone of c.
   */
  public static GodCard clone(GodCard c){
    GodCard clone = new GodCard(c.name);
    clone.getMovementConditions().addAll(c.getMovementConditions());
    clone.getMovementConstraints().addAll(c.getMovementConstraints());

    clone.getConstructionConditions().addAll(c.getConstructionConditions());
    clone.getConstructionConstraints().addAll(c.getConstructionConstraints());

    clone.getWinningConditions().addAll(c.getWinningConditions());
    clone.getWinningConstraints().addAll(c.getWinningConstraints());

    return clone;
  }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Apollo
   * @return an instance of a GodCard in the mold of Santorini's Apollo card
   */
  private static GodCard createApollo() {
    GodCard apollo = new GodCard(GodName.Apollo);
    apollo.getMovementConditions().add(ReachabilityDecider::canExchangeWithWorker);
    return apollo;
  }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Artemis
   * @return an instance of a GodCard in the mold of Santorini's Artemis card
   */
  private static GodCard createArtemis(){
    GodCard artemis = new GodCard(GodName.Artemis);
    artemis.nextPhase = FlowDecider::extendsMovement;
    return artemis;
  }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Athena
   * @return an instance of a GodCard in the mold of Santorini's Athena card
   */
  private static GodCard createAthena() {
    GodCard athena = new GodCard(GodName.Athena);
    athena.nextPhase = FlowDecider::applyMalus;
    return athena;
  }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Hera
     * @return an instance of a GodCard in the mold of Santorini's Hera card
     */
    private static GodCard createHera(){
      GodCard hera = new GodCard(GodName.Hera);
      hera.getWinningConstraints().add(WinDecider::perimeterConstraint);
      return hera;
    }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Hestia
     * @return an instance of a GodCard in the mold of Santorini's Hestia card
     */
    private static GodCard createHestia(){
      GodCard hestia = new GodCard(GodName.Hestia);
      hestia.nextPhase = FlowDecider::extendsConstruction;
      return hestia;
    }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Minotaur
     * @return an instance of a GodCard in the mold of Santorini's Minotaur card
     */
    private static GodCard createMinotaur() {
        GodCard minotaur = new GodCard(GodName.Minotaur);
        minotaur.getMovementConditions().add(ReachabilityDecider::conditionedExchange);
        minotaur.listMovementEffects = ConsequencesDecider::forceOpponentWorker;
        return minotaur;
    }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Atlas
   * @return an instance of a GodCard in the mold of Santorini's Atlas card
   */
  private static GodCard createAtlas() {
    GodCard atlas = new GodCard(GodName.Atlas);
    atlas.validateConstruction = ValidationDecider::allowDome;
    return atlas;
  }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Demeter
   * @return an instance of a GodCard in the mold of Santorini's Demeter card
   */
  private static GodCard createDemeter() {
    GodCard demeter = new GodCard(GodName.Demeter);
    demeter.nextPhase = FlowDecider::extendsConstruction;
    return demeter;
  }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Hephaestus
   * @return an instance of a GodCard in the mold of Santorini's Hephaestus card
   */
  private static GodCard createHephaestus() {
    GodCard hephy = new GodCard(GodName.Hephaestus);
    hephy.nextPhase = FlowDecider::extendsConstruction;
    return hephy;
  }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Poseidon
     * @return an instance of a GodCard in the mold of Santorini's Poseidon card
     */
    private static GodCard createPoseidon(){
      GodCard poseidon = new GodCard(GodName.Poseidon);
      poseidon.nextPhase = FlowDecider::extendsConstruction;
      return poseidon;
    }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Triton
     * @return an instance of a GodCard in the mold of Santorini's Triton card
     */
    private static GodCard createTriton(){
      GodCard triton = new GodCard(GodName.Triton);
      triton.nextPhase = FlowDecider::extendsMovement;
      return triton;
    }

    /**
     * This method injects a generic GodCard with all the Behaviors modified by the card Zeus
     * @return an instance of a GodCard in the mold of Santorini's Zeus card
     */
    private static GodCard createZeus(){
      GodCard zeus = new GodCard(GodName.Zeus);
      zeus.validateConstruction = ValidationDecider::allowUnderMyself;
      zeus.getConstructionConditions().add(BuildabilityDecider::buildUnderMyself);
      return zeus;
    }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Pan
   * @return an instance of a GodCard in the mold of Santorini's Pan card
   */
  private static GodCard createPan() {
    GodCard pan = new GodCard(GodName.Pan);
    pan.getWinningConditions().add(WinDecider::downTwoLevels);
    return pan;
  }

  /**
   * This method injects a generic GodCard with all the Behaviors modified by the card Prometheus
   * @return an instance of a GodCard in the mold of Santorini's Prometheus card
   */
  private static GodCard createPrometheus(){
    GodCard prometheus = new GodCard(GodName.Prometheus);
    prometheus.nextPhase = FlowDecider::buildBeforeAndAfter;
    return prometheus;
  }

  /**
   * This method just generates an empty card without any effect on the game
   * @return a generic instance of an "empty" GodCard, if someone wants to play with the standard
   * set of rules and without the influence of a card
   */
  private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
