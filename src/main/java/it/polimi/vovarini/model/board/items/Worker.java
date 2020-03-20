package it.polimi.vovarini.model.board.items;


public class Worker extends Item {
    private Sex sex;

    public Worker(Sex sex){
        this.sex = sex;
    }

    public boolean canBePlacedOn(Item item){
        if (item instanceof Block){
            Block block = (Block) item;
            return block.getLevel() < 4;
        } else { //TODO: gestire il caso in cui ci siano carte divinità
            return super.canBePlacedOn(item);
        }
    }

    @Override
    public String toString() {
        return sex == Sex.Male ? "M" : "F";
    }
}
