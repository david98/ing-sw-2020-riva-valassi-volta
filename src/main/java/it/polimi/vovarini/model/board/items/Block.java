package it.polimi.vovarini.model.board.items;

public class Block extends Item{
    private int level;

    public Block(int level) throws InvalidLevelException{
        if (level < 1 || level > 4){
            throw new InvalidLevelException();
        }
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean canBePlacedOn(Item item){
        if (item instanceof Block) {
            Block block = (Block) item;
            return level == (block.getLevel() + 1);
        } else {
            return super.canBePlacedOn(item);
        }
    }

    @Override
    public String toString() {
        return "" + level;
    }
}