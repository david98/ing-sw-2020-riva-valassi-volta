package it.polimi.vovarini.controller;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;

import java.util.*;

public abstract class Controller {

  private Game game;

  private boolean starting;
  private int numberOfPlayers;
  private HashMap<String, Player> players;
  private EnumMap<GodName, GodCard> availableCards;

  public Controller() {
    starting = false;
  }

  public void startGame(int numberOfPlayers)
      throws InvalidNumberOfPlayersException, GameStartNotYetCompletedException {
    if (!starting) {
      starting = true;
      this.numberOfPlayers = numberOfPlayers;
      players = new HashMap<>();
      availableCards = new EnumMap<>(GodName.class);
    } else {
      throw new GameStartNotYetCompletedException();
    }
  }

  public void addPlayer(String nickname) throws NicknameAlreadyInUseException, GameFullException {
    if (players.keySet().size() == numberOfPlayers) {
      throw new GameFullException();
    }
    if (players.get(nickname) != null) {
      throw new NicknameAlreadyInUseException();
    }
    players.put(nickname, new Player(nickname));
  }

  public Player getWhoDecides() throws MissingPlayersException {
    if (players.keySet().size() < numberOfPlayers) {
      throw new MissingPlayersException();
    }
    int rand = new Random().nextInt(numberOfPlayers);
    int i = 0;
    for (Player player : players.values()) {
      if (i == rand) {
        return player;
      }
      i++;
    }
    // we really should NOT get here, but let's put this just in case
    return (Player) players.values().toArray()[0];
  }

  public abstract Map<GodName, GodCard> getAllGodCards();

  public void setAvailableCards(Map<GodName, GodCard> cards)
      throws InvalidNumberOfGodCardsException, AvailableCardsAlreadySetException {
    if (!availableCards.isEmpty()) {
      throw new AvailableCardsAlreadySetException();
    }
    if (cards.keySet().size() != numberOfPlayers) {
      throw new InvalidNumberOfGodCardsException();
    }
    for (GodName key : cards.keySet()) {
      availableCards.put(key, cards.get(key));
    }
  }

  public Map<GodName, GodCard> getAvailableCards() {
    return availableCards.clone();
  }

  public abstract void selectWorker(Sex sex);

  public abstract void moveCurrentWorker(Point destination);

  public abstract void buildBlock(Point target);

  public static void main(String[] args) {}
}
