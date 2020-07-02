package it.polimi.vovarini.model;

/**
 * Enumeration containing all the possible Phases playable in a Game
 */
public enum Phase {
  Start,
  Movement,
  Construction,
  End;

  private static Phase[] vals = values();

  /**
   * This method returns the next value in subsequential order
   * @return the next Phase value in subsequential order
   */
  public Phase next() {
    return vals[(this.ordinal() + 1) % vals.length];
  }
}
