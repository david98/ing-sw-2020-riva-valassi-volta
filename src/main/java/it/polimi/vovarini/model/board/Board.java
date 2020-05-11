package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.events.BoardUpdateEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Item;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Board implements Cloneable, Serializable {

  public static final int DEFAULT_SIZE = 5;

  private Box[][] boxes;
  private int size;

  /*
   * Si presuppone che la plancia sia quadrata
   * */
  public Board(int size) {
    this.size = size;
    boxes = new Box[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        boxes[i][j] = new Box();
      }
    }
  }

  public boolean isPositionValid(Point p) {
    return (p.getX() >= 0 && p.getY() >= 0 && p.getX() < size && p.getY() < size);
  }

  public List<Point> getAdjacentPositions(Point p) {
    LinkedList<Point> adjacentPositions = new LinkedList<>();
    for (int i = p.getY() - 1; i <= p.getY() + 1; i++) {
      for (int j = p.getX() - 1; j <= p.getX() + 1; j++) {
        Point point = new Point(j, i);
        if (i >= 0 && i < size && j >= 0 && j < size && !point.equals(p)) {
          adjacentPositions.add(point);
        }
      }
    }
    return adjacentPositions;
  }

  public Box getBox(Point position) {
    return boxes[position.getY()][position.getX()];
  }

  public void place(Item item, Point p) throws InvalidPositionException, BoxFullException {
    if (!isPositionValid(p)) {
      throw new InvalidPositionException();
    }
    Box box = getBox(p);
    box.place(item);
    GameEventManager.raise(new BoardUpdateEvent(this, this.clone()));
  }

  public Stack<Item> getItems(Point p) throws InvalidPositionException, BoxEmptyException {
    if (!isPositionValid(p)) {
      throw new InvalidPositionException();
    }
    return getBox(p).getItems();
  }

  public Stack<Item> safeGetItems(Point p){
    try {
      return getItems(p);
    } catch (InvalidPositionException | BoxEmptyException e){
      return new Stack<Item>();
    }
  }

  public Item remove(Point p) throws InvalidPositionException, BoxEmptyException {
    if (!isPositionValid(p)) {
      throw new InvalidPositionException();
    }
    Box box = getBox(p);
    return box.removeTopmost();
  }

  public Point getItemPosition(Item item) throws ItemNotFoundException {
    for (int i = 0; i < boxes.length; i++) {
      for (int j = 0; j < boxes.length; j++) {
        try {
          if (boxes[j][i].getItems().peek().equals(item)) {
            return new Point(i, j);
          }
        } catch (BoxEmptyException ignored) {
        }
      }
    }
    throw new ItemNotFoundException();
  }

  public int getSize() {
    return size;
  }

  public Board clone() {
    try {
      Board b = (Board) super.clone();
      b.boxes = new Box[size][size];
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          b.boxes[i][j] = boxes[i][j].clone();
        }
      }
      return b;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

}
