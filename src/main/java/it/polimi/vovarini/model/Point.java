package it.polimi.vovarini.model;

public class Point {
    private int x;

    private int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point(Point p){
        x = p.getX();
        y = p.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
