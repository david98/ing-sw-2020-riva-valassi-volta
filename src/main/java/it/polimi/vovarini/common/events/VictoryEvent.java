package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.Player;

/**
 * Signals pointing the winner player to all the others
 *
 * @author Marco Riva
 * @version 0.1
 * @since 0.1
 */
public class VictoryEvent extends GameEvent {

    private final Player winningPlayer;


    public VictoryEvent(Object source, Player winningPlayer) {
        super(source);
        this.winningPlayer = winningPlayer;
    }

    public Player getWinningPlayer() { return winningPlayer; }
}
