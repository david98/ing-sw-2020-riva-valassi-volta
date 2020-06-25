package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.moves.Movement;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ApolloTests {
    private Game game;
    private GodCard apollo;

    @BeforeEach
    public void init(){
        try{
            game = new Game(2);

            game.addPlayer("Guest01");
            game.addPlayer("Guest02");

            apollo = GodCardFactory.create(GodName.Apollo);
            apollo.setGameData(game);
            for (Player player: game.getPlayers()){
                player.setGodCard(apollo);
            }
        } catch (InvalidNumberOfPlayersException e){
            e.printStackTrace();
        }
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
            for(Point end : startAdjacentPositions) {
                    args.add(Arguments.of(start, end));
            }
        }

        return args.stream();
    }

    @Test
    @DisplayName("Test that a GodCard of type Apollo can be instantiated correctly")
    public void apolloCreation() {
        assertEquals(GodName.Apollo, game.getCurrentPlayer().getGodCard().name);
    }

    @ParameterizedTest
    @MethodSource("provideAllPossibleMovementMoves")
    @DisplayName("Test that Apollo's movement conditions are correctly applied")
    public void testMovementConstraint(Point start, Point end) {

        Board board = game.getBoard();

        try {
            board.place(game.getCurrentPlayer().getCurrentWorker(), start);
            board.place(game.getPlayers()[1].getCurrentWorker(), end);
        } catch (InvalidPositionException | BoxFullException ignored) {
        }

        game.setCurrentPhase(Phase.Movement);

        Movement movement = new Movement(board, start, end);
        assertTrue(apollo.validate(apollo.computeReachablePoints(), movement));
        game.performMove(movement);

        assertEquals(game.getCurrentPlayer().getCurrentWorker(), board.getBox(end).getItems().peek());
        assertEquals(game.getPlayers()[1].getCurrentWorker(), board.getBox(start).getItems().peek());

    }


    
}
