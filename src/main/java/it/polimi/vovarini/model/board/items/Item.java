package it.polimi.vovarini.model.board.items;

import java.io.Serializable;

public abstract class Item implements Serializable {

  public boolean canBePlacedOn(Item item) {
    return item == null;
  }

  public boolean canBeRemoved() {
    return false;
  }

}
