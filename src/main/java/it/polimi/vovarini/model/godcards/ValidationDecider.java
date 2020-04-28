package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.moves.Construction;

import java.util.List;

/**
 * ValidationDecider is an extension of Behavior. It represents in specific the "Validate Move" behavior.
 * Here, all methods influenced by cards acting on the Validate Move aspect of the Game are listed
 * @author Mattia Valassi
 * @author Marco Riva
 */
public class ValidationDecider extends Decider {

    /**
     * This method checks if, after applying Atlas' effect, it is possible to build a cupola
     * on the point chosen by the player. (Atlas adds the possibility to build a cupola at any level)
     *
     * @param list is the list of points computed by the pre-move method {@link GodCard#computeBuildablePoints()}
     * @param construction is the construction move the player wants to perform
     * @return true if the move that the player wants to perform is valid, false otherwise
     * @author Marco Riva
     */
    public static boolean validateConstructionAtlas(List<Point> list, Construction construction) {
        try {
            if(construction.getBlock().getLevel() != Block.MAX_LEVEL) {
                return list.contains(construction.getTarget())
                        && construction.getBlock().canBePlacedOn(construction.getBoard().getItems(construction.getTarget()).peek());
            }

            return list.contains(construction.getTarget());

        } catch (InvalidPositionException ignored){
            System.err.println("This should really never happen...");
        } catch (BoxEmptyException e){
            return construction.getBlock().getLevel() == 1;
        }

        return false;
    }

}
