package it.polimi.vovarini.model.board.items;

import it.polimi.vovarini.model.Player;

import java.util.Arrays;

/**
 * This class represents a worker.
 */
public class Worker extends Item {
  private final Sex sex;
  private final Player owner;

  /**
   * Creates a worker with the given sex and owner.
   *
   * @param sex   The sex of the worker.
   * @param owner The owner of the worker.
   */
  public Worker(Sex sex, Player owner) {
    this.sex = sex;
    this.owner = owner;
  }

  /**
   * Creates a worker which is a clone of another worker.
   *
   * @param worker The worker to be cloned.
   */
  public Worker(Worker worker) {
    sex = worker.sex;
    owner = worker.owner;
  }

  public Sex getSex() {
    return sex;
  }

  public Player getOwner() {
    return owner;
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

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{
            owner,
            sex
    });
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Worker) {
      Worker other = (Worker) obj;
      return sex.equals(other.sex) && owner.equals(other.owner);
    } else {
      return super.equals(obj);
    }
  }
}
