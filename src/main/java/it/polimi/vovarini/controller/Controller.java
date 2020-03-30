package it.polimi.vovarini.controller;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Sex;

public abstract class Controller {

  public abstract void startGame(int numberOfPlayers);

  public abstract void addPlayers(String[] nicknames);

  public abstract void selectWorker(Sex sex);

  public abstract void moveCurrentWorker(Point destination);

  public abstract void buildBlock(Point target);
}
