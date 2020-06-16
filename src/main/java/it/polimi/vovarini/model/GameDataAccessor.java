package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;

public interface GameDataAccessor {
  Phase getCurrentPhase();

  Player[] getPlayers();

  Player getCurrentPlayer();

  void setCurrentPhase(Phase phase);

  boolean isFull();

  void nextPlayer();

  Board getBoard();
}
