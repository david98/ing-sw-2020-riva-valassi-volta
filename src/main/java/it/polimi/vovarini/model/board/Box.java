package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.model.board.items.Item;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class Box implements Serializable {

  public static final int MAX_ITEMS = 4;

  private final Deque<Item> items;

  public Box() {
    items = new ArrayDeque<>();
  }

  public Box(Box b) {
    items = new ArrayDeque<>(b.items);
  }

  public void place(Item item) throws BoxFullException {
    if (items.size() >= MAX_ITEMS) {
      throw new BoxFullException();
    }
    items.push(item);
  }

  public Deque<Item> getItems() {
    return new ArrayDeque<>(items);
  }

  public Item removeTopmost() {
    try {
      return items.pop();
    } catch (NoSuchElementException e) {
      return null;
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
