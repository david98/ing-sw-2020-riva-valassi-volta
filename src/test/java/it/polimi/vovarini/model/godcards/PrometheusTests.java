package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxEmptyException;
import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.moves.Construction;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrometheusTests {

    private static Game game;
    private static GodCard prometheus;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            prometheus = GodCardFactory.create(GodName.Prometheus);
            prometheus.setGame(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(prometheus);
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
        game.getCurrentPlayer().getConstructionList().clear();
        game.getCurrentPlayer().getMovementList().clear();
        prometheus.movementConstraints.clear();
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
            List<Point> adjacentPositions = board.getAdjacentPositions(start);
            for(Point end : adjacentPositions) {
                for(int startLevel = 0; startLevel < Block.MAX_LEVEL; startLevel++) {
                    for (int endLevel = 0; endLevel <= startLevel + 1 && endLevel < Block.MAX_LEVEL; endLevel++) {
                        args.add(Arguments.of(start, end, startLevel, endLevel, true));
                        args.add(Arguments.of(start, end, startLevel, endLevel, false));
                    }
                }
            }
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Prometheus can be instantiated correctly")
    public void prometheusCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Prometheus);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMovementMoves")
    @DisplayName("Test that Prometheus' movement constraints are correctly applied")
    public void testMovementConstraint(Point start, Point end, int lStart, int lEnd, Boolean construction) {
        Board board = game.getBoard();

        try {
            for(int i = 0; i < lStart; i++) {
                board.place(Block.blocks[i], start);
            }
            board.place(game.getCurrentPlayer().getCurrentWorker(), start);

            for(int i = 0; i < lEnd; i++) {
                board.place(Block.blocks[i], end);
            }
        } catch (InvalidPositionException | BoxFullException e) {
            e.printStackTrace();
        }

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(Phase.Construction, game.getCurrentPhase());

        if(construction) {
            Construction firstConstruction = new Construction(board, Block.blocks[lEnd], end);
            assertTrue(prometheus.validate(prometheus.computeBuildablePoints(),firstConstruction));
            game.performMove(firstConstruction);
            lEnd++;
        }

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(Phase.Movement, game.getCurrentPhase());

        Movement movement = new Movement(board, start, end);
        if(game.getCurrentPlayer().getConstructionList().isEmpty()) {
            assertEquals(lEnd - lStart <= 1, prometheus.validate(prometheus.computeReachablePoints(),movement));
        } else {
            assertEquals(lEnd - lStart < 1, prometheus.validate(prometheus.computeReachablePoints(),movement));
        }

        game.setCurrentPhase(prometheus.computeNextPhase(game));
        assertEquals(Phase.Construction, game.getCurrentPhase());
    }

}
