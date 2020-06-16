package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

public class LossEvent extends GameEvent {

    private final Player losingPlayer;


    public LossEvent(Object source, Player losingPlayer) {
        super(source);
        this.losingPlayer = losingPlayer;
    }

    public Player getLosingPlayer() { return losingPlayer; }
}
