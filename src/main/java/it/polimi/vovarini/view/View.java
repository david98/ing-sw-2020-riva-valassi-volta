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

  public View() {
    GameEventManager.bindListeners(this);
    data = new ViewData();
  }

  @Override
  public void handle(NewPlayerEvent e) {
    if (client != null) {
      client.setSocketTimeout(0);
    }
    if (data.getOwner().equals(e.getNewPlayer())) {
      data.setOwner(e.getNewPlayer());
    }
    data.addPlayer(e.getNewPlayer());
  }

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

  @Override
  public void handle(LossEvent e) {
    data.removePlayer(e.getLosingPlayer());
    if (e.getLosingPlayer().equals(data.getOwner()) && !data.getOwner().isHasLost()) {
      data.getOwner().setHasLost(true);
    }
  }

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

  @Override
  public void handle(BoardUpdateEvent e) {
    data.setBoard(e.getNewBoard());
  }

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

  @Override
  public void handle(FirstPlayerEvent e) {
    if (client != null) {
      client.setSocketTimeout(0);
    }
  }
}
