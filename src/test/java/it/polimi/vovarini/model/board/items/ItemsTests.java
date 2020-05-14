package it.polimi.vovarini.model.board.items;

import it.polimi.vovarini.common.exceptions.InvalidLevelException;
import it.polimi.vovarini.model.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

public class ItemsTests {

  private static EnumMap<Sex, Worker> workers;
  private static ArrayList<Block> blocks;

  @BeforeAll
  private static void init() {
    Player testPlayer = new Player("test_player");
    blocks = new ArrayList<>();
    workers = new EnumMap<>(Sex.class);
    try {
      for (int i = Block.MIN_LEVEL; i <= Block.MAX_LEVEL; i++) {
        blocks.add(new Block(i));
      }
      for (Sex sex : Sex.values()) {
        workers.put(sex, new Worker(sex, testPlayer));
      }
    } catch (InvalidLevelException ignored) {

    }
  }

  @Test
  @DisplayName("Test that a Worker can be instantiated correctly")
  void workerCreation() {
    Player testPlayer = new Player("test_player");
    Worker worker = new Worker(Sex.Male, testPlayer);
    Worker workerTwo = new Worker(worker);

    assertEquals(Sex.Male, worker.getSex());
    assertEquals(testPlayer, worker.getOwner());
    assertEquals(worker.getSex(), workerTwo.getSex());
    assertEquals(worker.getOwner(), workerTwo.getOwner());

    assertEquals(worker.toString(), "M");
    assertTrue(worker.canBeRemoved());
  }

  @Test
  @DisplayName("Tests that two equal Workers are acknowledged as that")
  void workerEquals(){
    Player testPlayer = new Player("test_player");
    Worker workerOne = new Worker(Sex.Male, testPlayer);
    Worker workerTwo = new Worker(Sex.Male, testPlayer);
    assertTrue(workerOne.equals(workerTwo));
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

  @Test
  @DisplayName("Test that equals in Block works")
  void blockEquals() {
    for (Block block : blocks) {
      for (Block other : blocks) {
        assertTrue(block.getLevel() == other.getLevel() || !block.equals(other));
      }
    }
  }

  @Test
  @DisplayName("Test that hashCode in Block works")
  void blockHashCode() {
    for (Block block : blocks) {
      for (Block other : blocks) {
        assertTrue(block.hashCode() == other.hashCode() || !block.equals(other));
      }
    }
  }

  @Test
  @DisplayName("Test copy constructor on Block")
  void testBlockCopy(){
    try {
      Block original = new Block(Block.MIN_LEVEL);
      Block clone = new Block(original);
      assertNotSame(original, clone);
      assertEquals(original, clone);
    } catch (InvalidLevelException ignored){
    }
  }
}
