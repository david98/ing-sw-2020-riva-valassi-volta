package it.polimi.vovarini.model;

public enum Phase {
  Start,
  Movement,
  Construction,
  End;

  private static Phase[] vals = values();

  public Phase next() {
    return vals[(this.ordinal() + 1) % vals.length];
  }
}
