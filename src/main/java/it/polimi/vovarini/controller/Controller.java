package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.MovementEvent;
import it.polimi.vovarini.controller.events.NextPlayerEvent;
import it.polimi.vovarini.controller.events.RegistrationEvent;
import it.polimi.vovarini.controller.events.UndoEvent;
import it.polimi.vovarini.model.*;

import java.util.*;

public class Controller implements EventListener {
  private final Game game;

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    this.game = game;
  }

  public void update(RegistrationEvent evt)
      throws InvalidPhaseException, NicknameAlreadyInUseException {

    // Non sarebbe meglio avere una Fase Init, precedente a Start, che viene svolta una tantum ad
    // inizio partita?
    if (!game.getCurrentPhase().equals(Phase.Start)) {
      throw new InvalidPhaseException();
    }

    if (evt.getNickname() == null) {
      // throw new NicknameNullException();
    }

    /*if(game.isNicknameAvailable(evt.getNickname())) {
      game.addPlayer(evt.getNickname());
    } else {
      throw new NicknameAlreadyInUseException();
    }*/

    // Questo pezzo di codice andrà dentro addPlayer(String nickname) nel Model
    for (Player player : game.getPlayers()) {
      if (player.getNickname().equalsIgnoreCase(evt.getNickname())) {
        throw new NicknameAlreadyInUseException();
      }
    }
  }

  public void update(UndoEvent evt) throws InvalidPhaseException {

    if (!game.getCurrentPhase().equals(Phase.Movement)
        || !game.getCurrentPhase().equals(Phase.Construction)) {
      throw new InvalidPhaseException();
    }

    if (!game.getCurrentPlayer().equals(evt.getSource())) {
      // throw new InvalidTurnException();
    }

    game.undoLastMove();

    // Manca da gestire il fatto che il Client potrebbe provare ad annullare una mossa che non ha
    // ancora fatto,
    // ovvero, il Client si trova nella fase di Movement, ma non ha ancora mosso il proprio Worker,
    // però prova ad annullare la mossa (stesso ragionamento nella fase di Construction)
  }

  public void update(NextPlayerEvent evt) throws InvalidPhaseException {

    if (!game.getCurrentPhase().equals(Phase.End)) {
      throw new InvalidPhaseException();
    }

    if (!game.getCurrentPlayer().equals(evt.getSource())) {
      // throw new InvalidTurnException();
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

  public void update(MovementEvent evt) throws InvalidPhaseException {

    if (!game.getCurrentPhase().equals(Phase.Movement)) {
      throw new InvalidPhaseException();
    }

    if (!game.getCurrentPlayer().equals(evt.getSource())) {
      // throw new InvalidTurnException();
    }

    // Il punto start è il punto dove risiede il Worker che voglio spostare
    // Questo punto mi viene passato dal Client tramite un evento workerEvent
    // Devo salvare questo currentWorkerPosition da qualche parte su Game
    Point start = null;
    Movement movement = new Movement(game.getBoard(), start, evt.getPoint());

    // Se la mossa non è consentita execute() deve notificare il Client
    // In alternativa, execute() potrà restituire al Controller un boolean che indica l'esito
    // dell'operazione
    // e il Controller provvederà ad inviare al Client un messaggio d'errore (in questo caso
    // servirebbe la RemoteView)
    movement.execute();

    // Passa alla fase di checkWin e controlla se il currentPlayer ha vinto
    game.nextPhase();
    game.getCurrentPlayer().getGodCard().checkWin(movement);

    // Passa alla fase di costruzione
    game.nextPhase();
  }

  public static void main(String[] args) {}
}
