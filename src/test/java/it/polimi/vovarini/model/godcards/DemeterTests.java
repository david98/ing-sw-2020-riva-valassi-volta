package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.moves.Construction;
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

public class DemeterTests {

    private static Game game;
    private static GodCard demeter;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            demeter = GodCardFactory.create(GodName.Demeter);
            demeter.setGame(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(demeter);
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
        demeter.constructionConstraints.clear();
    }

    @Test
    @DisplayName("Test that a GodCard of type Demeter can be instantiated correctly")
    public void demeterCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Demeter);
    }

    private static Stream<Arguments> provideAllPossibleTarget() {
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
            for(Point firstTarget : adjacentPositions) {
                for(Point secondTarget : adjacentPositions) {
                    args.add(Arguments.of(start, firstTarget, secondTarget));
                }
            }
        }

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleTarget")
    @DisplayName("Test an invalid construction with a GodCard of type Demeter")
    public void testConstructionConstraint(Point start, Point firstTarget, Point secondTarget) {
        Board board = game.getBoard();

        try {
            board.place(game.getCurrentPlayer().getCurrentWorker(), start);
        } catch (InvalidPositionException | BoxFullException e) {
            e.printStackTrace();
        }

        game.setCurrentPhase(demeter.computeNextPhase(game));
        game.setCurrentPhase(demeter.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[0], firstTarget);
        assertTrue(demeter.validate(demeter.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(demeter.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction secondConstruction = new Construction(board, Block.blocks[0], secondTarget);
        if(secondTarget.equals(firstTarget)) {
            secondConstruction = new Construction(board, Block.blocks[1], secondTarget);
        }

        assertEquals(!secondTarget.equals(firstTarget), demeter.validate(demeter.computeBuildablePoints(), secondConstruction));
    }

}
