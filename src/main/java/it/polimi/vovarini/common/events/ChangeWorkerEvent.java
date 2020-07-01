package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.board.items.Worker;

/**
 * Represents the choice from a player to change his selected {@link it.polimi.vovarini.model.board.items.Worker}
 *
 * @author Mattia Valassi
 * @version 0.1
 */
public class ChangeWorkerEvent extends GameEvent {

  private final Worker workerToSelect;

  /**
   * Builds a ChangeWorkerEvent
   * @param source is the source object of the event
   * @param toSelect is the worker I want to select
   */
  public ChangeWorkerEvent(Object source, Worker toSelect) {
    super(source);
    workerToSelect = toSelect;
  }

  public Worker getWorkerToSelect() {
    return workerToSelect;
  }
}
