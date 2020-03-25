package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;

public class Nobody extends GodCard {

  public Nobody(Game game) {
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
