package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.board.items.Sex;

// evento di selezione del worker
public class WorkerSelectionEvent extends GameEvent {

  private Sex sex;

  public WorkerSelectionEvent(Object source, Player player, Sex sex) {
    super(source, player);
    this.sex = sex;
  }

  public Sex getSex() {
    return sex;
  }
}
