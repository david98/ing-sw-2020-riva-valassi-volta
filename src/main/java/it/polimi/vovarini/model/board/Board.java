package it.polimi.vovarini.model.board;

import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Item;

public class Board {

    public static final int DEFAULT_SIZE = 5;

    private Box[][] boxes;


    /*
    * Si presuppone che la plancia sia quadrata
    * */
    public Board(int size){
        boxes = new Box[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                boxes[i][j] = new Box();
            }
        }
    }

    public void place(Item item, Point p) throws InvalidPositionException, IncompatibleItemsException,
            BoxFullException{
        if (p.getX() >= boxes.length || p.getY() >= boxes[0].length){
            throw new InvalidPositionException();
        }
        Box box = boxes[p.getY()][p.getX()];
        box.place(item);
    }

    public Item remove(Point p) throws InvalidPositionException, BoxEmptyException{
        if (p.getX() >= boxes.length || p.getY() >= boxes[0].length){
            throw new InvalidPositionException();
        }
        Box box = boxes[p.getY()][p.getX()];
        return box.removeTopmost();
    }

    public Point getItemPosition(Item item) throws ItemNotFoundException{
        for (int i = 0; i < boxes.length; i++){
            for (int j = 0; j < boxes.length; j++){
                try {
                    if (boxes[j][i].getTopmost().equals(item)) {
                        return new Point(i, j);
                    }
                } catch (BoxEmptyException ignored){
                }
            }
        }
        throw new ItemNotFoundException();
    }

    public void debugPrintToConsole(){
        for (int i = 0; i < boxes.length; i++){
            for (int j = 0; j < boxes.length; j++){
                System.out.print(boxes[i][j].toString());
            }
            System.out.print("\n");
        }
    }
}
