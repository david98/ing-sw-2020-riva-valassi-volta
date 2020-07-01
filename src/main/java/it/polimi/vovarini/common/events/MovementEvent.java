package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Point;

/**
 * Represents a {@link it.polimi.vovarini.model.Player}'s intention to
 * move their current {@link it.polimi.vovarini.model.board.items.Worker}
 * to {@code end}.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class MovementEvent extends GameEvent {

  private final Point end;

  /**
   * @param source The event source, it should be a {@link it.polimi.vovarini.model.Player} object.
   * @param end    Where the current {@link it.polimi.vovarini.model.board.items.Worker} should be moved to.
   */
  public MovementEvent(Object source, Point end) {
    super(source);
    this.end = end;
  }

  public Point getPoint() {
    return end;
  }
}
