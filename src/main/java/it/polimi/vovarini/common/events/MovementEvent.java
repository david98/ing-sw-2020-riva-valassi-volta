package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

public class MovementEvent extends GameEvent {

  private final Point end;

  public MovementEvent(Object source, Point end) {
    super(source);
    this.end = end;
  }

  public Point getPoint() {
    return end;
  }
}
