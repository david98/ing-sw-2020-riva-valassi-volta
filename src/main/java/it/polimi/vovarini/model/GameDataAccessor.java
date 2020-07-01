package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;

/**
 * This interface specifies the methods that all objects
 * containing data pertaining to a Santorini game should implement.
 *
 * @author Davide Volta
 */
public interface GameDataAccessor {
  /**
   * Getter method for the current phase in the Game
   * @return the current phase in the game
   */
  Phase getCurrentPhase();

  /**
   * Getter method for the players in the Game
   * @return the players in the Game
   */
  Player[] getPlayers();

  /**
   * Getter method for the player currently playing in the game
   * @return the player currently playing in the game
   */
  Player getCurrentPlayer();

  /**
   * Setter method for the current phase in the game
   * @param phase a value of Phase I want to be set as the current phase in the game
   */
  void setCurrentPhase(Phase phase);

  /**
   * This method checks if a Game has all players registered
   * @return true if the game has all players registered, false otherwise
   */
  boolean isFull();

  /**
   * This method points to the nextPlayer
   */
  void nextPlayer();

  /**
   * Getter method for the board where the game is played
   * @return the board where the game is played
   */
  Board getBoard();
}
