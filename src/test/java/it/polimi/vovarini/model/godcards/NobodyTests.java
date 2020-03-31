package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Stack;

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
