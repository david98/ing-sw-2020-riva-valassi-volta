package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

public class SpawnWorkerEvent extends GameEvent {

    private Point target;

    public SpawnWorkerEvent(Object source, Point target){
        super(source);
        this.target = target;
    }

    public Point getTarget() {
        return target;
    }
}
