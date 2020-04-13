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
    GameEventManager em = GameEventManager.getInstance();
    GameEventManagerTest test = new GameEventManagerTest();
    em.raise(new WorkerSelectionEvent(test, null, Sex.Male));
  }
}
