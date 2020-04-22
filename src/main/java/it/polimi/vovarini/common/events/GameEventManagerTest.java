package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.board.items.Sex;

public class GameEventManagerTest {

  public GameEventManagerTest(){
    GameEventManager.bindListeners(this);
  }

  @GameEventListener
  public void prova(WorkerSelectionEvent e, String a){
    System.out.println(e);
  }

  public static void main(String[] args){
    GameEventManagerTest test = new GameEventManagerTest();
    GameEventManager.raise(new WorkerSelectionEvent(test, Sex.Male));
  }
}
