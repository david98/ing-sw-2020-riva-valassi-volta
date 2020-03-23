package it.polimi.vovarini.model.board.items;

public abstract class Item {

    //protected int level;

    public boolean canBePlacedOn(Item item){
        return false;
    }
    public boolean canBeRemoved() { return false; }

    /*public int getLevel(){
        return level;
    }*/
}
