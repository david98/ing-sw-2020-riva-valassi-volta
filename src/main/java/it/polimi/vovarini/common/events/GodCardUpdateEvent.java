package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;

/**
 * Represents that the godCard skills have changed
 * (i.e. some skills has been changed)
 *
 * @author Marco Riva
 */
public class GodCardUpdateEvent extends GameEvent {

    private final GodCard updatedCard;
    private final Player owner;
    /**
     *
     * @param source The event source (it should be a {@link it.polimi.vovarini.model.Game} object.
     * @param owner The player who owns the modified godCard.
     */
    public GodCardUpdateEvent(Object source, GodCard updatedCard, Player owner){
        super(source);
        this.updatedCard = GodCardFactory.clone(updatedCard);
        this.owner = owner;
    }

    public GodCard getUpdatedCard() {
        return updatedCard;
    }

    public Player getOwner() {
        return owner;
    }
}
