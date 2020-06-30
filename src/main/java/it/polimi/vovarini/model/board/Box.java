package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * This class represents a single box, or cell, of the {@link it.polimi.vovarini.model.board.Board}
 */
public class Box implements Serializable {

  public static final int MAX_ITEMS = 4;

  private final Deque<Item> items;

  /**
   * Creates an empty box.
   */
  public Box() {
    items = new ArrayDeque<>();
  }

  /**
   * Creates a box which is a clone of b.
   *
   * @param b The box to be cloned.
   */
  public Box(Box b) {
    items = new ArrayDeque<>(b.items);
  }

  /**
   * Places item on top of this box, if possible.
   *
   * @param item The item to be placed.
   * @throws BoxFullException If this box is full.
   */
  public void place(Item item) throws BoxFullException {
    if (items.size() >= MAX_ITEMS) {
      throw new BoxFullException();
    }
    items.push(item);
  }

  public Deque<Item> getItems() {
    return new ArrayDeque<>(items);
  }

  /**
   * Removes and returns the item on top of this box.
   *
   * @return The removed item, or null if the box was empty.
   */
  public Item removeTopmost() {
    try {
      return items.pop();
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  /**
   * Computes the level of this box, ignoring workers.
   *
   * @return The level of this box.
   */
  public int getLevel() {
    /* Here we assume that if the Block below a Worker is, say,
     * a level 3 block, then below it you have a level 2 block
     * and a level 1 block. Blocks must be stacked according to
     * their level. Also, with Atlas you can build level 4 blocks
     * anywhere, but no Worker can stand on top of a level 4 Block
     * so this assumption is still valid.
     */
    if (items.isEmpty()) {
      return 0;
    } else if (items.peek().canBeRemoved()) {
      return items.size() - 1;
    } else if (new Worker(Sex.Male, null).canBePlacedOn(items.peek())) { //trick
      return items.size();
    } else {
      return Block.MAX_LEVEL;
    }
  }

  public String toString() {
    StringBuilder rep = new StringBuilder();
    for (Item item : items) {
      rep.append(item.toString()).append(" - ");
    }
    return rep.toString();
  }
}
