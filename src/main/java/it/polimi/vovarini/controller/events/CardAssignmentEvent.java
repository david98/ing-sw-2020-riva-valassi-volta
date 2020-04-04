package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.godcards.GodName;

//risponde alla scelta del giocatore della propria carta, successiva alla selezione
public class CardAssignmentEvent extends GameEvent {

    private GodName assignedCard;

    public CardAssignmentEvent(Object source){
        super(source);
    }
}
