package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.board.items.Sex;

/**
 * Represents a {@link it.polimi.vovarini.model.Player}'s
 * selection among their {@link it.polimi.vovarini.model.board.items.Worker}s.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class WorkerSelectionEvent extends GameEvent {

  private final Sex sex;

  /**
   * Builds a WorkerSelectionEvent
   * @param source is the source object of the event
   * @param sex is the sex identifying the worker I want to select
   */
  public WorkerSelectionEvent(Object source, Sex sex) {
    super(source);
    this.sex = sex;
  }

  public Sex getSex() {
    return sex;
  }
}
