package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Represents that the player object has been updated.
 */
public class PlayerInfoUpdateEvent extends GameEvent {

    private final Player targetPlayer;

    public PlayerInfoUpdateEvent(Object source, Player targetPlayer){
        super(source);
        this.targetPlayer = targetPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }
}
