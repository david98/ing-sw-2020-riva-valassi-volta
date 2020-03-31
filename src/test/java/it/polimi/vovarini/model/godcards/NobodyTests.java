package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NobodyTests {

  Game game;
  String name;

  @BeforeEach
  void createNobodyItems() {
    game = new Game(2);
    name = "";
  }

  @Test
  @DisplayName("Test that a GodCard of type Nobody can be instantiated correctly")
  void nobodyCreation() {
    Nobody nobody = new Nobody(game);
    assertEquals(nobody.name, "Nobody");
  }
}
