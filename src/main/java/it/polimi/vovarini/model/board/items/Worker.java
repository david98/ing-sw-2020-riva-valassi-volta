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

  /**
   * Getter method for the Sex of the worker
   * @return the Sex of the worker (Male or Female)
   */
  public Sex getSex() {
    return sex;
  }

  /**
   * Getter method for the Owner of the worker
   * @return the instance of the Player who owns the worker
   */
  public Player getOwner() {
    return owner;
  }

  /**
   * This method checks if an Item can be placed on a Worker
   * @param item The item which this object would be placed on.
   * @return true if the item can be placed on the worker, false otherwise
   */
  @Override
  public boolean canBePlacedOn(Item item) {
    if (item instanceof Block) {
      Block block = (Block) item;
      return block.getLevel() < 4;
    } else {
      return super.canBePlacedOn(item);
    }
  }

  /**
   * This method checks if a worker can be removed from his current spot on the Board
   * @return always true, as a worker is by definition a removable Item
   */
  @Override
  public boolean canBeRemoved() {
    return true;
  }

  /**
   * ToString method for the class Worker
   * @return a string representing the instance of a Worker
   */
  @Override
  public String toString() {
    return sex == Sex.Male ? "M" : "F";
  }

  /**
   * hashCode method for the class Worker
   * @return an hashcode representing the instance of a Worker
   */
  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{
            owner,
            sex
    });
  }

  /**
   * equals method for the class Worker
   * @param obj an object to compare with the Worker
   * @return true if obj is an instance of Worker and if it has both sex and owner equal in value to the other worker. False otherwise
   */
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
