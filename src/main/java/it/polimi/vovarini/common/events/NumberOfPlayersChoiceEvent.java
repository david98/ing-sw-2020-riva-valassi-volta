package it.polimi.vovarini.common.events;

public class NumberOfPlayersChoiceEvent extends GameEvent {
  private int numberOfPlayers;

  public NumberOfPlayersChoiceEvent(Object source, int numberOfPlayers) {
    super(source);
    this.numberOfPlayers = numberOfPlayers;
  }

  public int getNumberOfPlayers() {
    return numberOfPlayers;
  }
}
