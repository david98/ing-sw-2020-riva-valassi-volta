package it.polimi.vovarini.model;

import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.Nobody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTests {

  @Test
  @DisplayName("Test that a Player can be instantiated correctly")
  void playerCreation() {
    Game game = new Game(2);
    GodCard godCard = new Nobody(game);
    String nickname = "Guest";

    Player player = new Player(godCard, nickname);

    assertEquals("M", player.getCurrentWorker().toString());
    assertEquals("F", player.getOtherWorker().toString());
    assertEquals(godCard, player.getGodCard());
    assertEquals(nickname, player.getNickname());
  }
}
