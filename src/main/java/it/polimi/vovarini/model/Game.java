package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.*;
import it.polimi.vovarini.model.godcards.Apollo;
import it.polimi.vovarini.model.godcards.Nobody;

import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.Stack;

public class Game {
    private Player[] players;
    private int currentPlayerIndex;


    public Board getBoard() {
        return board;
    }

    private Board board;

    private Stack<Move> moves;
    private Stack<Move> undoneMoves;

    public Game(int numberOfPlayers){
        players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++){
            players[i] = new Player(this, new Apollo(this), "Player" + i); //TODO: sistemare
        }
        moves = new Stack<>();
        undoneMoves = new Stack<>();
        currentPlayerIndex = 0;
        board = new Board(Board.DEFAULT_SIZE);
    }

    public void performMove(Move move){
        undoneMoves.clear();
        moves.push(move);
        move.execute();
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Player getCurrentPlayer(){
        return players[currentPlayerIndex];
    }

    //return 0 se tutto va bene
    //altri valori in base al tipo di eccezione, in modo da poter richiedere il necessario all'utente una volta che ha sbagliato
    public int startingBoardConfig(int i, Scanner input, Sex sex){

        System.out.println(players[i].getNickname() + ", inserisci le Coordinate del tuo " + sex.toString()+"Worker:");
        String numberList = input.nextLine();
        String[] numberVector = numberList.split(",");
        Point newPoint = new Point (Integer.parseInt(numberVector[0]), Integer.parseInt(numberVector[1]));
        players[i].setCurrentSex(sex);
        try {
            board.place(players[i].getCurrentWorker(), newPoint);
        }
        catch  (InvalidPositionException e){
            System.err.println("Attenzione! Hai inserito una posizione non valida!");
            return 1;
        }
        catch (BoxFullException e){
            System.err.println("Attenzione! La casella selezionata è già piena!");
            return 2;
        }
        /*catch (OverwritedWorkerException e){
            System.err.println("Attenzione! Non puoi rimpiazzare un Worker avversario!");
            return 3;
        }*/

        return 0;
    }

    public int turn(Scanner input){


        players[currentPlayerIndex].setCurrentPhase(Phase.StartPhase);
        if (players[currentPlayerIndex].getGodCard().computeReachablePoints().isEmpty()) {
            players[currentPlayerIndex].setCurrentSex(players[currentPlayerIndex].getOtherWorker().getSex());
            if (players[currentPlayerIndex].getGodCard().computeReachablePoints().isEmpty()){
                return -1;
            }
        }

        players[currentPlayerIndex].setCurrentPhase(Phase.MovementPhase);
        System.out.println(players[currentPlayerIndex].getNickname() + ", seleziona il tuo Worker inserendo M o F:");
        String workerChar = input.nextLine();
        switch (workerChar){
            case "M": {
                players[currentPlayerIndex].setCurrentSex(Sex.Male);
                break;
            }
            case "F": {
                players[currentPlayerIndex].setCurrentSex(Sex.Female);
                break;
            }
        }
        boolean validPoint = false;
        while(!validPoint){
            System.out.println(players[currentPlayerIndex].getNickname() + ", inserisci le Coordinate del tuo " + players[currentPlayerIndex].getCurrentWorker().getSex().toString()+"Worker:");
            String numberList = input.nextLine();
            String[] numberVector = numberList.split(",");
            Point newPoint = new Point (Integer.parseInt(numberVector[0]), Integer.parseInt(numberVector[1]));
            for (Point point : players[currentPlayerIndex].getGodCard().computeReachablePoints()){
                if (point.equals(newPoint)) validPoint = true;
            }
            if (!validPoint) System.out.println("La posizione selezionata non è valida! Reinserisci!");
        }



        return 0;

    }

    public void undoLastMove(){
        try {
            Move opposite = moves.pop().reverse();
            undoneMoves.push(opposite);
            opposite.execute();
        } catch (EmptyStackException ignored) {

        }
    }

    public void redoMove(){
        try {
            Move move = undoneMoves.pop().reverse();
            moves.push(move);
            move.execute();
        } catch (EmptyStackException ignored){

        }
    }

    public Player nextPlayer(){
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.length){
            currentPlayerIndex = 0;
        }
        return players[currentPlayerIndex];
    }

    public static void main(String[] args){

            String cleaner;
            Scanner input = new Scanner(System.in);
            System.out.println("Benvenuti a Santorini! Inserire il numero di Giocatori:");
            int numberOfPlayers = input.nextInt();
            Game game = new Game (numberOfPlayers);
            input.nextLine();
            for (int i = 0; i < game.players.length; i++){
                System.out.println("Inserisci il Nickname del Giocatore "+(i+1)+":");
                String nickname = input.nextLine();
                game.players[i] = new Player(game, new Nobody(game), nickname);
            }
            Board board = game.getBoard();
            //qui va assegnato anche il colore. Marco se ne sta occupando, vedremo quando integrare quella parte
            for (int i = 0; i < game.players.length; i++){

                while (game.startingBoardConfig(i, input, Sex.Male) != 0){};
                while (game.startingBoardConfig(i, input, Sex.Female) != 0){};
            }
            System.out.println("Siamo pronti per giocare! Inizia " + game.getCurrentPlayer().getNickname() + "!");
            switch (game.turn(input)){
                case -1: {
                    System.out.println(game.getCurrentPlayer().getNickname()+" ha perso! Questo significa che la vittoria è di " + game.nextPlayer().getNickname()+"!");
                }
                case 0:{
                    game.nextPlayer();
                }
            }

            board.debugPrintToConsole();


/*            Player player = game.getCurrentPlayer();
            player.setCurrentSex(Sex.Male);
            Worker maleWorker = player.getCurrentWorker();
            player.setCurrentSex(Sex.Female);
            Worker femaleWorker = player.getCurrentWorker();

            Player other = game.nextPlayer();
            System.out.println(game.getCurrentPlayer().getNickname());
            other.setCurrentSex(Sex.Male);
            Worker otherMaleWorker = other.getCurrentWorker();
            other.setCurrentSex(Sex.Female);
            Worker otherFemaleWorker = other.getCurrentWorker();

            game.nextPlayer();





            board.place(maleWorker, new Point (0,0));
            board.place(femaleWorker, new Point (4, 4));
            board.place(otherMaleWorker, new Point(4, 3));
            board.place(otherFemaleWorker, new Point(3, 4));
            board.debugPrintToConsole();
            for (Point p: player.getGodCard().computeReachablePoints()
                 ) {
                System.out.println(p);
            }
            player.moveCurrentWorker(new Point(4, 3));
            board.debugPrintToConsole();*/


    }
}
