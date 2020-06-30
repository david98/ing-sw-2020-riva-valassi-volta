package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;

/**
 * This interface specifies the methods that all objects
 * containing data pertaining to a Santorini game should implement.
 *
 * @author Davide Volta
 */
public interface GameDataAccessor {
  Phase getCurrentPhase();

  Player[] getPlayers();

  Player getCurrentPlayer();

  void setCurrentPhase(Phase phase);

  boolean isFull();

  void nextPlayer();

  Board getBoard();
}
