package it.polimi.vovarini.model;

public enum Phase {
  Init,
  Start,
  Movement,
  CheckWin,
  Construction,
  End;

  private static Phase[] vals = values();

  public Phase next() {
    return vals[(this.ordinal() + 1) % vals.length];
  }
}
