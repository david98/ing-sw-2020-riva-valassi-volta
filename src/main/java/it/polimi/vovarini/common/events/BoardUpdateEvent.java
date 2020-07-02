package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.board.Board;

/**
 * Represents that the board content has changed
 * (i.e. something has been placed/moved)
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class BoardUpdateEvent extends GameEvent {

  private final Board newBoard;

  /**
   * @param source   The event source (it should be a {@link it.polimi.vovarini.model.board.Board} object.
   * @param newBoard The new board.
   */
  public BoardUpdateEvent(Object source, Board newBoard) {
    super(source);
    this.newBoard = new Board(newBoard);
  }

  public Board getNewBoard() {
    return newBoard;
  }
}
