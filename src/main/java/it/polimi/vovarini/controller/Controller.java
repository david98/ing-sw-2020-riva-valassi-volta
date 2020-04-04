package it.polimi.vovarini.controller;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;
import jdk.jfr.Event;

import java.awt.event.ActionListener;
import java.util.*;

public class Controller implements EventListener {
  private Game game;

  //Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    this.game = game;
  }

  public void update() { }

  public static void main(String[] args) {}

}
