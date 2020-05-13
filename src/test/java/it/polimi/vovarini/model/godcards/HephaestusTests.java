package it.polimi.vovarini.model.godcards;

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

public class HephaestusTests {

    private static Game game;
    private static GodCard hephaestus;

    @BeforeAll
    public static void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            hephaestus = GodCardFactory.create(GodName.Hephaestus);
            hephaestus.setGameData(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(hephaestus);
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
                while (b.remove(cur) != null);
            }
        }
        game.setCurrentPhase(Phase.Start);
        game.getCurrentPlayer().getConstructionList().clear();
        hephaestus.constructionConstraints.clear();
    }

    private static Stream<Arguments> provideAllPossibleTargetAndLevel() {
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
                    for (int lTarget = 0; lTarget < Block.MAX_LEVEL-1; lTarget++) {
                        args.add(Arguments.of(start, firstTarget, secondTarget, lTarget));
                    }
                }
            }
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Hephaestus can be instantiated correctly")
    public void hephaestusCreation() {
        assertEquals(game.getCurrentPlayer().getGodCard().name, GodName.Hephaestus);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleTargetAndLevel")
    @DisplayName("Test that Hephaestus' construction constraints are correctly applied")
    public void validSecondConstruction(Point start, Point firstTarget, Point secondTarget, int lTarget) {
        Board board = game.getBoard();

        try {
            for(int i = 0; i < lTarget; i++) {
                board.place(Block.blocks[i], firstTarget);
            }
            board.place(game.getCurrentPlayer().getCurrentWorker(), start);
        } catch (InvalidPositionException ignored) {
        } catch (BoxFullException ignored) {
        }

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction firstConstruction = new Construction(board, Block.blocks[lTarget], firstTarget);
        assertTrue(hephaestus.validate(hephaestus.computeBuildablePoints(), firstConstruction));
        game.performMove(firstConstruction);

        game.setCurrentPhase(hephaestus.computeNextPhase(game));
        assertEquals(game.getCurrentPhase(), Phase.Construction);

        Construction secondConstruction = new Construction(board, Block.blocks[lTarget+1], secondTarget);
        assertEquals(firstTarget.equals(secondTarget) && lTarget + 2 != Block.MAX_LEVEL, hephaestus.validate(hephaestus.computeBuildablePoints(), secondConstruction));
    }

}
