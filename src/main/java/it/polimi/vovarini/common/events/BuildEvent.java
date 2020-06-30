package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Point;

/**
 * Represents the player's intention to build a block.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class BuildEvent extends GameEvent {

  private final Point buildEnd;
  private final int level;

  /**
   * @param source The event source (ideally, a {@link it.polimi.vovarini.model.Player} object).
   * @param point  Where the block is to be built.
   * @param level  What level the new block is.
   */
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
