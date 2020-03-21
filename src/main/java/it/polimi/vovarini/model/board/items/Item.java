package it.polimi.vovarini.model.board.items;

public abstract class Item {
    public boolean canBePlacedOn(Item item){
        return false;
    }
    public boolean canBeRemoved() { return false; }
}