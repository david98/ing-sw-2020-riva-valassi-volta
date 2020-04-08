package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCard;

import java.util.EnumMap;
import java.util.Map;

public class Player {

  private EnumMap<Sex, Worker> workers;
  private Sex currentSex;

  private GodCard godCard;
  private String nickname;

  public Player(String nickname) {
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Female, new Worker(Sex.Female));
    workers.put(Sex.Male, new Worker(Sex.Male));
    currentSex = Sex.Male;

    this.nickname = nickname;
  }

  public Player(GodCard assignedCard, String nickname) {
    workers = new EnumMap<>(Sex.class);
    workers.put(Sex.Female, new Worker(Sex.Female));
    workers.put(Sex.Male, new Worker(Sex.Male));
    currentSex = Sex.Male;
    godCard = assignedCard;
    this.nickname = nickname;
  }

  public Map<Sex, Worker> getWorkers() {
    return workers;
  }

  public void setCurrentSex(Sex sex) {
    currentSex = sex;
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
