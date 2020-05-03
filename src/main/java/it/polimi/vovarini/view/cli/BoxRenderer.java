package it.polimi.vovarini.view.cli;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Item;

import java.util.Stack;

/**
 * A singleton that can create a {@link String} representation
 * of a given {@link Box} ready to be printed.
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class BoxRenderer {

  public static BoxRenderer instance = null;

  public static BoxRenderer getInstance() {
    if (instance == null) {
      instance = new BoxRenderer();
    }
    return instance;
  }

  /**
   * Returns a string representation of the content of a {@link Box} object.
   *
   * @param box The box to be rendered.
   * @param hasCursor Whether the box should be rendered with a cursor placed upon it.
   * @return A string representation of the content of {@code box}.
   */
  public String render(Box box, boolean hasCursor) {
    try {
      Stack<Item> items = box.getItems();
      ItemRenderer itemRenderer = ItemRenderer.getInstance();
      PlayerRenderer playerRenderer = PlayerRenderer.getInstance();

      StringBuilder content = new StringBuilder();

      if (items.peek().canBeRemoved()) {
        Item topMostItem = items.pop();
        if (items.empty()) {
          return " " + (hasCursor ? "▮" : itemRenderer.render(topMostItem, playerRenderer));
        } else {
          return itemRenderer.render(items.pop(), playerRenderer)
              + (hasCursor ? "▮" : itemRenderer.render(topMostItem, playerRenderer));
        }
      } else if (!items.empty()){
        return itemRenderer.render(items.pop(), playerRenderer) + (hasCursor ? "▮" : " ");
      }
    } catch (BoxEmptyException ignored) {
    }
    return " " + (hasCursor ? "▮" : " ");
  }

  /**
   * Returns a string representation of the content of a {@link Box} object.
   *
   * @param box The box to be rendered.
   * @return A string representation of the content of {@code box}.
   */
  public String render(Box box) {
    return render(box, false);
  }
}
