package it.polimi.vovarini.model;

import it.polimi.vovarini.Observable;
import it.polimi.vovarini.Observer;
import it.polimi.vovarini.model.board.*;
import it.polimi.vovarini.model.board.items.*;
import it.polimi.vovarini.model.godcards.Apollo;
import it.polimi.vovarini.model.godcards.Nobody;

import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.Stack;

public class Game implements Observable {
  private Player[] players;
  private int currentPlayerIndex;

  private Phase currentPhase;

  public Board getBoard() {
    return board;
  }

  private Board board;

  private Stack<Move> moves;
  private Stack<Move> undoneMoves;

  public Game(int numberOfPlayers) {
    players = new Player[numberOfPlayers];
    for (int i = 0; i < numberOfPlayers; i++) {
      players[i] = new Player(this, new Apollo(this), "Player" + i); // TODO: sistemare
    }
    currentPlayerIndex = 0;

    moves = new Stack<>();
    undoneMoves = new Stack<>();

    board = new Board(Board.DEFAULT_SIZE);

    currentPhase = Phase.Start;
  }

  public void performMove(Move move) {
    undoneMoves.clear();
    moves.push(move);
    move.execute();
  }

  public Phase getCurrentPhase() {
    return currentPhase;
  }

  public Phase nextPhase(){
    currentPhase = currentPhase.next();
    return currentPhase;
  }

  public Player[] getPlayers() {
    return players;
  }

  public Player getCurrentPlayer() {
    return players[currentPlayerIndex];
  }

  public Player nextPlayer() {
    currentPhase = Phase.Start;
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    return players[currentPlayerIndex];
  }

  public void undoLastMove() {
    try {
      Move opposite = moves.pop().reverse();
      undoneMoves.push(opposite);
      opposite.execute();
    } catch (EmptyStackException ignored) {

    }
  }

  public void redoMove() {
    try {
      Move move = undoneMoves.pop().reverse();
      moves.push(move);
      move.execute();
    } catch (EmptyStackException ignored) {

    }
  }

  @Override
  public void add(Observer observer) {

  }

  @Override
  public void remove(Observer observer) {

  }

  @Override
  public void notifyObservers() {

  }

  // qui iniziano i metodi di MERDA

  // return 0 se tutto va bene
  // altri valori in base al tipo di eccezione, in modo da poter richiedere il necessario all'utente
  // una volta che ha sbagliato
  public int startingBoardConfig(int i, Scanner input, Sex sex) {

    System.out.println(
        players[i].getNickname()
            + ", inserisci le Coordinate del tuo "
            + sex.toString()
            + "Worker:");
    String numberList = input.nextLine();
    String[] numberVector = numberList.split(",");
    Point newPoint =
        new Point(Integer.parseInt(numberVector[0]), Integer.parseInt(numberVector[1]));
    players[i].setCurrentSex(sex);
    try {
      board.place(players[i].getCurrentWorker(), newPoint);
    } catch (InvalidPositionException e) {
      System.err.println("Attenzione! Hai inserito una posizione non valida!");
      return 1;
    } catch (BoxFullException e) {
      System.err.println("Attenzione! La casella selezionata è già piena!");
      return 2;
    }

    return 0;
  }

  public Point isValidReachablePoint(Scanner input) {
    boolean validPoint = false;
    while (!validPoint) {
      System.out.println(
          players[currentPlayerIndex].getNickname()
              + ", inserisci le Coordinate del tuo "
              + players[currentPlayerIndex].getCurrentWorker().getSex().toString()
              + "Worker:");
      String numberList = input.nextLine();
      String[] numberVector = numberList.split(",");
      Point newPoint =
          new Point(Integer.parseInt(numberVector[0]), Integer.parseInt(numberVector[1]));
      for (Point point : players[currentPlayerIndex].getGodCard().computeReachablePoints()) {
        if (point.equals(newPoint)) return newPoint;
      }
      if (!validPoint) System.out.println("La posizione selezionata non è valida! Reinserisci!");
    }
    return new Point(-1, -1);
  }

