package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.moves.Construction;

import java.util.List;

public class ValidationDecider extends Decider {

    public static boolean validateConstructionAtlas(List<Point> list, Construction construction) {
        try {
            if(construction.getBlock().getLevel() != Block.MAX_LEVEL) {
                return list.contains(construction.getTarget())
                        && construction.getBlock().canBePlacedOn(construction.getBoard().getItems(construction.getTarget()).peek());
            } else {
                return list.contains(construction.getTarget());
            }
        } catch (InvalidPositionException ignored){
            System.err.println("This should really never happen...");
        } catch (BoxEmptyException e){
            return construction.getBlock().getLevel() == 1;
        }

        return false;
    }

}
