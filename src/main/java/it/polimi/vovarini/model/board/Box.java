package it.polimi.vovarini.model.board;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.EmptyStackException;
import java.util.Stack;

public class Box implements Cloneable{

  public static final int MAX_ITEMS = 4;

  private Stack<Item> items;

  public Box() {
    items = new Stack<>();
  }

  // prima permetteva di sovrascrivere un worker di un altro giocatore. Magari le carte porteranno a
  // nuove modifiche
  public void place(Item item) throws BoxFullException {
    if (items.size() >= MAX_ITEMS) {
      throw new BoxFullException();
    }

    // if ( !(items.empty()) && )

    items.push(item);
  }

  @SuppressWarnings(value = "unchecked")
  public Stack<Item> getItems() throws BoxEmptyException {
    if (items.isEmpty()) {
      throw new BoxEmptyException();
    }
    return (Stack<Item>) items.clone();
  }

  public Item removeTopmost() throws BoxEmptyException {
    try {
      return items.pop();
    } catch (EmptyStackException e) {
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
    if (items.size() == 0) {
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
      rep.append(item.toString() + " - ");
    }
    return rep.toString();
  }


  public Box clone(){
    Box box;
    try{
      box = (Box) super.clone();
      box.items = getItems();
      return box;
    } catch (CloneNotSupportedException e){
      throw new RuntimeException(e);
    } catch (BoxEmptyException e){
      box = new Box();
      return box;
    }
  }
}
