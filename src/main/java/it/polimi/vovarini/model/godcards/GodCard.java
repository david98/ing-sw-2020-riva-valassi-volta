package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Movement;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.LinkedList;
import java.util.List;

public abstract class GodCard {


    protected Game game;
    protected GodName name;


  public GodCard(Game game) {
    this.game = game;
    this.name = GodName.Nobody;
  }

  public List<Point> computeReachablePoints() {
    LinkedList<Point> reachablePoints = new LinkedList<>();

    try {
      Player player = game.getCurrentPlayer();
      Board board = game.getBoard();
      Worker selectedWorker = player.getCurrentWorker();
      Point workerPosition = board.getItemPosition(selectedWorker);

      List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

      for (int i = 0; i < candidatePositions.size(); i++) {
        try {
          if (selectedWorker.canBePlacedOn(board.getTopmostItem(candidatePositions.get(i)))) {
            reachablePoints.add(candidatePositions.get(i));
          }
        } catch (BoxEmptyException e) {
          reachablePoints.add(candidatePositions.get(i));
        } catch (InvalidPositionException ignored) {

        }
      }


        } catch (ItemNotFoundException ignored) {
            System.out.println("NOT FOUND!");
        }
        return reachablePoints;
    }

    public boolean checkWin(Movement lastMovement){
        return false;
    }

    public boolean checkLoss(Point workerPosition){
        return false;
    }

    public List<Point> computeBuildablePoints(){
        LinkedList<Point> reachablePoints = new LinkedList<>();

        try {
            Player player = game.getCurrentPlayer();
            Board board = game.getBoard();
            Worker selectedWorker = player.getCurrentWorker();
            Point workerPosition = board.getItemPosition(selectedWorker);

            List<Point> candidatePositions = board.getAdjacentPositions(workerPosition);

            for (int i = 0; i < candidatePositions.size(); i++){
                try {
                    if (selectedWorker.canBePlacedOn(board.getTopmostItem(candidatePositions.get(i)))){
                        reachablePoints.add(candidatePositions.get(i));
                    }
                } catch (BoxEmptyException e){
                    reachablePoints.add(candidatePositions.get(i));
                } catch (InvalidPositionException ignored){

                }
            }

        } catch (ItemNotFoundException ignored) {
            System.out.println("NOT FOUND!");
        }
        return reachablePoints;
    }

    public void consequences(Game game){}


}
