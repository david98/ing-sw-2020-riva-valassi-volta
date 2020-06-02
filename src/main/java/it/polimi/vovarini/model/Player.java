package it.polimi.vovarini.model;

import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.events.LossEvent;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Movement;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

  public Player(Player p){
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

  public Map<Sex, Worker> getWorkers() {
    return workers;
  }

  public void setCurrentSex(Sex sex) {
    currentSex = sex;
    isWorkerSelected = true;
  }

  public Worker getCurrentWorker() {
    return workers.get(currentSex);
  }

  public Worker getOtherWorker() {
    if (currentSex.equals(Sex.Male)) return workers.get(Sex.Female);
    else return workers.get(Sex.Male);
  }

  public GodCard getGodCard() {
    return godCard;
  }

  public void setGodCard(GodCard godCard) {
    this.godCard = godCard;
  }

  public String getNickname() {
    return nickname;
  }

  public static boolean validateNickname(String nickname) {
    return (nickname != null) && nickname.matches("[A-Za-z0-9_]{4,16}$");
  }

  public boolean isWorkerSelected() {
    return isWorkerSelected;
  }

  public void setWorkerSelected (boolean value){
    isWorkerSelected = value;
  }

  public Board getBoardStatus(){
    return boardStatus;
  }

  public void setBoardStatus(Board gameBoard){
    boardStatus = gameBoard;
  }

  public List<Movement> getMovementList() {
    return movementList;
  }

  public List<Construction> getConstructionList() {
    return constructionList;
  }


  public void setHasLost(boolean hasLost) {
    this.hasLost = hasLost;
    if(hasLost){
      GameEventManager.raise(new LossEvent(this, this));
    }
  }

  public boolean isHasLost() {
    return hasLost;
  }

  public boolean hasPlayerRisen(GameDataAccessor gameData){

    for (Movement movement : movementList){
      if (gameData.getBoard().getBox(movement.getEnd()).getLevel() -
              gameData.getBoard().getBox(movement.getStart()).getLevel() == 1)
        return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return nickname.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Player) {
      return nickname.equals(((Player) obj).nickname);
    } else {
      return super.equals(obj);
    }
  }
}
