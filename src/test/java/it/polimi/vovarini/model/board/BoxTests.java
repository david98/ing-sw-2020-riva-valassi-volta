package it.polimi.vovarini.model.board;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidLevelException;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.board.items.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class BoxTests {

  private static LinkedList<Block> allBlocks;

  @BeforeAll
  private static void init() {
    allBlocks = new LinkedList<>();
    for (int i = Block.MIN_LEVEL; i <= Block.MAX_LEVEL; i++) {
      try {
        allBlocks.add(new Block(i));
      } catch (InvalidLevelException ignored) {

      }
    }
  }

  @Test
  @DisplayName("Test that a Box can be instantiated correctly")
  void boxCreation() {
    Box box = new Box();
    assertTrue(box.getItems().isEmpty());

    assertNull(box.removeTopmost());
  }

  @Test
  @DisplayName("Test that place works")
  void place() {
    Box box = new Box();
    assertDoesNotThrow(() -> box.place(allBlocks.getFirst()));
    assertDoesNotThrow(() -> assertEquals(box.getItems().peek(), allBlocks.getFirst()));
  }

  @Test
  @DisplayName("Test that a BoxFullException is thrown when Box already contains MAX_ITEMS items")
  void boxMaximumCapacity() {
    Box box = new Box();
    for (int i = 0; i < Box.MAX_ITEMS; i++) {
      try {
        box.place(allBlocks.get(i));
      } catch (BoxFullException ignored) {

      }
    }
    Player testPlayer = new Player("test_player");
    assertThrows(BoxFullException.class, () -> box.place(new Worker(Sex.Male, testPlayer)));
  }

  @Test
  @DisplayName("Test that getItems returns a clone of the Stack")
  void getItemsReturnsClone() {
    Box box = new Box();
    for (int i = 0; i < Box.MAX_ITEMS; i++) {
      try {
        box.place(allBlocks.get(i));
      } catch (BoxFullException ignored) {

      }
    }
    var items = box.getItems();
    items.clear();
    items = box.getItems();
    assertFalse(items.isEmpty());
  }

  @Test
  @DisplayName("Test that toString works as expected")
  void toStringTest() {
    Box aBox = new Box();

    Block block = new Block(Block.MIN_LEVEL);
    aBox.place(block);
    Player testPlayer = new Player("test_player");
    Worker worker = new Worker(Sex.Female, testPlayer);
    aBox.place(worker);

    assertEquals("F - 1 - ", aBox.toString());
  }
}
