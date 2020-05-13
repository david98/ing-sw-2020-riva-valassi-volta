package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.moves.Construction;

import java.util.List;

/**
 * ValidationDecider is an extension of Decider. It decides if a move is valid or not
 * @author Mattia Valassi
 * @version 2.0
 * @since 1.0
 */
public class ValidationDecider extends Decider {

    /**
     * This method allows you to validate also a Construction move that wants to build a dome block (Level 4 Block)
     * over another item that is not another dome or a worker.
     * @param list is the list of all buildable points
     * @param construction is the construction move the player wants to perform
     * @return if the move that the player wants to perform is valid returns true, false otherwise
     * @author Mattia Valassi, Marco Riva
     */
    public static boolean allowDome(List<Point> list, Construction construction) {
        Item targetTopmostItem = construction.getBoard().getItems(construction.getTarget()).peek();
        return list.contains(construction.getTarget()) &&
                (construction.getBlock().canBePlacedOn(targetTopmostItem)
                        || (construction.getBlock().getLevel() == Block.MAX_LEVEL));
    }

}
