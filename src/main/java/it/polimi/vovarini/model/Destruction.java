package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;

public class Destruction extends Move {

    private Block block;

    private int x;
    private int y;

    public Destruction(Board board, Block block, int x, int y){
        super(board);
        this.block = block;
        this.x = x;
        this.y = y;
    }

    @Override
    public Move reverse() {
        return new Construction(board, block, x, y);
    }

    @Override
    public void execute() {

    }
}
