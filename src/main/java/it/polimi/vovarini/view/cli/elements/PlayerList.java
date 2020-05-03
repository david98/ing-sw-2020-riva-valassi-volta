package it.polimi.vovarini.view.cli.elements;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.ViewData;

public class PlayerList extends CliElement {

  private final ViewData data;

  public PlayerList(ViewData data) {
    this.data = data;
  }

  @Override
  public String render(){
    StringBuilder content = new StringBuilder();
    for (Player player : data.getPlayers()){
      boolean isOwner = player.equals(data.getOwner());
      boolean isCurrent = player.equals(data.getCurrentPlayer());
      content.append(isOwner ? "YOU --> " : "")
              .append(isCurrent ? "*" : "")
              .append(data.getPlayersColors().get(player).wrap(
              player.getNickname() + (player.getGodCard() == null ?
                      null : " (" + player.getGodCard().getName().name() + ")")))
              .append(isCurrent ? "*" : "")
              .append("\n");
    }
    return content.toString();
  }
}