  public Point isValidBuildablePoint(Scanner input) {
    boolean validPoint = false;
    while (!validPoint) {
      System.out.println(
          players[currentPlayerIndex].getNickname()
              + ", inserisci le Coordinate del tuo "
              + players[currentPlayerIndex].getCurrentWorker().getSex().toString()
              + "Worker:");
      String numberList = input.nextLine();
      String[] numberVector = numberList.split(",");
      Point newPoint =
          new Point(Integer.parseInt(numberVector[0]), Integer.parseInt(numberVector[1]));
      for (Point point : players[currentPlayerIndex].getGodCard().computeBuildablePoints()) {
        if (point.equals(newPoint)) return newPoint;
      }
      if (!validPoint) System.out.println("La posizione selezionata non è valida! Reinserisci!");
    }
    return new Point(-1, -1);
  }

  public int turn(Scanner input) {

    if (players[currentPlayerIndex].getGodCard().computeReachablePoints().isEmpty()) {
      players[currentPlayerIndex].setCurrentSex(
          players[currentPlayerIndex].getOtherWorker().getSex());
      if (players[currentPlayerIndex].getGodCard().computeReachablePoints().isEmpty()) {
        return -1;
      }
    }

    nextPhase();
    System.out.println(
        players[currentPlayerIndex].getNickname() + ", seleziona il tuo Worker inserendo M o F:");
    String workerChar = input.nextLine();
    switch (workerChar) {
      case "M":
        {
          players[currentPlayerIndex].setCurrentSex(Sex.Male);
          break;
        }
      case "F":
        {
          players[currentPlayerIndex].setCurrentSex(Sex.Female);
          break;
        }
      default:
        System.err.println("Non esiste altro sesso!");
    }

    Point newPoint = isValidReachablePoint(input);
    Point start = new Point(-1, -1);
    try {
      start = board.getItemPosition(players[currentPlayerIndex].getCurrentWorker());
    }
    // Da riempire
    catch (ItemNotFoundException ignored) {

    }

    Movement movement = new Movement(board, start, newPoint);
    movement.execute();

    nextPhase();
    players[currentPlayerIndex].getGodCard().checkWin(movement);

    nextPhase();
    Construction build;
    newPoint = isValidBuildablePoint(input);
    Block topItem;
    try {
      topItem = (Block) board.getTopmostItem(newPoint);
      build = new Construction(board, topItem, newPoint);
      build.execute();
    } catch (InvalidPositionException | BoxEmptyException ignored) {

    }
    nextPhase();
    players[currentPlayerIndex].getGodCard().consequences(this);

    nextPhase();
    nextPlayer();

    return 0;
  }

  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);
    System.out.println("Benvenuti a Santorini! Inserire il numero di Giocatori:");
    int numberOfPlayers = input.nextInt();

    Game game = new Game(numberOfPlayers);

    input.nextLine();
    for (int i = 0; i < game.players.length; i++) {
      System.out.println("Inserisci il Nickname del Giocatore " + (i + 1) + ":");
      String nickname = input.nextLine();
      game.players[i] = new Player(game, new Nobody(game), nickname);
    }

    Board board = game.getBoard();
    /*
      qui va assegnato anche il colore. Marco se ne sta occupando, vedremo quando integrare
      quella parte
     */
    for (int i = 0; i < game.players.length; i++) {

      while (game.startingBoardConfig(i, input, Sex.Male) != 0) {}
      while (game.startingBoardConfig(i, input, Sex.Female) != 0) {}
    }

    System.out.println(
        "Siamo pronti per giocare! Inizia " + game.getCurrentPlayer().getNickname() + "!");
    switch (game.turn(input)) {
      case -1:
        {
          System.out.println(
              game.getCurrentPlayer().getNickname()
                  + " ha perso! Questo significa che la vittoria è di "
                  + game.nextPlayer().getNickname()
                  + "!");
        }
      case 0:
        {
          game.nextPlayer();
        }
      default:
        {
          System.err.println("ERROR: risultato impossibile. Stato della partita indeterminabile");
        }
    }

    board.debugPrintToConsole();
  }
}
