package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.LinkedList;
import java.util.List;

public class Nobody extends GodCard {

    public Nobody (Game game){
        super(game);
        name = GodName.Nobody;
    }

    /*public List<Point> computeReachablePoints() {

        LinkedList<Point> reachablePoints = new LinkedList<>();
        try {
            Player player = game.getCurrentPlayer();
            Board board = game.getBoard();
            Worker selectedWorker = player.getCurrentWorker();
            Point workerPosition = board.getItemPosition(selectedWorker);

            List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);
            for (Point candidatePosition : candidatePositions) {
                try {
                    Point p = candidatePosition;
                    Item topmostItem = board.getTopmostItem(p);
                    if(topmostItem.getLevel() != -1 &&)
                } catch (BoxEmptyException e) {
                    reachablePoints.add(candidatePosition);
                } catch (InvalidPositionException ignored) {

                }
            }
            player.moveCurrentWorker(new Point(4, 3));

        } catch (ItemNotFoundException ignored) {
        }
        return reachablePoints;
    }*/
}
