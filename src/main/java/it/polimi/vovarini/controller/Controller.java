package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.BuildEvent;
import it.polimi.vovarini.controller.events.CardAssignmentEvent;
import it.polimi.vovarini.controller.events.CardChoiceEvent;
import it.polimi.vovarini.controller.events.WorkerEvent;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.controller.events.MovementEvent;
import it.polimi.vovarini.controller.events.NextPlayerEvent;
import it.polimi.vovarini.controller.events.RegistrationEvent;
import it.polimi.vovarini.controller.events.UndoEvent;
import it.polimi.vovarini.model.*;

import java.util.*;

public class Controller implements EventListener {

  private ArrayList<GodName> selectedCards;
  private final Game game;

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    this.game = game;
    selectedCards = new ArrayList<GodName>();
  }

  // CLI: keyboard M,F characters
  public void update(WorkerEvent evt) throws InvalidPhaseException {
    Phase currentPhase = game.getCurrentPhase();

    if (!currentPhase.equals(Phase.Start)) throw new InvalidPhaseException();
    game.getCurrentPlayer().setCurrentSex(evt.getSex());
    game.nextPhase();
  }

  // CLI: Coordinates input or keyboard arrows
  public void update(BuildEvent evt)
      throws InvalidPositionException, InvalidPhaseException, NonBuildablePositionException {
    Point input = new Point(evt.getBuildEnd());
    if (!game.getBoard().isPositionValid(input)) throw new InvalidPositionException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Construction)) throw new InvalidPhaseException();

    boolean pointFound = false;
    Block blockToBuild = null;
    int levelToBuild = 0;
    try {
      for (Point point : game.getCurrentPlayer().getGodCard().computeBuildablePoints()) {
        if (point.equals(input)) {
          pointFound = true;
          try {
            Stack<Item> currentStack = game.getBoard().getBox(point).getItems();
            if (currentStack.peek() instanceof Worker) currentStack.pop();
            if (currentStack.empty()) levelToBuild = 1;
            else {
              Block topBlock = (Block) currentStack.pop();
              levelToBuild = topBlock.getLevel() + 1;
            }
          } catch (BoxEmptyException e) {
            levelToBuild = 1;
          } finally {
            try {
              blockToBuild = new Block(levelToBuild);
            } catch (InvalidLevelException ignored) {
            }
          }
          Move newMove = new Construction(game.getBoard(), blockToBuild, point);
          game.performMove(newMove);
          break;
        }
      }
    } catch (CurrentPlayerLosesException e) {
      // method required in model or RemoteView to signal the player which triggered the exception
    }

    if (!pointFound) {
      throw new NonBuildablePositionException();
    }
  }

  // Not part of the 1vs1 simulation we want to develop now
  public void update(CardChoiceEvent evt) {
    for (GodName card : evt.getSelectedCards()) {
      this.selectedCards.add(card);
    }
  }

  // Not part of the 1vs1 simulation we want to develop now
  public void update(CardAssignmentEvent evt)
      throws CardsNotSelectedException, InvalidCardException {
    if (this.selectedCards.isEmpty()) {
      throw new CardsNotSelectedException();
    } else {
      if (!this.selectedCards.contains(evt.getAssignedCard())) {
        throw new InvalidCardException();
      }

      // GodCard playerCard = new GodCard(game, evt.getAssignedCard()); (new card instance)
      // game.getCurrentPlayer().setGodCard(playerCard);
    }
  }

  // CLI: String input at the start of the game. Any string longer than one should be considered a
  // nickname input
  public void update(RegistrationEvent evt)
      throws InvalidPhaseException, NicknameAlreadyInUseException {
    /*Va controllata lunghezza/serie di spazi/caratteri speciali?
    if (evt.getNickname() == null) {
      // throw new NicknameNullException();
    }*/

    // Questa perché è commentata? Sicuramente questo controllo va fatto
    /*if(game.isNicknameAvailable(evt.getNickname())) {
      game.addPlayer(evt.getNickname());
    } else {
      throw new NicknameAlreadyInUseException();
    }*/

    // Questo pezzo di codice andrà dentro addPlayer(String nickname) nel Model (players instanziati
    // dopo inserimento del nickname)
    for (Player player : game.getPlayers()) {
      if (player.getNickname().equalsIgnoreCase(evt.getNickname())) {
        throw new NicknameAlreadyInUseException();
      }
    }
  }

  // CLI: Ctrl+Z pressed by the user
  public void update(UndoEvent evt) throws InvalidPhaseException {

    if (!game.getCurrentPhase().equals(Phase.Movement)
        || !game.getCurrentPhase().equals(Phase.Construction)) {
      throw new InvalidPhaseException();
    }

    // Questi controlli sono necessari, ma per la simulazione 1vs1 per ora possiamo rivederli in
    // seguito
    if (!game.getCurrentPlayer().equals(evt.getSource())) {
      // throw new InvalidTurnException();
    }

    game.undoLastMove();

    // Necessario metodo di controllo per eventuali annullamenti senza la presenza di mosse nel
    // turno
    // Suppongo che alla fine del turno lo stack di annullamento mosse venga svuotato/pulito (vedere
    // anche la regola dei 5 secondi)

  }

  // CLI: we should find something to press that will make the "skip button" function, to trigger
  // the next turn (like pressing space)
  public void update(NextPlayerEvent evt) throws InvalidPhaseException, WrongPlayerException {

    if (!game.getCurrentPhase().equals(Phase.End)) {
      throw new InvalidPhaseException();
    }

    if (!game.getCurrentPlayer().equals(evt.getPlayerSource())) {
      throw new WrongPlayerException();
    }

    game.nextPlayer();
  }

  // Gestisce lo skip dell'utente tra una fase e l'altra
  /*public void update(NextPhaseEvent evt) {

    if (!game.getCurrentPlayer().equals(evt.getSource())) {
      // throw new InvalidTurnException();
    }

    game.nextPhase();
  }*/

  // CLI: Coordinates input or keyboard arrows
  public void update(MovementEvent evt) throws InvalidPhaseException, WrongPlayerException {

    if (!game.getCurrentPhase().equals(Phase.Movement)) {
      throw new InvalidPhaseException();
    }

    if (!game.getCurrentPlayer().equals(evt.getPlayerSource())) {
      throw new WrongPlayerException();
    }

    // Il punto start è il punto dove risiede il Worker che voglio spostare
    // Questo punto mi viene passato dal Client tramite un evento workerEvent
    // Devo salvare questo currentWorkerPosition da qualche parte su Game
    Point start = null;
    try {
      start = game.getBoard().getItemPosition(game.getCurrentPlayer().getCurrentWorker());
    } catch (ItemNotFoundException ignored) {

    }
    // Qui non manca il controllo che il punto dato sia parte dei ReachablePoints?
    // Ed un controllo per capire se il movimento che tenta di fare è una salita o discesa di + di
    // un livello?
    Movement movement = new Movement(game.getBoard(), start, evt.getPoint());
    game.performMove(movement);

    // Passa alla fase di costruzione
    game.nextPhase();
  }

  public static void main(String[] args) {}
}
