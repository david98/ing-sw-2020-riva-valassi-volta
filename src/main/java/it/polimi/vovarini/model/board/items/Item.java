package it.polimi.vovarini.model.board.items;

import java.io.Serializable;

/**
 * This class represents an object which can be placed on a {@link it.polimi.vovarini.model.board.Board}.
 */
public abstract class Item implements Serializable {

  /**
   * @param item The item which this object would be placed on.
   * @return Whether this object can be placed on top of item.
   */
  public boolean canBePlacedOn(Item item) {
    return item == null;
  }

  /**
   * @return Whether this item can be removed once it has been placed.
   */
  public boolean canBeRemoved() {
    return false;
  }

}
