package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Represents that the godCard skills have changed
 * (i.e. some skills has been changed)
 *
 * @author Marco Riva
 */
public class GodCardUpdateEvent extends GameEvent {

    private Player owner;
    /**
     *
     * @param source The event source (it should be a {@link it.polimi.vovarini.model.godcards.GodCard} object.
     * @param owner The player who owns the modified godCard.
     */
    public GodCardUpdateEvent(Object source, Player owner){
        super(source);
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }
}
