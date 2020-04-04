package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.client.Point;
import it.polimi.vovarini.model.godcards.GodName;

import java.util.EventObject;
import java.util.HashMap;

public abstract class GameEvent extends EventObject {

    //private Player playerSource; (Capire se è necessario. Può darsi che sia raggiungibile tramite RemoteView oppure tramite altri mezzi)

    public GameEvent(Object source){
        super(source);
    }

}
