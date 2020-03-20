package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.board.items.Block;

import java.util.EmptyStackException;
import java.util.Stack;

public class Game {
    private Player[] players;
    private int currentPlayerIndex;

    public Board getBoard() {
        return board;
    }

    private Board board;

    private Stack<Move> moves;
    private Stack<Move> undoneMoves;

    public Game(int numberOfPlayers){
        players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++){
            players[i] = new Player(this, null, "Player"); //TODO: sistemare
        }
        moves = new Stack<>();
        undoneMoves = new Stack<>();
        currentPlayerIndex = 0;
        board = new Board(Board.DEFAULT_SIZE);
    }

    public void performMove(Move move){
        undoneMoves.clear();
        moves.push(move);
        move.execute();
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Player getCurrentPlayer(){
        return players[currentPlayerIndex];
    }

    public void undoLastMove(){
        try {
            Move opposite = moves.pop().reverse();
            undoneMoves.push(opposite);
            opposite.execute();
        } catch (EmptyStackException ignored) {

        }
    }

    public void redoMove(){
        try {
            Move move = undoneMoves.pop().reverse();
            moves.push(move);
            move.execute();
        } catch (EmptyStackException ignored){

        }
    }

    public Player nextPlayer(){
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.length){
            currentPlayerIndex = 0;
        }
        return players[currentPlayerIndex];
    }

    public static void main(String[] args){
        try {
            Game game = new Game(1);
            Player player = game.getCurrentPlayer();
            player.setCurrentSex(Sex.Male);
            Worker maleWorker = player.getCurrentWorker();
            player.setCurrentSex(Sex.Female);
            Worker femaleWorker = player.getCurrentWorker();

            Board board = game.getBoard();

            board.place(maleWorker, new Point (0,0));
            board.place(femaleWorker, new Point (4, 4));
            board.debugPrintToConsole();

            player.moveCurrentWorker(new Point(3, 3));
            player.moveCurrentWorker(new Point(2, 1));
            board.debugPrintToConsole();
            game.undoLastMove();
            game.undoLastMove();
            player.moveCurrentWorker(new Point(1, 2));
            game.redoMove();
            game.redoMove();
            game.redoMove();
            board.debugPrintToConsole();

        } catch (InvalidPositionException e){
            System.err.println("Invalid position.");
        } catch (BoxFullException e){
            System.err.println("Box is full.");
        } catch (IncompatibleItemsException e){
            System.err.println("Items are incompatible");
        }
    }
}
