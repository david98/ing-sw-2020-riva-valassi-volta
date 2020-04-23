package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.moves.Movement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class MinotaurTests {

  private static Game game;
  private static GodCard minotaur;

  @BeforeAll
  private static void init(){
    try {
      game = new Game(2);

      game.addPlayer("Guest01");
      game.addPlayer("Guest02");

      minotaur = GodCardFactory.create(GodName.Minotaur);
      minotaur.setGame(game);

      for (Player player: game.getPlayers()){
        player.setGodCard(minotaur);
      }
    } catch (InvalidNumberOfPlayersException e){
      e.printStackTrace();
    }
  }

  @BeforeEach
  private void resetGame(){
    Board b = game.getBoard();
    for (int x = 0; x < b.getSize(); x++){
      for (int y = 0; y < b.getSize(); y++){
        Point cur = new Point(x, y);
        while (true){
          try {
            b.remove(cur);
          } catch (BoxEmptyException | InvalidPositionException e){
            break;
          }
        }
      }
    }
  }

  private static Stream<Arguments> provideSituations() {
    LinkedList<Point> allPoints = new LinkedList<>();
    for (int x = 0; x < game.getBoard().getSize(); x++) {
      for (int y = 0; y < game.getBoard().getSize(); y++) {
        allPoints.add(new Point(x, y));
      }
    }

    LinkedList<Stack<Item>> allPossibleEndContents = new LinkedList<>();

    for (int l = 0; l < Block.MAX_LEVEL; l++){
      Stack<Item> cEmpty = new Stack<>();
      Stack<Item> cDome = new Stack<>();
      Stack<Item> cEnemy = new Stack<>();

      cDome.push(Block.blocks[Block.MAX_LEVEL - 1]);
      cEnemy.push(game.getPlayers()[1].getCurrentWorker());

      for (int i = l - 1; i >= 0; i--){
        cEmpty.push(Block.blocks[i]);
        cDome.push(Block.blocks[i]);
        cEnemy.push(Block.blocks[i]);
      }

      allPossibleEndContents.add(cEmpty);
      allPossibleEndContents.add(cDome);
      allPossibleEndContents.add(cEnemy);
    }


    LinkedList<Arguments> args = new LinkedList<>();

    for (Point start: allPoints){
      for (Point end: allPoints){
        if (end.isAdjacent(start)) {
          for (Stack<Item> content: allPossibleEndContents){
            args.add(Arguments.of(start, end));
          }
        }
      }
    }

    return args.stream();
  }

  @ParameterizedTest
  @MethodSource("provideSituations")
  @DisplayName("Test that Minotaur's movement rules work")
  void minotaurMovement(Point start, Point end,
                        Stack<Item> reverseStartContent, Stack<Item> reverseEndContent,
                        Stack<Item> reverseForcedEndContent){
    Board b = game.getBoard();
    Point forcedEnd = new Point(2 * end.getX() - start.getX(),
            2 * end.getY() - start.getY());
    while (!reverseStartContent.isEmpty()){
      try {
        b.place(reverseStartContent.pop(), start);
      } catch (InvalidPositionException | BoxFullException e){
        e.printStackTrace();
      }
    }
    Worker cw = game.getCurrentPlayer().getCurrentWorker();

    // if start does not contain the current player's Worker the whole test doesn't make sense
    assertDoesNotThrow(() ->
            assumeTrue(b.getItems(start).peek().equals(cw))
    );
    assumeTrue(b.isPositionValid(start));
    assumeTrue(b.isPositionValid(end));

    while (!reverseEndContent.isEmpty()){
      try {
        b.place(reverseEndContent.pop(), end);
      } catch (InvalidPositionException | BoxFullException e){
        e.printStackTrace();
      }
    }

    while (!reverseForcedEndContent.isEmpty()){
      try {
        b.place(reverseForcedEndContent.pop(), forcedEnd);
      } catch (InvalidPositionException | BoxFullException e){
        e.printStackTrace();
      }
    }

    Item endItem = null;
    Item forcedEndItem = null;
    List<Point> reachablePoints;

    try {
      reachablePoints = minotaur.computeReachablePoints();
    } catch (CurrentPlayerLosesException e){
      reachablePoints = new LinkedList<>();
    }

    try {
      endItem = b.getItems(end).peek();

      //end is not empty, let's check if it's not occupied
      if (cw.canBePlacedOn(endItem)){
        //normal move
        assertTrue(reachablePoints.contains(end));
        return;
      }

      // mmmh, end is occupied
      if (!endItem.canBeRemoved()){
        // can't push back cause ya can't remove
        assertFalse(reachablePoints.contains(end));
        return;
      }

      try {
        forcedEndItem = b.getItems(forcedEnd).peek();
      } catch (InvalidPositionException e){
        // oh no, you would exit the Board, i'm sorry
        assertDoesNotThrow(() -> assertFalse(minotaur.computeReachablePoints().contains(end)));
        return;
      } catch (BoxEmptyException e){
        // ok, you can push back cause it's empty
        assertDoesNotThrow(() -> assertTrue(minotaur.computeReachablePoints().contains(end)));
        return;
      }

      assertEquals(endItem.canBePlacedOn(forcedEndItem), reachablePoints.contains(end));

    } catch (InvalidPositionException ignored){
      // wut?
    } catch (BoxEmptyException e){
      // ok, end is empty so yes you can move
      assertTrue(reachablePoints.contains(end));
    }
  }

  @Test
  @DisplayName("Test an invalid movement with a GodCard of type Minotaur")
  /** status:
   *  startBox: lv 2 + currentWorker
   *  endBox: lv 0 + otherWorker of currentPlayer
   *  forcedDestinationBox: lv 0 + free
   */
  public void invalidMovementOtherWorker() {

    try {
      GodCard minotaur = game.getCurrentPlayer().getGodCard();
      Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
      Worker otherWorker = game.getCurrentPlayer().getOtherWorker();

      Board board = game.getBoard();
      Point start = new Point(0, 0);
      Point end = new Point(1, 1);
      Movement movement = new Movement(board, start, end);

      board.place(Block.blocks[0], start);
      board.place(Block.blocks[1], start);
      board.place(currentWorker, start);
      board.place(otherWorker, end);

      if (game.validateMove(movement)) {
        List<Movement> movementList = minotaur.listEffects(movement);
        for (Movement m : movementList) {
          game.performMove(m);
        }
      }

      assertFalse(Reachability.isPointReachableConditionedExchange(game, end));
      assertEquals(currentWorker, board.getBox(start).getItems().peek());
      assertEquals(otherWorker, board.getBox(end).getItems().peek());

    } catch (BoxFullException ignored) {
    } catch (BoxEmptyException ignored) {
    } catch (InvalidPositionException ignored) {
    }
  }

  @Test
  @DisplayName("Test an invalid movement with a GodCard of type Minotaur")
  /** status
   *  startBox: lv 2 + currentWorker
   *  endBox: lv 0 + enemy's Worker
   *  forcedDestinationBox: lv 0 + otherWorker of currentPlayer
   */
  public void invalidMovementForcedDestinationOccupied() {

    try {
      GodCard minotaur = game.getCurrentPlayer().getGodCard();
      Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
      Worker otherWorker = game.getCurrentPlayer().getOtherWorker();
      Player enemysPlayer = game.getPlayers()[1];
      Worker enemysWorker = enemysPlayer.getCurrentWorker();

      Board board = game.getBoard();
      Point start = new Point(0, 0);
      Point end = new Point(1, 1);
      Point forcedDestination = new Point(2,2);
      Movement movement = new Movement(board, start, end);

      board.place(Block.blocks[0], start);
      board.place(Block.blocks[1], start);
      board.place(currentWorker, start);
      board.place(enemysWorker, end);
      board.place(otherWorker, forcedDestination);

      if(game.validateMove(movement)) {
        List<Movement> movementList = minotaur.listEffects(movement);
        for(Movement m : movementList) {
          game.performMove(m);
        }
      }

      assertFalse(Reachability.isPointReachableConditionedExchange(game, end));
      assertEquals(currentWorker, board.getBox(start).getItems().peek());
      assertEquals(enemysWorker, board.getBox(end).getItems().peek());
      assertEquals(otherWorker, board.getBox(forcedDestination).getItems().peek());

    } catch (BoxFullException ignored) {
    } catch (BoxEmptyException ignored) {
    } catch (InvalidPositionException ignored) {
    }
  }

  @Test
  @DisplayName("Test an invalid movement with a GodCard of type Minotaur")
  /** status
   *  startBox: lv 1 + currentWorker
   *  endBox: lv 3 + enemy's Worker
   *  forcedDestinationBox: lv 3 + free
   */
  public void invalidMovementLevel() {

    try {
      GodCard minotaur = game.getCurrentPlayer().getGodCard();
      Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
      Player enemysPlayer = game.getPlayers()[1];
      Worker enemysWorker = enemysPlayer.getCurrentWorker();

      Board board = game.getBoard();
      Point start = new Point(0, 0);
      Point end = new Point(1, 1);
      Point forcedDestination = new Point(2,2);
      Movement movement = new Movement(board, start, end);

      board.place(Block.blocks[0], start);
      board.place(currentWorker, start);
      board.place(Block.blocks[0], end);
      board.place(Block.blocks[1], end);
      board.place(Block.blocks[2], end);
      board.place(enemysWorker, end);
      board.place(Block.blocks[0], forcedDestination);
      board.place(Block.blocks[1], forcedDestination);
      board.place(Block.blocks[2], forcedDestination);

      if(game.validateMove(movement)) {
        List<Movement> movementList = minotaur.listEffects(movement);
        for(Movement m : movementList) {
          game.performMove(m);
        }
      }

      assertFalse(Reachability.isPointReachableConditionedExchange(game, end));
      assertEquals(currentWorker, board.getBox(start).getItems().peek());
      assertEquals(enemysWorker, board.getBox(end).getItems().peek());

    } catch (BoxFullException ignored) {
    } catch (BoxEmptyException ignored) {
    } catch (InvalidPositionException ignored) {
    }
  }

  @Test
  @DisplayName("Test a valid movement with a GodCard of type Minotaur")
  /** status
   *  startBox: lv 2 + currentWorker
   *  endBox: lv 0 + enemy's Worker
   *  forcedDestinationBox: lv 3 + free
   */
  public void validMovement() {

    try {
      GodCard minotaur = game.getCurrentPlayer().getGodCard();
      Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
      Worker otherWorker = game.getCurrentPlayer().getOtherWorker();
      Player enemysPlayer = game.getPlayers()[1];
      Worker enemysWorker = enemysPlayer.getCurrentWorker();

      Board board = game.getBoard();
      Point start = new Point(0, 0);
      Point end = new Point(1, 1);
      Point forcedDestination = new Point(2,2);
      Movement movement = new Movement(board, start, end);

      board.place(Block.blocks[0], start);
      board.place(Block.blocks[1], start);
      board.place(currentWorker, start);
      board.place(enemysWorker, end);
      board.place(Block.blocks[0], forcedDestination);
      board.place(Block.blocks[1], forcedDestination);
      board.place(Block.blocks[2], forcedDestination);

      assertTrue(Reachability.isPointReachableConditionedExchange(game, end));
      assertFalse(minotaur.isMovementWinning(movement));

      if(game.validateMove(movement)) {
        List<Movement> movementList = minotaur.listEffects(movement);
        for(Movement m : movementList) {
          game.performMove(m);
        }
      }

      assertEquals(currentWorker, board.getBox(end).getItems().peek());
      assertEquals(enemysWorker, board.getBox(forcedDestination).getItems().peek());

    } catch (BoxFullException ignored) {
    } catch (BoxEmptyException ignored) {
    } catch (InvalidPositionException ignored) {
    }
  }

  @Test
  @DisplayName("Test a valid and winning movement with a GodCard of type Minotaur")
  /** status
   *  startBox: lv 2 + currentWorker
   *  endBox: lv 3 + enemy's Worker
   *  forcedDestinationBox: lv 3 + free
   */
  public void validWinningMovement() {

    try {
      GodCard minotaur = game.getCurrentPlayer().getGodCard();
      Worker currentWorker = game.getCurrentPlayer().getCurrentWorker();
      Worker otherWorker = game.getCurrentPlayer().getOtherWorker();
      Player enemysPlayer = game.getPlayers()[1];
      Worker enemysWorker = enemysPlayer.getCurrentWorker();

      Board board = game.getBoard();
      Point start = new Point(0, 0);
      Point end = new Point(1, 1);
      Point forcedDestination = new Point(2,2);
      Movement movement = new Movement(board, start, end);

      board.place(Block.blocks[0], start);
      board.place(Block.blocks[1], start);
      board.place(currentWorker, start);
      board.place(Block.blocks[0], end);
      board.place(Block.blocks[1], end);
      board.place(Block.blocks[2], end);
      board.place(enemysWorker, end);
      board.place(Block.blocks[0], forcedDestination);
      board.place(Block.blocks[1], forcedDestination);
      board.place(Block.blocks[2], forcedDestination);

      assertTrue(Reachability.isPointReachableConditionedExchange(game, end));
      assertTrue(minotaur.isMovementWinning(movement));

      if(game.validateMove(movement)) {
        List<Movement> movementList = minotaur.listEffects(movement);
        for(Movement m : movementList) {
          game.performMove(m);
        }
      }

      assertEquals(currentWorker, board.getBox(end).getItems().peek());
      assertEquals(enemysWorker, board.getBox(forcedDestination).getItems().peek());

    } catch (BoxFullException ignored) {
    } catch (BoxEmptyException ignored) {
    } catch (InvalidPositionException ignored) {
    }
  }
}
