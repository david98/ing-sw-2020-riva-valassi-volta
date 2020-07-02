package it.polimi.vovarini.model;

import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.events.LossEvent;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a player in a {@link Game}.
 */
public class Player implements Serializable {

  private final EnumMap<Sex, Worker> workers;
  private Sex currentSex;

  private GodCard godCard;
  private final String nickname;

  private boolean isWorkerSelected;
  private List<Movement> movementList;
  private List<Construction> constructionList;
  private Board boardStatus;
  private boolean hasLost;

  /**
   * Builds a player from the nickname
   * @param nickname an alias typed by the user, representing himself as a Player
   */
  public Player(String nickname) {
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Female, new Worker(Sex.Female, this));
    workers.put(Sex.Male, new Worker(Sex.Male, this));
    currentSex = Sex.Male;

    this.nickname = nickname;

    isWorkerSelected = false;
    movementList = new LinkedList<>();
    constructionList = new LinkedList<>();
    boardStatus = new Board(Board.DEFAULT_SIZE);
    hasLost = false;

  }

  /**
   * Builds a player from the nickname and his assigned GodCard
   * @param nickname an alias typed by the user, representing himself as a Player
   * @param assignedCard the GodCard assigned to this user
   */
  public Player(GodCard assignedCard, String nickname) {
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Female, new Worker(Sex.Female, this));
    workers.put(Sex.Male, new Worker(Sex.Male, this));
    currentSex = Sex.Male;
    godCard = assignedCard;

    this.nickname = nickname;

    isWorkerSelected = false;
    movementList = new LinkedList<>();
    constructionList = new LinkedList<>();
    boardStatus = new Board(Board.DEFAULT_SIZE);
    hasLost = false;
  }

  /**
   * Creates a deep clone of a Player
   * @param p the player I want to clone
   */
  public Player(Player p) {
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Male, p.workers.get(Sex.Male));
    workers.put(Sex.Female, p.workers.get(Sex.Female));
    currentSex = p.currentSex;

    if (p.godCard != null) {
      godCard = GodCardFactory.clone(p.godCard);
    }

    nickname = p.nickname;

    isWorkerSelected = p.isWorkerSelected;
    movementList = new LinkedList<>(p.movementList);
    constructionList = new LinkedList<>(p.constructionList);
    boardStatus = p.boardStatus;
    hasLost = p.hasLost;
  }

  /**
   * Getter method for the workers owned by the Player
   * @return The Workers owned by the player
   */
  public Map<Sex, Worker> getWorkers() {
    return workers;
  }

  /**
   * This method sets a Sex value, allowing the Player to select one of his worker
   * @param sex the sex of the worker the Player wants to select
   */
  public void setCurrentSex(Sex sex) {
    currentSex = sex;
    isWorkerSelected = true;
  }

  /**
   * This method returns the worker currently selected, based upon the value of CurrentSex
   * @return the worker currently selected
   */
  public Worker getCurrentWorker() {
    return workers.get(currentSex);
  }

  /**
   * This method returns the worker currently not selected, based upon the value of CurrentSex
   * @return the worker currently not selected
   */
  public Worker getOtherWorker() {
    if (currentSex.equals(Sex.Male)) return workers.get(Sex.Female);
    else return workers.get(Sex.Male);
  }

  /**
   * Getter method for the GodCard assigned to this player
   * @return the GodCard assigned to this player
   */
  public GodCard getGodCard() {
    return godCard;
  }

  /**
   * Setter method for the GodCard I want to assign to this player
   * @param godCard ths GodCard I want to assign to this player
   */
  public void setGodCard(GodCard godCard) {
    this.godCard = godCard;
  }

  /**
   * Getter method for the Player's nickname
   * @return the Player's nickname
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * This method checks if a nickname is valid or not
   * @param nickname the nickname I want to check
   * @return true if the nickname is valid, false otherwise
   */
  public static boolean validateNickname(String nickname) {
    return (nickname != null) && nickname.matches("[A-Za-z0-9_]{4,16}$");
  }

  /**
   * This method checks if the Player has selected a worker
   * @return true if the Player has selected a worker, false otherwise
   */
  public boolean isWorkerSelected() {
    return isWorkerSelected;
  }

  /**
   * Setter method for the isWorkerSelected flag
   * @param value the boolean value I want to assign to the isWorkerSelected flag
   */
  public void setWorkerSelected(boolean value) {
    isWorkerSelected = value;
  }

  /**
   * Getter method for the Player's board status
   * @return an instance of Board representing the player's board status
   */
  public Board getBoardStatus() {
    return boardStatus;
  }

  /**
   * Setter method for the Player's board status
   * @param gameBoard the current Board where the game is played
   */
  public void setBoardStatus(Board gameBoard) {
    boardStatus = gameBoard;
  }

  /**
   * Getter method for the list of Movements performed by the Player in a complete round
   * @return the list of Movements performed by the Player in a complete round
   */
  public List<Movement> getMovementList() {
    return movementList;
  }

  /**
   * Getter method for the list of Constructions performed by the Player in a complete round
   * @return the list of Constructions performed by the Player in a complete round
   */
  public List<Construction> getConstructionList() {
    return constructionList;
  }

  /**
   * Setter method to set if a player has lost
   * @param hasLost boolean value I want to set. True if the player has lost, false otherwise
   */
  public void setHasLost(boolean hasLost) {
    this.hasLost = hasLost;
    if (hasLost) {
      setWorkerSelected(false);
      GameEventManager.raise(new LossEvent(this, this));
    }
  }

  /**
   * This method returns if this Player has lost
   * @return true if this Player has lost, false otherwise
   */
  public boolean isHasLost() {
    return hasLost;
  }

  /**
   * This method checks if a Player, in this round, perfomed a Movement that made him climb a level up (he has risen)
   * @param gameData the Data of the game all the Players are currently playing
   * @return true if a player moved up a level with a Movement in the last round, false otherwise
   */
  public boolean hasPlayerRisen(GameDataAccessor gameData) {

    for (Movement movement : movementList) {
      if (gameData.getBoard().getBox(movement.getEnd()).getLevel() -
              gameData.getBoard().getBox(movement.getStart()).getLevel() == 1)
        return true;
    }

    return false;
  }

  /**
   * hashCode method for the Player class
   * @return an hashcode representing this Player
   */
  @Override
  public int hashCode() {
    return nickname.hashCode();
  }

  /**
   * equals method for the Player class
   * @param obj an object to compare with this player
   * @return true if obj is an instance of Player and has the same nickname of this player. False otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Player) {
      return nickname.equals(((Player) obj).nickname);
    } else {
      return super.equals(obj);
    }
  }
}
