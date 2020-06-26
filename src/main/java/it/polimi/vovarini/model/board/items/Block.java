package it.polimi.vovarini.model.board.items;

import it.polimi.vovarini.common.exceptions.InvalidLevelException;

import java.util.stream.IntStream;

/**
 * This class represents a block you can place on the {@link it.polimi.vovarini.model.board.Board} due to a {@link it.polimi.vovarini.model.moves.Construction}
 * move.
 *
 * @author Davide Volta
 * @author Marco Riva
 * @author Mattia Valassi
 */
public class Block extends Item {

  public static final int MIN_LEVEL = 1;
  public static final int MAX_LEVEL = 4;
  public static final int WIN_LEVEL = 3;

  // static array where blocks[i] is a block of level i+1
  public static final Block[] blocks =
      IntStream.range(MIN_LEVEL, MAX_LEVEL + 1)
          .mapToObj(
              l -> {
                try {
                  return new Block(l);
                } catch (InvalidLevelException ignored) {
                  return null;
                }
              })
          .toArray(Block[]::new);

  protected int level;

  public Block(int level) {
    if (level < MIN_LEVEL || level > MAX_LEVEL) {
      throw new InvalidLevelException();
    }
    this.level = level;
  }

  public Block(Block b){
    level = b.level;
  }

  public int getLevel() {
    return level;
  }

  /**
   * This method checks if an item can be placed on top of a block.
   * @param item is the item object you want to place on the block referenced by this method
   * @return true if the item can be placed on top of the block, false otherwise
   */
  @Override
  public boolean canBePlacedOn(Item item) {
    if (item == null){
      return level == 1;
    } else if (item instanceof Block) {
      Block block = (Block) item;
      return level == (block.getLevel() + 1);
    } else {
      return super.canBePlacedOn(item);
    }
  }

  @Override
  public String toString() {
    return "" + level;
  }

  @Override
  public int hashCode() {
    return level;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Block) {
      return ((Block) obj).level == level;
    } else {
      return super.equals(obj);
    }
  }
}
