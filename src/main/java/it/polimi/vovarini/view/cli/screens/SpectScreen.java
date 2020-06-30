package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.BoardUpdateEvent;
import it.polimi.vovarini.common.events.CurrentPlayerChangedEvent;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.BoardElement;
import it.polimi.vovarini.view.cli.elements.PlayerList;
import it.polimi.vovarini.view.cli.elements.Text;
import it.polimi.vovarini.view.cli.styling.Color;

/**
 * A screen the player sees when they have lost, but there are still at
 * least two people playing.
 */

public class SpectScreen extends Screen {

  private final PlayerList playerList;
  private final BoardElement boardElement;
  private final Text message;

  public SpectScreen(ViewData data, GameClient client) {
    super(data, client);

    playerList = new PlayerList(data.getPlayerSet(), data.getOwner(), data.getPlayersColors());
    playerList.setCurrentPlayer(data.getCurrentPlayer());
    boardElement = new BoardElement(data.getBoard(), data.getPlayerSet(), data.getPlayersColors(), Color.Green);
    message = new Text("You lost :P");
  }

  @Override
  public String render() {
    StringBuilder content = new StringBuilder();
    content.append(playerList.render())
            .append("\n")
            .append(boardElement.render())
            .append("\n")
            .append(message.render());
    needsRender = false;
    return content.toString();
  }

  @Override
  public void handle(BoardUpdateEvent e) {
    boardElement.setBoard(e.getNewBoard());
    needsRender = true;
  }

  @Override
  public void handle(CurrentPlayerChangedEvent e) {
    playerList.setCurrentPlayer(e.getNewPlayer());
    message.setContent("");

    needsRender = true;
  }
}
