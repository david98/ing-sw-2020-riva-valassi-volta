package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ArtemisTests {

    private static Game game;
    private static GodCard artemis;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            artemis = GodCardFactory.create(GodName.Artemis);
            artemis.setGameData(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(artemis);
            }
        } catch (InvalidNumberOfPlayersException e){
            e.printStackTrace();;
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
        game.setCurrentPhase(Phase.Start);
        game.getCurrentPlayer().getMovementList().clear();
        artemis.movementConstraints.clear();
    }

    private static Stream<Arguments> provideAllPossibleMovementMoves() {

        LinkedList<Arguments> args = new LinkedList<>();

        Board board = new Board(Board.DEFAULT_SIZE);
        LinkedList<Point> allPoints = new LinkedList<>();

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                allPoints.add(new Point(x, y));
            }
        }

        for(Point start : allPoints) {
            List<Point> startAdjacentPositions = board.getAdjacentPositions(start);
            for(Point endStart : startAdjacentPositions) {
                List<Point> endStartAdjacentPositions = board.getAdjacentPositions(endStart);
                for(Point secondEnd : endStartAdjacentPositions) {
                    args.add(Arguments.of(start, endStart, secondEnd));
                }
            }
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Artemis can be instantiated correctly")
    public void artemisCreation() {
        assertEquals(GodName.Artemis, game.getCurrentPlayer().getGodCard().name);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMovementMoves")
    @DisplayName("Test that Artemis' movement constraints are correctly applied")
    public void testMovementConstraint(Point start, Point endStart, Point secondEnd) {

        Board board = game.getBoard();

        try {
            board.place(game.getCurrentPlayer().getCurrentWorker(), start);
        } catch (InvalidPositionException | BoxFullException ignored) {
        }

        game.setCurrentPhase(artemis.computeNextPhase(game));
        assertEquals(Phase.Movement, game.getCurrentPhase());

        Movement firstMovement = new Movement(board, start, endStart);
        assertTrue(artemis.validate(artemis.computeReachablePoints(), firstMovement));
        game.performMove(firstMovement);

        game.setCurrentPhase(artemis.computeNextPhase(game));
        assertEquals(Phase.Movement, game.getCurrentPhase());

        Movement secondMovement = new Movement(board, endStart, secondEnd);
        assertEquals(!start.equals(secondEnd), artemis.validate(artemis.computeReachablePoints(), secondMovement));
    }
}
