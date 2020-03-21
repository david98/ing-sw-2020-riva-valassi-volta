package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.Apollo;

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
            players[i] = new Player(this, new Apollo(this), "Player" + i); //TODO: sistemare
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
            Game game = new Game(2);
            Player player = game.getCurrentPlayer();
            player.setCurrentSex(Sex.Male);
            Worker maleWorker = player.getCurrentWorker();
            player.setCurrentSex(Sex.Female);
            Worker femaleWorker = player.getCurrentWorker();

            Player other = game.nextPlayer();
            System.out.println(game.getCurrentPlayer().getNickname());
            other.setCurrentSex(Sex.Male);
            Worker otherMaleWorker = other.getCurrentWorker();
            other.setCurrentSex(Sex.Female);
            Worker otherFemaleWorker = other.getCurrentWorker();

            game.nextPlayer();

            Board board = game.getBoard();

            board.place(maleWorker, new Point (0,0));
            board.place(femaleWorker, new Point (4, 4));
            board.place(otherMaleWorker, new Point(4, 3));
            board.place(otherFemaleWorker, new Point(3, 4));
            board.debugPrintToConsole();
            for (Point p: player.getGodCard().computeReachablePoints()
                 ) {
                System.out.println(p);
            }
            player.moveCurrentWorker(new Point(4, 3));
            board.debugPrintToConsole();

        } catch (InvalidPositionException e){
            System.err.println("Invalid position.");
        } catch (BoxFullException e){
            System.err.println("Box is full.");
        }
    }
}