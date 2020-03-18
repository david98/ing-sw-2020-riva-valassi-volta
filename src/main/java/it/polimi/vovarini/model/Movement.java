package it.polimi.vovarini.model;

public class Movement extends Move {
    private int startX;
    private int startY;

    private int endX;
    private int endY;

    public Movement(int startX, int startY, int endX, int endY){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public Move reverse() {
        return new Movement(endX, endY, startX, startY);
    }

}
