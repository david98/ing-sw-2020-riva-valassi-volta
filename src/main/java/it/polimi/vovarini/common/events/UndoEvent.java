package it.polimi.vovarini.common.events;

/**
 * Represents a "Ctrl+Z" on the game: should result
 * in the last action being undone.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class UndoEvent extends GameEvent {

  /**
   * Builds an UndoEvent
   * @param source is the source object of the event
   */
  public UndoEvent(Object source) {
    super(source);
  }
}
