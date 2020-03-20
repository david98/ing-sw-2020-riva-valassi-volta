package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

public class Movement extends Move {

    private Board board;
    private Point start;
    private Point end;

    public Movement(Board board, Point start, Point end){
        this.board = board;
        this.start = new Point(start);
        this.end = new Point(end);
    }

    @Override
    public Move reverse() {
        return new Movement(board, end, start);
    }

    @Override
    public void execute() {
        try {
            Item startItem = board.remove(start);
            //TODO: gestire Apollo e divinità che fanno scambiare posizione
            board.place(startItem, end);
        } catch (BoxEmptyException e){
            System.err.println("Start was empty.");
        } catch (InvalidPositionException e){
            System.err.println("Invalid start/end");
        } catch (BoxFullException e){
            System.err.println("End box is full.");
        } catch (IncompatibleItemsException e){
            System.err.println("Cannot move because of an incompatibility.");
        }
    }
}
