package it.polimi.vovarini.model.board.items;

public class Block extends Item {

  public final static int MIN_LEVEL = 1;
  public final static int MAX_LEVEL = 4;

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
}
