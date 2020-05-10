package it.polimi.vovarini.model.godcards.deciders;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.common.exceptions.ItemNotFoundException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
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
        try {
            if(construction.getBlock().getLevel() != Block.MAX_LEVEL) {
                return list.contains(construction.getTarget())
                        && construction.getBlock().canBePlacedOn(construction.getBoard().getItems(construction.getTarget()).peek());
            }

            return list.contains(construction.getTarget());

        } catch (InvalidPositionException ignored){
            System.err.println("This should really never happen...");
        } catch (BoxEmptyException e){
            return list.contains(construction.getTarget()) && construction.getBlock().getLevel() == 1;
        }

        return false;
    }

    /**
     * This method allows you to validate also a Construction move that wants to build a block under the current worker (not a dome)
     * @param list is the list of all buildable points
     * @param construction is the construction move the player wants to perform
     * @return if the move that the player wants to perform is valid returns true, false otherwise
     * @author Marco Riva
     */
    public static boolean allowUnderMyself(List<Point> list, Construction construction) {

        Point target = construction.getTarget();
        int targetLevel = construction.getBoard().getBox(target).getLevel();
        int blockLevel = construction.getBlock().getLevel();

        return list.contains(target) && targetLevel + 1 == blockLevel;
    }



}
