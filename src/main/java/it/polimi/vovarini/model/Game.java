package it.polimi.vovarini.model;

import it.polimi.vovarini.common.events.CurrentPlayerChangedEvent;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.events.NewPlayerEvent;
import it.polimi.vovarini.common.events.PhaseUpdateEvent;
import it.polimi.vovarini.common.exceptions.*;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Move;
import it.polimi.vovarini.model.moves.Movement;

import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {

  public static final int MIN_PLAYERS = 2;
  public static final int MAX_PLAYERS = 3;

  private Player[] players;
  private int currentPlayerIndex;

  private Phase currentPhase;

  public Board getBoard() {
    return board;
  }

  private Board board;

  private Stack<Move> moves;
  private Stack<Move> undoneMoves;

  public Game(int numberOfPlayers) throws InvalidNumberOfPlayersException {
    if (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
      throw new InvalidNumberOfPlayersException();
    }

    players = new Player[numberOfPlayers];

    currentPlayerIndex = 0;

    moves = new Stack<>();
    undoneMoves = new Stack<>();

    board = new Board(Board.DEFAULT_SIZE);

    currentPhase = Phase.Start;
  }

  /**
   * This method adds a new player into the game with the nickname already
   * validated through {@link Player#validateNickname(String)}
   *
   * @param nickname the name of the player to be added
   * @throws InvalidNumberOfPlayersException if there is already the maximum number of players
   */
  public void addPlayer(String nickname)
      throws InvalidNumberOfPlayersException {

    if (players[players.length - 1] != null) {
      throw new InvalidNumberOfPlayersException();
    }

    Player player = new Player(nickname);

    for (int i = 0; i < players.length; i++) {
      if (players[i] == null) {
        players[i] = player;
        player.setGodCard(GodCardFactory.create(GodName.Nobody)); // MERDA PER TEST!!!
        player.getGodCard().setGame(this);                        // RIPETO MERDA PER TEST!!!
        GameEventManager.raise(new NewPlayerEvent(this, player));
        return;
      }
    }
  }

  public void performMove(Movement move) {

    undoneMoves.clear();
    moves.push(move);
    getCurrentPlayer().getMovementList().add(move);

    for(Move executableMove : getCurrentPlayer().getGodCard().consequences(move)){
      executableMove.execute();
    }

  }


  public void performMove (Construction move){

    undoneMoves.clear();
    moves.push(move);
    getCurrentPlayer().getConstructionList().add(move);

    for(Move executableMove : getCurrentPlayer().getGodCard().consequences(move)){
      executableMove.execute();
    }

  }
  public Phase getCurrentPhase() {
    return currentPhase;
  }

  public Phase nextPhase() {
    setCurrentPhase(currentPhase.next());
    return currentPhase;
  }

  public Player[] getPlayers() {
    return players;
  }

  public Player getCurrentPlayer() {
    return players[currentPlayerIndex];
  }

  public Player nextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    GameEventManager.raise(new CurrentPlayerChangedEvent(this, players[currentPlayerIndex].clone()));
    return players[currentPlayerIndex];
  }

  public void setCurrentPhase(Phase phase){
    this.currentPhase = phase;
    GameEventManager.raise(new PhaseUpdateEvent(this, phase));
  }

  // needs to manage turn flow
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

  public boolean isFull(){
    return Arrays.stream(players).noneMatch(Objects::isNull);
  }
}
