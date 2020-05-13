package it.polimi.vovarini.model.board.items;

import it.polimi.vovarini.model.Player;

public class Worker extends Item {
  private final Sex sex;
  private Player owner;

  public Worker(Sex sex, Player owner) {
    this.sex = sex;
    this.owner = owner;
  }

  public Worker(Worker worker){
    sex = worker.sex;
    owner = worker.owner;
  }

  public Sex getSex() {
    return sex;
  }

  public Player getOwner() { return owner; }

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


  public boolean equals(Worker other){
    return sex.equals(other.sex) && owner.equals(other.owner); }

}
