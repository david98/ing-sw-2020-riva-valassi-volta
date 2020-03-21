package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;

public class Construction extends Move {

    private Block block;

    private int x;
    private int y;

    public Construction(Board board, Block block, int x, int y){
        super(board);
        this.block = block;
        this.x = x;
        this.y = y;
    }

    @Override
    public Move reverse() {
        return new Destruction(board, block, x, y);
    }

    @Override
    public void execute() {

    }
}
