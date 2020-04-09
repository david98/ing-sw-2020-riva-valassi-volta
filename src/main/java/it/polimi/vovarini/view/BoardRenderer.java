package it.polimi.vovarini.view;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Block;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

public class BoardRenderer {

  private Point cursorLocation;

  public BoardRenderer() {
    cursorLocation = new Point(0, 0);
  }

  public String render(Board board) {
    StringBuilder boardRep = new StringBuilder();
    for (int y = 0; y < board.getSize(); y++) {
      for (int x = 0; x < board.getSize(); x++) {
        boardRep.append("----");
      }
      boardRep.append("\n");
      for (int x = 0; x < board.getSize(); x++) {
        boardRep.append("|");
        Point cur = new Point(x, y); //something's wrong with coordinates
        Box box = board.getBox(cur);
        boardRep.append(BoxRenderer.getInstance().render(box, cur.equals(cursorLocation)));
        boardRep.append("|");
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

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public static void main(String[] args) {
    Board board = new Board(Board.DEFAULT_SIZE);
    Player[] players = {new Player("davide"), new Player("lezzo")};

    try {
      board.place(Block.blocks[0], new Point(0, 0));
      board.place(players[0].getCurrentWorker(), new Point(0, 0));
      board.place(players[0].getOtherWorker(), new Point(1, 1));
    } catch (Exception ignored) {

    }

    try {
      Terminal terminal = TerminalBuilder.builder()
              .jna(true)
              .system(true)
              .build();

      terminal.enterRawMode();
      Reader reader = terminal .reader();

      BoardRenderer boardRenderer = new BoardRenderer();
      PlayerRenderer playerRenderer = PlayerRenderer.getInstance();
      playerRenderer.setPlayers(players);

      clearScreen();

      System.out.println(boardRenderer.render(board));
      // "game" loop
      while (true){
        int input = reader.read();

        switch (input){
          case 97: { //A
            boardRenderer.moveCursor(Direction.Left);
            break;
          }
          case 100: { //D
            boardRenderer.moveCursor(Direction.Right);
            break;
          }
          case 119: { //W
            boardRenderer.moveCursor(Direction.Up);
            break;
          }
          case 115: { //S
            boardRenderer.moveCursor(Direction.Down);
            break;
          }
          default: {
            break;
          }
        }
        for (int i = 0; i < board.getSize() * 2 + 2; i++) {
          System.out.print("\033[F\r");
        }
        System.out.println(boardRenderer.render(board));
      }

    } catch (IOException ignored){
    }
  }
}
