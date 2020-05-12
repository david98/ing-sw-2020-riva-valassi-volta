package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NobodyTests {

  Game game;
  String name;

  @BeforeEach
  void createNobodyItems() {
    try {
      game = new Game(2);
    } catch (InvalidNumberOfPlayersException ignored) {

    }
    name = "";
  }

  @Test
  @DisplayName("Test that a GodCard of type Nobody can be instantiated correctly")
  void nobodyCreation() {
    GodCard nobody = GodCardFactory.create(GodName.Nobody);
    assertEquals(GodName.Nobody, nobody.name);
  }
}
