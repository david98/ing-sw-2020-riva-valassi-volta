package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Point;

/**
 * Represents the initial position choice for the
 * {@link it.polimi.vovarini.model.Player}'s current
 * {@link it.polimi.vovarini.model.board.items.Worker}.
 *
 * @author Mattia Valassi
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 *
 */
public class SpawnWorkerEvent extends GameEvent {

    private Point target;

    /**
     *
     * @param source The event source, it should be a {@link it.polimi.vovarini.model.Player} object.
     * @param target Where the current {@link it.polimi.vovarini.model.board.items.Worker} should spawn.
     */
    public SpawnWorkerEvent(Object source, Point target){
        super(source);
        this.target = target;
    }

    public Point getTarget() {
        return target;
    }
}
