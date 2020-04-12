package it.polimi.vovarini.view;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;

import java.util.ArrayList;
import java.util.Collection;

public class BoardRenderer {

  private Color markedColor = Color.Green;

  private Point cursorLocation;

  private ArrayList<Point> markedPoints;

  public BoardRenderer() {
    cursorLocation = new Point(0, 0);
    markedPoints = new ArrayList<>();
  }

  public String render(Board board) {
    StringBuilder boardRep = new StringBuilder();
    for (int y = 0; y < board.getSize(); y++) {
      for (int x = 0; x < board.getSize(); x++) {
        boardRep.append("----");
      }
      boardRep.append("\n");
      for (int x = 0; x < board.getSize(); x++) {
        Point cur = new Point(x, y);
        boolean marked = markedPoints.contains(cur);

        boardRep.append(marked ? markedColor.wrap("|") : "|");
        Box box = board.getBox(cur);
        boardRep.append(
                marked ?
                        markedColor.wrap(BoxRenderer.getInstance().render(box, cur.equals(cursorLocation))) :
                        BoxRenderer.getInstance().render(box, cur.equals(cursorLocation))
        );
        boardRep.append(marked ? markedColor.wrap("|") : "|");
      }
      boardRep.append("\n");
    }
    for (int x = 0; x < board.getSize(); x++) {
      boardRep.append("----");
    }
    boardRep.append("\n");
    return boardRep.toString();
  }

  public static int clamp(int n, int minInclusive, int maxExclusive){
    return Math.max(minInclusive, Math.min(n, maxExclusive - 1));
  }

  public void moveCursor(Direction direction){
    int newX = cursorLocation.getX();
    int newY = cursorLocation.getY();
    switch (direction) {
      case Up: {
        newY = clamp(cursorLocation.getY() - 1, 0, Board.DEFAULT_SIZE);
        break;
      }
      case Down: {
        newY = clamp(cursorLocation.getY() + 1, 0, Board.DEFAULT_SIZE);
        break;
      }
      case Left: {
        newX = clamp(cursorLocation.getX() - 1, 0, Board.DEFAULT_SIZE);
        break;
      }
      case Right: {
        newX = clamp(cursorLocation.getX() + 1, 0, Board.DEFAULT_SIZE);
      }
      default: {
        break;
      }
    }
    if (cursorLocation.getX() != newX || cursorLocation.getY() != newY) {
      cursorLocation = new Point(newX, newY);
    }
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
}
