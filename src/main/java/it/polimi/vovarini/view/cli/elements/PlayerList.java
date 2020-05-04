package it.polimi.vovarini.view.cli.elements;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.cli.Color;

import java.util.Map;
import java.util.Set;

public class PlayerList extends CLIElement {

  private final Set<Player> players;
  private final Player owner;
  private Player currentPlayer;

  private final Map<Player, Color> playersColors;

  public PlayerList(Set<Player> players, Player owner, Map<Player, Color> playersColors) {
    this.players = players;
    this.owner = owner;
    this.playersColors = playersColors;
  }

  @Override
  public String render(){
    StringBuilder content = new StringBuilder();
    for (Player player : players){
      boolean isOwner = player.equals(owner);
      boolean isCurrent = player.equals(currentPlayer);
      String playerLine = playersColors.get(player).fgWrap(
              player.getNickname() + (player.getGodCard() == null ?
                      null : " (" + player.getGodCard().getName().name() + ")"));
      content.append(isOwner ? "YOU --> " : "")
              .append(isCurrent ? Color.White.bgWrap(playerLine) : playerLine)
              .append("\n");
    }
    return content.toString();
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }
}
