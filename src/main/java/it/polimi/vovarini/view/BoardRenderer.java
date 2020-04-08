package it.polimi.vovarini.view;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Block;

public class BoardRenderer {

  private Point cursorLocation;

  public BoardRenderer() {}

  public String render(Board board) {
    StringBuilder boardRep = new StringBuilder();
    for (int x = 0; x < board.getSize(); x++) {
      for (int y = 0; y < board.getSize(); y++) {
        boardRep.append("----");
      }
      boardRep.append("\n");
      for (int y = 0; y < board.getSize(); y++) {
        boardRep.append("|");
        Box box = board.getBox(new Point(x, y));
        boardRep.append(BoxRenderer.getInstance().render(box));
        boardRep.append("|");
      }
      boardRep.append("\n");
    }
    for (int y = 0; y < board.getSize(); y++) {
      boardRep.append("----");
    }
    boardRep.append("\n");
    return boardRep.toString();
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

    BoardRenderer boardRenderer = new BoardRenderer();
    PlayerRenderer playerRenderer = PlayerRenderer.getInstance();
    playerRenderer.setPlayers(players);

    System.out.println(boardRenderer.render(board));
    for (int i = 0; i < board.getSize() * 2 + 2; i++) {
      System.out.print("\033[F\r");
    }
    System.out.println(boardRenderer.render(board));
  }
}
