package it.polimi.vovarini.common.events;

public class NumberOfPlayersChoiceEvent extends GameEvent {
  private int numberOfPlayers;

  /**
   * Builds a NumberOfPlayersChoiceEvent
   * @param source  is the source object of the event
   * @param numberOfPlayers is the number of players chosen to play (2 or 3)
   */
  public NumberOfPlayersChoiceEvent(Object source, int numberOfPlayers) {
    super(source);
    this.numberOfPlayers = numberOfPlayers;
  }

  public int getNumberOfPlayers() {
    return numberOfPlayers;
  }
}
