package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.board.items.Sex;

public class GameEventManagerTest {

  public GameEventManagerTest(){
    GameEventManager.bindListeners(this);
  }

  @GameEventListener(eventClass = WorkerSelectionEvent.class)
  public void prova(WorkerSelectionEvent e){
    System.out.println(e.getSex());
  }

  public static void main(String[] args){
    GameEventManagerTest test = new GameEventManagerTest();
    GameEventManager.raise(new WorkerSelectionEvent(test, null, Sex.Male));
  }
}
