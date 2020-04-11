package it.polimi.vovarini.controller.events;

import it.polimi.vovarini.model.Player;

public class SkipEvent extends GameEvent {

    public SkipEvent(Object source, Player player) {
        super(source, player);
    }
}
