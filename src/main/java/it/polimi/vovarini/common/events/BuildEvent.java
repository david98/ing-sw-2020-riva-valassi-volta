package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Point;

/**
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
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
