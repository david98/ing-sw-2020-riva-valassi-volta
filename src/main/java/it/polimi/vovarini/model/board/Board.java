package it.polimi.vovarini.model.board;

public class Board {
    private Box[][] boxes;

    public Board(int size){
        boxes = new Box[size][size];
    }
}
