package it.polimi.vovarini.model;

public enum Phase {
  Start,
  Movement,
  CheckWin,
  Construction,
  End,
  Wait;

  private static Phase[] vals = values();
  public Phase next()
  {
    return vals[(this.ordinal()+1) % vals.length];
  }
}
