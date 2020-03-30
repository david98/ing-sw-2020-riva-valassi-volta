package it.polimi.vovarini.model.board.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

public class ItemsTests {

  EnumMap<Sex, Worker> workers;
  ArrayList<Block> blocks;

  @BeforeEach
  void createTestItems() {
    blocks = new ArrayList<>();
    workers = new EnumMap<Sex, Worker>(Sex.class);
    try {
      for (int i = Block.MIN_LEVEL; i <= Block.MAX_LEVEL; i++) {
        blocks.add(new Block(i));
      }
      for (Sex sex : Sex.values()) {
        workers.put(sex, new Worker(sex));
      }
    } catch (InvalidLevelException ignored) {

    }
  }

  @Test
  @DisplayName("Test that a Worker can be instantiated correctly")
  void workerCreation() {
    Worker worker = new Worker(Sex.Male);

    assertEquals(Sex.Male, worker.getSex());
  }

  @Test
  @DisplayName("Test that a Block can be instantiated correctly")
  void blockCreation() {
    assertDoesNotThrow(
        () -> {
          Block block = new Block(1);
          assertEquals(1, block.getLevel());
        },
        "");
    InvalidLevelException thrown =
        assertThrows(
            InvalidLevelException.class,
            () -> new Block(Block.MAX_LEVEL + 1),
            "Block constructor should have thrown InvalidLevelException, but it didn't");
  }

  @Test
  @DisplayName("Check that placement rules work")
  void itemsPlacement() {
    for (Block cur : blocks) {
      for (Block other : blocks) {
        assertTrue(!cur.canBePlacedOn(other) || cur.getLevel() == other.getLevel() + 1);
      }
    }

    for (Sex sex : Sex.values()) {
      Worker cur = workers.get(sex);
      for (Block block : blocks) {
        assertTrue(cur.canBePlacedOn(block) || block.getLevel() == Block.MAX_LEVEL);
      }
    }
  }
}
