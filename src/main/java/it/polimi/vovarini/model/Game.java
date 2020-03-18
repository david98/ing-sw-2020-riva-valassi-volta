package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.Worker;
import it.polimi.vovarini.model.board.items.Block;

import java.util.Stack;

public class Game {
    private Player[] players;
    private int currentPlayerIndex;
    private Board board;

    private Stack<Move> moves;


    public moveWorker(int x, int y){

    }

    public buildBlock(Block block, int x, int y){

    }

    public undoLastMove(){
        performMove(moves.pop().reverse());
    }

    public Player getCurrentPlayer(){
        return players[currentPlayerIndex];
    }

    public Player nextPlayer(){
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.length){
            currentPlayerIndex = 0;
        }
        return players[currentPlayerIndex];
    }

    private void performMove(Move move){

    }
}
