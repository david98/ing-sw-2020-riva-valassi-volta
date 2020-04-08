package it.polimi.vovarini.view;

import it.polimi.vovarini.model.Player;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class PlayerRenderer {

  public static PlayerRenderer instance = null;

  public static PlayerRenderer getInstance(){
    if (instance == null){
      instance = new PlayerRenderer();
    }
    return instance;
  }

  private HashMap<Player, Color> colorMap;

  private PlayerRenderer(){
    colorMap = new HashMap<>();
  }

  public void setPlayers(Player[] players){
    Random rand = new Random();
    for (Player player: players){
      colorMap.put(player, new Color(rand.nextInt(255), rand.nextInt(255), rand
              .nextInt(255)));
    }
  }

  public Set<Player> getPlayers(){
    return colorMap.keySet();
  }

  public Color getPlayerColor(Player player){
    return colorMap.get(player);
  }
}
