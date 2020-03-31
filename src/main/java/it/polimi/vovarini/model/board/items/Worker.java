package it.polimi.vovarini.model.board.items;

public class Worker extends Item {
  private Sex sex;

  public Worker(Sex sex) {
    this.sex = sex;
  }

  public Sex getSex() {
    return sex;
  }

  @Override
  public boolean canBePlacedOn(Item item) {
    if (item instanceof Block) {
      Block block = (Block) item;
      return block.getLevel() < 4;
    } else {
      return super.canBePlacedOn(item);
    }
  }

  @Override
  public boolean canBeRemoved() {
    return true;
  }

  @Override
  public String toString() {
    return sex == Sex.Male ? "M" : "F";
  }
}
