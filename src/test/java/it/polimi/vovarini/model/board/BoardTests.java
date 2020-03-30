package it.polimi.vovarini.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTests {
  @Test
  @DisplayName("Test that a Board can be instantiated correctly")
  void boardCreation() {
    Board board = new Board(Board.DEFAULT_SIZE);
    assertEquals(Board.DEFAULT_SIZE, board.getSize());
  }
}
