package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Point;

public class MovementEvent extends GameEvent {

  private final Point end;

  public MovementEvent(Object source) {
    super(source);
    this.end = null;
  }

  public MovementEvent(Object source, Point end) {
    super(source);
    this.end = end;
  }

  public Point getPoint() {
    return end;
  }
}
