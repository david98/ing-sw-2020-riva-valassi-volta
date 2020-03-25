package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCard;

import java.util.EnumMap;
import java.util.Map;

public class Player {


  private Game game;

  private int movementsLeft;
  private boolean hasMoved;
  private boolean hasBuilt;

  private EnumMap<Sex, Worker> workers;
  private Sex currentSex;
  private Phase currentPhase;

  private GodCard godCard;
  private String nickname;

  public Player(Game game, GodCard assignedCard, String nickname) {
    this.game = game;
    movementsLeft = 0;
    hasMoved = false;
    hasBuilt = false;
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Female, new Worker(Sex.Female));
    workers.put(Sex.Male, new Worker(Sex.Male));
    currentSex = Sex.Male;
    godCard = assignedCard;
    this.nickname = nickname;
    currentPhase = Phase.Wait;
  }

  public void moveCurrentWorker(Point destination) {
    try {
      Board board = game.getBoard();
      Point start = board.getItemPosition(getCurrentWorker());
      Move movement = new Movement(board, start, destination);
      game.performMove(movement);
    } catch (ItemNotFoundException ignored) {

    }
  }

  public Map<Sex, Worker> getWorkers() {
    return workers;
  }

  public void setCurrentSex (Sex sex){
    currentSex = sex;
  }


  public Worker getCurrentWorker(){
        return workers.get(currentSex);
    }

  public Worker getOtherWorker(){
      if(currentSex.equals(Sex.Male)) return workers.get(Sex.Female);
      else return workers.get(Sex.Male);
  }

  public GodCard getGodCard(){
      return godCard;
  }

  public String getNickname() {
      return nickname;
  }

  public void setCurrentPhase(Phase phase){
      currentPhase = phase;
  }

}
