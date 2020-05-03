package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.*;

public abstract class View {
  protected ViewData data;

  public View(){
    GameEventManager.bindListeners(this);
    System.out.println("bind");
    data = new ViewData();
  }

  public abstract void handleBoardUpdate(BoardUpdateEvent e);

  public abstract void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e);

  public abstract void handlePhaseUpdate(PhaseUpdateEvent e);

  public abstract void handleGameStart(GameStartEvent e);

  public abstract void handleNewPlayer(NewPlayerEvent e);
}
