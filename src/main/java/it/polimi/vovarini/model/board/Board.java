package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.events.BoardUpdateEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Board implements Serializable {

  public static final int DEFAULT_SIZE = 5;

  private final Box[][] boxes;
  private final int size;

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

  public void place(Item item, Point p) {
    if (!isPositionValid(p)) {
      throw new InvalidPositionException();
    }
    Box box = getBox(p);
    box.place(item);
    GameEventManager.raise(new BoardUpdateEvent(this, this));
  }

  public Deque<Item> getItems(Point p) {
    if (!isPositionValid(p)) {
      throw new InvalidPositionException();
    }
    return getBox(p).getItems();
  }

  public Item remove(Point p) {
    if (!isPositionValid(p)) {
      throw new InvalidPositionException();
    }
    Box box = getBox(p);
    return box.removeTopmost();
  }

  public Point getItemPosition(Block block) {
    for (int i = 0; i < boxes.length; i++) {
      for (int j = 0; j < boxes.length; j++) {
        if (Objects.equals(boxes[j][i].getItems().peek(), block))
        {
          return new Point(i, j);
        }
      }
    }
    throw new ItemNotFoundException();
  }

  public Point getItemPosition(Worker worker) {
    for (int i = 0; i < boxes.length; i++) {
      for (int j = 0; j < boxes.length; j++) {
        if (boxes[j][i].getItems().peek() != null && boxes[j][i].getItems().peek().canBeRemoved()) {
          Worker peekedWorker = (Worker) boxes[j][i].getItems().peek();
          if (Objects.equals(peekedWorker, worker)) {
            return new Point(i, j);
          }
        }
      }
    }
    throw new ItemNotFoundException();
  }

  public int getSize() {
    return size;
  }

}
