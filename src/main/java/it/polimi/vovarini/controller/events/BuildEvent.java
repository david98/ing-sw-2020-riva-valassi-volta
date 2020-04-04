package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Point;

//si è deciso di separare MovementEvent e BuildEvent perchè, pur strutturalmente equivalenti, sono molto diversi a livello semantico
public class BuildEvent extends GameEvent {

    private Point buildEnd;

    public BuildEvent(Object source){
        super(source);
    }


    public Point getBuildEnd() {
        return buildEnd;
    }
}
