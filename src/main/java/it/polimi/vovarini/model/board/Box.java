package it.polimi.vovarini.model.board;

import it.polimi.vovarini.model.board.items.Item;

import java.util.EmptyStackException;
import java.util.Stack;

public class Box {

  public static final int MAX_ITEMS = 4;

  private Stack<Item> items;

  public Box() {
    items = new Stack<>();
  }

  public void place(Item item) throws BoxFullException {
    if (items.size() >= 4) {
      throw new BoxFullException();
    }

    items.push(item);
  }

  public Item getTopmost() throws BoxEmptyException {
    try {
      return items.peek();
    } catch (EmptyStackException e) {
      throw new BoxEmptyException();
    }
  }

  public Item removeTopmost() throws BoxEmptyException {
    try {
      return items.pop();
    } catch (EmptyStackException e) {
      throw new BoxEmptyException();
    }
  }

  @Override
  public String toString() {
    if (items.size() == 0) {
      return "-";
    } else {
      return items.peek().toString();
    }
  }
}
