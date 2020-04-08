package it.polimi.vovarini.model.board.items;

import java.util.stream.IntStream;

public class Block extends Item implements Cloneable {

  public static final int MIN_LEVEL = 1;
  public static final int MAX_LEVEL = 4;
  public static final int WIN_LEVEL = 3;

  // static array where blocks[i] is a block of level i+1
  public static final Block[] blocks =
          IntStream.range(MIN_LEVEL, MAX_LEVEL + 1)
              .mapToObj(
                  level -> {
                    try {
                      return new Block(level);
                    } catch (InvalidLevelException e) {
                      return null;
                    }
                  })
              .toArray(Block[]::new);

  protected int level;

  public Block(int level) throws InvalidLevelException {
    if (level < MIN_LEVEL || level > MAX_LEVEL) {
      throw new InvalidLevelException();
    }
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  public boolean canBePlacedOn(Item item) {
    if (item instanceof Block) {
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
