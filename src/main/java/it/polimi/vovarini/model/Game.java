package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.godcards.Apollo;
import it.polimi.vovarini.model.godcards.Athena;
import it.polimi.vovarini.model.godcards.GodCard;

import java.util.Scanner;
import java.util.Stack;

public class Game {
    private Player[] players;
    private int currentPlayerIndex;
    private Board board;
    private Stack<Move> moves;

    public Game(int numberOfPlayers){
        players = new Player[numberOfPlayers];
        currentPlayerIndex = 0;
        board = new Board(Board.DEFAULT_SIZE);

    }

    public void moveWorker(int x, int y){
        Worker worker = getCurrentPlayer().getCurrentWorker();
    }

    public void buildBlock(Block block, int x, int y){

    }

    public void undoLastMove(){
        performMove(moves.pop().reverse());
    }

    public Player getCurrentPlayer(){
        return players[currentPlayerIndex];
    }

    public Player nextPlayer(){
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.length){
            currentPlayerIndex = 0;
        }
        return players[currentPlayerIndex];
    }

    private void performMove(Move move){

    }

    private boolean checkWin(){ return false; };
    private boolean checkLoss(){ return false; };

    public static void main(String[] args){
        try {

            GodCard cardOne = new Apollo();
            GodCard cardTwo = new Athena();
            Game newGame = new Game(2);
            newGame.players[0] = new Player(cardOne, "playerOne");
            newGame.players[1] = new Player(cardTwo, "playerTwo");
            Block level1Block = new Block(1);
            Block level2Block = new Block(2);
            Block level3Block = new Block(3);
            Block level4Block = new Block(4);
            Worker maleWorker = new Worker(Sex.Male);
            Worker femaleWorker = new Worker(Sex.Female);
            Scanner scan = new Scanner(System.in);

            while (newGame.checkLoss() || newGame.checkWin()){
                switch (newGame.getCurrentPlayer().getCurrentPhase()){
                    case WaitPhase: {
                        if (newGame.players[0].getCurrentPhase().equals(Phase.WaitPhase) && newGame.players[1].getCurrentPhase().equals(Phase.WaitPhase)){
                            newGame.getCurrentPlayer().setCurrentPhase(Phase.StartPhase);
                        }
                        else {
                            newGame.nextPlayer();
                            newGame.getCurrentPlayer().setCurrentPhase(Phase.StartPhase);
                        }
                        break;
                    }

                    case StartPhase: {
                        //notifyTurnStarted(newGame.getCurrentPlayer());
                        System.out.println("Inizia il turno di" + newGame.getCurrentPlayer().nickname + ":");
                        newGame.getCurrentPlayer().currentPhase = Phase.ApplyGCPhase;
                        break;
                    }

                    case ApplyGCPhase: {
                        newGame.getCurrentPlayer().card.applyEffect();
                        newGame.getCurrentPlayer().currentPhase = Phase.MovementPhase;
                    }

                    case MovementPhase: {
                        System.out.println("Inserisci le coordinate della cella su cui vuoi muoverti:");
                        
                    }
                }
            }

            Board board = new Board(Board.DEFAULT_SIZE);
            board.place(level1Block, new Point(0, 0));
            board.debugPrintToConsole();
            board.place(maleWorker, new Point(2, 4));
            board.debugPrintToConsole();
            System.out.println(board.getItemPosition(maleWorker).toString());

        } catch (InvalidLevelException e){
            System.err.println("Invalid level provided.");
        } catch (InvalidPositionException e){
            System.err.println("Invalid position.");
        } catch (BoxFullException e){
            System.err.println("Box is full.");
        } catch (IncompatibleItemsException e){
            System.err.println("Items are incompatible");
        } catch (ItemNotFoundException e){
            System.err.println("Item not found.");
        }
    }
}
