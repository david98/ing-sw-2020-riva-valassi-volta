package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.model.board.items.Item;

import java.io.Serializable;
import java.util.*;

public class Box implements Serializable {

  public static final int MAX_ITEMS = 4;

  private final Deque<Item> items;

  public Box() {
    items = new ArrayDeque<>();
  }

  public void place(Item item) throws BoxFullException {
    if (items.size() >= MAX_ITEMS) {
      throw new BoxFullException();
    }
    items.push(item);
  }

  @SuppressWarnings(value = "unchecked")
  public Deque<Item> getItems() throws BoxEmptyException {
    if (items.isEmpty()) {
      throw new BoxEmptyException();
    }
    return new ArrayDeque<>(items);
  }

  public Item removeTopmost() throws BoxEmptyException {
    try {
      return items.pop();
    } catch (NoSuchElementException e) {
      throw new BoxEmptyException();
    }
  }

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
    } else {
      return items.size();
    }
  }

  public String toString(){
    StringBuilder rep = new StringBuilder();
    for (Item item: items){
      rep.append(item.toString()).append(" - ");
    }
    return rep.toString();
  }
}
