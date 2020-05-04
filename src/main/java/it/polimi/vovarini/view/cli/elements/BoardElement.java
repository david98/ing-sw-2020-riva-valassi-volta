package it.polimi.vovarini.view.cli.elements;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.view.cli.Color;
import it.polimi.vovarini.view.cli.Direction;
import it.polimi.vovarini.view.cli.Utils;

import java.util.*;

public class BoardElement extends CLIElement {

  private Board board;
  private final Set<Player> players;
  private final Map<Player, Color> playersColors;

  private final Color markedColor;
  private final ArrayList<Point> markedPoints;

  private Point cursorLocation;

  private final Map<Item, Player> ownerMap;

  public BoardElement(Board board, Set<Player> players,
                      Map<Player, Color> playersColors,
                      Color markedColor) {
    this.board = board;
    this.players = players;
    this.playersColors = playersColors;
    this.markedColor = markedColor;
    cursorLocation = new Point(0, 0);
    markedPoints = new ArrayList<>();
    ownerMap = new HashMap<>();
  }

  public String render() {
    StringBuilder boardRep = new StringBuilder();
    for (int y = 0; y < board.getSize(); y++) {
      boardRep.append("----".repeat(Math.max(0, board.getSize())));
      boardRep.append("\n");
      for (int x = 0; x < board.getSize(); x++) {
        Point cur = new Point(x, y);
        boolean marked = markedPoints.contains(cur);

        boardRep.append(marked ? markedColor.fgWrap("|") : "|");
        Box box = board.getBox(cur);
        boardRep.append(
                marked ?
                        markedColor.fgWrap(renderBox(box, cur.equals(cursorLocation))) :
                        renderBox(box, cur.equals(cursorLocation))
        );
        boardRep.append(marked ? markedColor.fgWrap("|") :"|");
      }
      boardRep.append("\n");
    }
    boardRep.append("----".repeat(Math.max(0, board.getSize())));
    boardRep.append("\n");
    return boardRep.toString();
  }

  public void moveCursor(Direction direction){
    int newX = cursorLocation.getX();
    int newY = cursorLocation.getY();
    switch (direction) {
      case Up: {
        newY = Utils.clamp(cursorLocation.getY() - 1, 0, Board.DEFAULT_SIZE);
        break;
      }
      case Down: {
        newY = Utils.clamp(cursorLocation.getY() + 1, 0, Board.DEFAULT_SIZE);
        break;
      }
      case Left: {
        newX = Utils.clamp(cursorLocation.getX() - 1, 0, Board.DEFAULT_SIZE);
        break;
      }
      case Right: {
        newX = Utils.clamp(cursorLocation.getX() + 1, 0, Board.DEFAULT_SIZE);
      }
      default: {
        break;
      }
    }
    if (cursorLocation.getX() != newX || cursorLocation.getY() != newY) {
      cursorLocation = new Point(newX, newY);
    }
  }

  private String renderBox(Box box, boolean hasCursor){
    try {
      Stack<Item> items = box.getItems();

      if (items.peek().canBeRemoved()) {
        Item topMostItem = items.pop();
        if (items.empty()) {
          return " " + (hasCursor ? "▮" : renderItem(topMostItem));
        } else {
          return renderItem(items.pop())
                  + (hasCursor ? "▮" : renderItem(topMostItem));
        }
      } else if (!items.empty()){
        return renderItem(items.pop()) + (hasCursor ? "▮" : " ");
      }
    } catch (BoxEmptyException ignored) {
    }
    return " " + (hasCursor ? "▮" : " ");
  }

  private String renderItem(Item item) {
    if (!item.canBeRemoved()) {
      return item.toString();
    } else {
      Player owner = ownerMap.get(item);
      if (owner == null) {
        owner = findOwner(item);
      }
      return playersColors.get(owner).fgWrap(item.toString());
    }
  }

  private Player findOwner(Item item) {
    for (Player player : players) {
      if (player.getWorkers().containsValue(item)) {
        ownerMap.put(item, player);
        return player;
      }
    }
    return null;
  }

  public Point getCursorLocation() {
    return new Point(cursorLocation);
  }

  public void markPoints(Collection<Point> points){
    markedPoints.addAll(points);
  }

  public void resetMarkedPoints(){
    markedPoints.clear();
  }

  public ArrayList<Point> getMarkedPoints() {
    return new ArrayList<>(markedPoints);
  }

  public void setBoard(Board board) {
    this.board = board;
  }
}
