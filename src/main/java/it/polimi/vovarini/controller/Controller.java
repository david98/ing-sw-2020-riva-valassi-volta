package it.polimi.vovarini.controller;

import it.polimi.vovarini.model.Game;

import java.util.*;

public class Controller implements EventListener {
  private Game game;

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    this.game = game;
  }

  // Questo è il controller del Mengi

  public void update() {}

  public static void main(String[] args) {}
}
