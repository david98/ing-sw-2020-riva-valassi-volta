package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.items.Worker;

public class Movement extends Move {

    private Worker worker;

    private Point start;

    private Point end;

    public Movement(Worker worker, Point start, Point end){
        this.worker = worker;
        this.start = new Point(start);
        this.end = new Point(end);
    }

    @Override
    public Move reverse() {
        return new Movement(worker, end, start);
    }
}
