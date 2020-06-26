package it.polimi.vovarini.model.board.items;

import java.io.Serializable;

/**
 * This class should represents any physical object placeable on the board
 */
public abstract class Item implements Serializable {

  public boolean canBePlacedOn(Item item) {
    return item == null;
  }

  public boolean canBeRemoved() {
    return false;
  }

}
