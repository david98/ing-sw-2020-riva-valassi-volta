package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.board.items.Sex;

// evento di selezione del worker
public class WorkerSelectionEvent extends GameEvent {

  private Sex sex;

  public WorkerSelectionEvent(Object source) {
    super(source);
  }

  public Sex getSex() {
    return sex;
  }
}
