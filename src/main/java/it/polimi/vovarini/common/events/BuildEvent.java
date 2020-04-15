package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

// si è deciso di separare MovementEvent e BuildEvent perchè, pur strutturalmente equivalenti, sono
// molto diversi a livello semantico
public class BuildEvent extends GameEvent {

  private final Point buildEnd;
  private final int level;

  public BuildEvent(Object source, Point point, int level) {
    super(source);
    buildEnd = point;
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  public Point getBuildEnd() {
    return buildEnd;
  }
}
