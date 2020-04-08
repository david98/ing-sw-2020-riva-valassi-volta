package it.polimi.vovarini.view;

import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.board.items.Item;

import java.util.HashMap;

public class ItemRenderer {

  public static ItemRenderer instance = null;

  public static ItemRenderer getInstance() {
    if (instance == null) {
      instance = new ItemRenderer();
    }
    return instance;
  }

  private HashMap<Item, Player> ownerMap;

  public ItemRenderer() {
    this.ownerMap = new HashMap<>();
  }

  public String render(Item item, PlayerRenderer playerRenderer) {
    if (!item.canBeRemoved()) {
      return item.toString();
    } else {
      Player owner = ownerMap.get(item);
      if (owner == null) {
        owner = findOwner(item, playerRenderer);
      }
      return playerRenderer.getPlayerColor(owner).wrap(item.toString());
    }
  }

  private Player findOwner(Item item, PlayerRenderer playerRenderer) {
    for (Player player : playerRenderer.getPlayers()) {
      if (player.getWorkers().containsValue(item)) {
        ownerMap.put(item, player);
        return player;
      }
    }
    return null;
  }
}
