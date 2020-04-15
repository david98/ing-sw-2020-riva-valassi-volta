package it.polimi.vovarini.common.events;

/**
 * Represents the start of the game (all players have chosen
 * both a nickname and a {@link it.polimi.vovarini.model.godcards.GodCard}
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class GameStartEvent extends GameEvent{

  public GameStartEvent(Object source){
    super(source);
  }
}
