package it.polimi.vovarini.common.events;

import it.polimi.vovarini.view.GameView;

public class InvalidNicknameEvent extends GameEvent{

  private GameView target;

  public InvalidNicknameEvent(Object source, GameView target){
    super(source, null);
    this.target = target;
    // TODO: view should be cloned, can't pass it to other players
  }

  public GameView getTarget() {
    return target;
  }
}
