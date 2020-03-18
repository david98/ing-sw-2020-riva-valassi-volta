package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.items.Block;

public class Construction extends Move {

    private Block block;

    private int x;
    private int y;

    public Construction(Block block, int x, int y){
        this.block = block;
        this.x = x;
        this.y = y;
    }

    @Override
    public Move reverse() {
        return new Destruction(block, x, y);
    }
}
