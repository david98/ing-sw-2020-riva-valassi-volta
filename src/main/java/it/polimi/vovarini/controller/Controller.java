package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.BuildEvent;
import it.polimi.vovarini.controller.events.WorkerEvent;
import it.polimi.vovarini.model.*;

import java.util.*;

public class Controller implements EventListener {
  private Game game;

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    this.game = game;
  }

  public void update(WorkerEvent evt) throws InvalidPhaseException {
    Phase currentPhase = game.getCurrentPhase();

    if (!currentPhase.equals(Phase.Movement)) throw new InvalidPhaseException();
    game.getCurrentPlayer().setCurrentSex(evt.getSex());
  }

  public void update(BuildEvent evt) {
    Point input = new Point(evt.getBuildEnd());
  }

  public static void main(String[] args) {}
}
