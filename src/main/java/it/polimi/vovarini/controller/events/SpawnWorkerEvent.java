package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;

public class SpawnWorkerEvent extends GameEvent {

    private Point target;

    public SpawnWorkerEvent(Object source, Player player, Point target){
        super(source, player);
        this.target = target;
    }

    public Point getTarget() {
        return target;
    }
}
