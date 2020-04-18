package it.polimi.vovarini.model.board;

import it.polimi.vovarini.model.board.items.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Stack;

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
    BoxEmptyException thrown =
        assertThrows(
            BoxEmptyException.class,
            box::getItems,
            "Expected getItems() to throw BoxEmptyException, but it didn't");

    thrown =
        assertThrows(
            BoxEmptyException.class,
            box::removeTopmost,
            "Expected removeTopmost() to throw BoxEmptyException, but it didn't");
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
    assertThrows(BoxFullException.class, () -> box.place(new Worker(Sex.Male)));
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
    try {
      Stack<Item> items = box.getItems();
      items.clear();
      items = box.getItems();
      assertFalse(items.isEmpty());
    } catch (BoxEmptyException ignored) {

    }
  }

  @Test
  @DisplayName("Test that the clone() method on Box works")
  void cloneWorks(){
    Box box = new Box();

    for (int i = 0; i < Box.MAX_ITEMS; i++) {
      try {
        box.place(allBlocks.get(i));
      } catch (BoxFullException ignored) {

      }
    }

    Box box2 = box.clone();
    try {
      box2.removeTopmost();
      box2.removeTopmost();
      assertEquals(box.getItems().size(), Box.MAX_ITEMS);
    } catch (BoxEmptyException ignored){

    }
  }
}
