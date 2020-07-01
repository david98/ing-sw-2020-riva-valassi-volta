package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.Player;

/**
 * This class represents an abstract View and handles
 * generic View behavior, such as updating the Board,
 * updating the current player, etc.
 *
 * @author Davide Volta
 */
public abstract class View implements EventsForViewListener {

  protected GameClient client;

  protected ViewData data;

  /**
   * This method builds an instance of View
   */
  public View() {
    GameEventManager.bindListeners(this);
    data = new ViewData();
  }

  /**
   * This methods handles a NewPlayerEvent, adding the player to the data of this View
   * @param e is the NewPlayerEvent to handle
   */
  @Override
  public void handle(NewPlayerEvent e) {
    client.setSocketTimeout(0);
    if (data.getOwner().equals(e.getNewPlayer())) {
      data.setOwner(e.getNewPlayer());
    }
    data.addPlayer(e.getNewPlayer());
  }

  /**
   * This methods handles a PlayerInfoUpdateEvent, updating the information of the player linked to the event inside the data of this View
   * @param e is the PlayerInfoUpdateEvent to handle
   */

  @Override
  public void handle(PlayerInfoUpdateEvent e) {
    if (e.getTargetPlayer().getGodCard() != null) {
      e.getTargetPlayer().getGodCard().setGameData(data);
    }
    if (data.getOwner().equals(e.getTargetPlayer())) {
      data.setOwner(e.getTargetPlayer());
    }
    if (data.getCurrentPlayer().equals(e.getTargetPlayer())) {
      data.setCurrentPlayer(e.getTargetPlayer());
    }
    for (int i = 0; i < data.getPlayers().length; i++) {
      if (data.getPlayers()[i].equals(e.getTargetPlayer())) {
        data.getPlayers()[i] = e.getTargetPlayer();
      }
    }
  }

  /**
   * This methods handles a GodCardUpdateEvent, updating the information of the GodCard linked to the event inside the data of this View
   * @param e is the GodCardUpdateEvent to handle
   */

  @Override
  public void handle(GodCardUpdateEvent e) {
    e.getUpdatedCard().setGameData(data);
    if (data.getOwner().equals(e.getOwner())) {
      data.getOwner().setGodCard(e.getUpdatedCard());
    }
    if (data.getCurrentPlayer().equals(e.getOwner())) {
      data.getCurrentPlayer().setGodCard(e.getUpdatedCard());
    }
    for (int i = 0; i < data.getPlayers().length; i++) {
      if (data.getPlayers()[i].equals(e.getOwner())) {
        data.getPlayers()[i].setGodCard(e.getUpdatedCard());
      }
    }
  }

  /**
   * This methods handles a LossEvent, adding the information that the player linked to the event has lost to the data of this View
   * @param e is the LossEvent to handle
   */

  @Override
  public void handle(LossEvent e) {
    data.removePlayer(e.getLosingPlayer());
  }

  /**
   * This methods handles a CardAssignmentEvent, adding the assigned GodCard to the player inside the data of this View
   * @param e is the CardAssignmentEvent to handle
   */

  @Override
  public void handle(CardAssignmentEvent e) {
    for (Player p : data.getPlayerSet()) {
      if (p.equals(e.getTargetPlayer())) {
        e.getAssignedCard().setGameData(data);
        p.setGodCard(e.getAssignedCard());
      }
    }
    if (data.getOwner().equals(e.getTargetPlayer())) {
      data.getOwner().setGodCard(e.getAssignedCard());
    }
  }

  /**
   * This methods handles a BoardUpdateEvent, updating the board inside the data of this View
   * @param e is the BoardUpdateEvent to handle
   */

  @Override
  public void handle(BoardUpdateEvent e) {
    data.setBoard(e.getNewBoard());
  }

  /**
   * This methods handles a GodSelectionStartEvent, notifying what player is the one that must select the GodCards to play in the game
   * inside the data of this View
   * @param e is the GodSelectionStartEvent to handle
   */

  @Override
  public void handle(GodSelectionStartEvent e) {
    Player[] players = e.getPlayers();
    for (int i = 0; i < players.length; i++) {
      for (Player p : data.getPlayerSet()) {
        if (players[i].equals(p)) {
          players[i] = p;
        }
      }
    }
    data.getPlayerSet().clear();
    for (Player p : players) {
      data.addPlayer(p);
    }
    data.setCurrentPlayer(e.getElectedPlayer());
  }

  /**
   * This methods handles a FirstPlayerEvent, setting the client's timeout properly
   * @param e is the FirstPlayerEvent to handle
   */

  @Override
  public void handle(FirstPlayerEvent e) {
    client.setSocketTimeout(0);
  }
}
