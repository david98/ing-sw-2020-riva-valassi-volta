package it.polimi.vovarini.model.board;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Item;

import java.util.LinkedList;
import java.util.List;

public class Board {

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

  public List<Point> getAdjacentPositions(Point p) {
    LinkedList<Point> adjacentPositions = new LinkedList<>();
    System.out.println("Point under exam: " + p + "\nAdjacent points:\n");
    for (int i = p.getY() - 1; i < p.getY() + 1; i++) {
      for (int j = p.getX() - 1; j < p.getX() + 1; j++) {
        Point point = new Point(j, i);
        if (i >= 0 && i < size && j >= 0 && j < size && !point.equals(p)) {
          System.out.println(point);
          adjacentPositions.add(point);
        }
      }
    }
    System.out.println();
    return adjacentPositions;
  }

  public void place(Item item, Point p) throws InvalidPositionException, BoxFullException {
    if (p.getX() >= size || p.getY() >= size) {
      throw new InvalidPositionException();
    }
    Box box = boxes[p.getY()][p.getX()];
    box.place(item);
  }

  public Item getTopmostItem(Point p) throws InvalidPositionException, BoxEmptyException {
    if (p.getX() >= size || p.getY() >= size) {
      throw new InvalidPositionException();
    }
    Box box = boxes[p.getY()][p.getX()];
    return box.getTopmost();
  }

  public Item remove(Point p) throws InvalidPositionException, BoxEmptyException {
    if (p.getX() >= size || p.getY() >= size) {
      throw new InvalidPositionException();
    }
    Box box = boxes[p.getY()][p.getX()];
    return box.removeTopmost();
  }

  public Point getItemPosition(Item item) throws ItemNotFoundException {
    for (int i = 0; i < boxes.length; i++) {
      for (int j = 0; j < boxes.length; j++) {
        try {
          if (boxes[j][i].getTopmost().equals(item)) {
            return new Point(i, j);
          }
        } catch (BoxEmptyException ignored) {
        }
      }
    }
    throw new ItemNotFoundException();
  }

  public void debugPrintToConsole(Player[] players) {
    for (int i = 0; i < boxes.length; i++) {
      System.out.print("|");
      for (int j = 0; j < boxes.length; j++) {
        System.out.print(" " + boxes[i][j].toString(players));
      }
      System.out.println((char) 27 + "[37m |");
    }
    System.out.println((char) 27 + "[37m");
  }

  public int getSize() {
    return size;
  }
}
