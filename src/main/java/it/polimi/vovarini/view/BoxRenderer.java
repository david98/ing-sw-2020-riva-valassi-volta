package it.polimi.vovarini.view;

import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.items.Item;

import java.util.Stack;

public class BoxRenderer {

  public static BoxRenderer instance = null;

  public static BoxRenderer getInstance() {
    if (instance == null) {
      instance = new BoxRenderer();
    }
    return instance;
  }

  // renders the content of Box
  public String render(Box box) {
    try {
      Stack<Item> items = box.getItems();
      ItemRenderer itemRenderer = ItemRenderer.getInstance();
      PlayerRenderer playerRenderer = PlayerRenderer.getInstance();
      if (items.peek().canBeRemoved()) {
        Item topMostItem = items.pop();
        if (items.empty()) {
          return " " + itemRenderer.render(topMostItem, playerRenderer);
        } else {
          return itemRenderer.render(items.pop(), playerRenderer)
              + itemRenderer.render(topMostItem, playerRenderer);
        }
      }
    } catch (BoxEmptyException ignored) {
    }
    return "  ";
  }
}
