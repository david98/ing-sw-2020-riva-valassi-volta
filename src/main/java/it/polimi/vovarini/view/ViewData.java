package it.polimi.vovarini.view;

import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Worker;

import java.io.Serializable;

/**
 * Data that is needed by any kind of view.
 *
 * @author Davide Volta
 */
public class ViewData implements Serializable {
  private Player owner;
  private Player currentPlayer;
  private Player[] players;
  private Phase currentPhase;

  private Board board;

  private Worker selectedWorker;
  private Point currentStart;

  public ViewData(){
    currentPhase = Phase.Start;
  }

  public Phase getCurrentPhase() {
    return currentPhase;
  }

  public void setCurrentPhase(Phase currentPhase) {
    this.currentPhase = currentPhase;
  }

  public Player getOwner() {
    return owner;
  }

  public void setOwner(Player owner) {
    this.owner = owner;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public Player[] getPlayers() {
    return players;
  }

  public void setPlayers(Player[] players) {
    this.players = players;
  }

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public Worker getSelectedWorker() {
    return selectedWorker;
  }

  public void setSelectedWorker(Worker selectedWorker) {
    this.selectedWorker = selectedWorker;
  }

  public Point getCurrentStart() {
    return currentStart;
  }

  public void setCurrentStart(Point currentStart) {
    this.currentStart = currentStart;
  }
}
