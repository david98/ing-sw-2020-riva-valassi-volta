package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

public class MovementEvent extends GameEvent {

  private final Point end;

  public MovementEvent(Object source, Player player, Point end) {
    super(source, player);
    this.end = end;
  }

  public Point getPoint() {
    return end;
  }
}
