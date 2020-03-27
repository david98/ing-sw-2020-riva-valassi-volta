package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.BoxFullException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Item;
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

    Player player = new Player(game, godCard, nickname);

    assertEquals("M", player.getCurrentWorker().toString());
    assertEquals("F", player.getOtherWorker().toString());
    assertEquals(godCard, player.getGodCard());
    assertEquals(nickname, player.getNickname());
  }

  @Test
  @DisplayName("Test currentWorker movement")
  void playerMoveCurrentWorker() throws BoxEmptyException, InvalidPositionException, BoxFullException, InvalidLevelException {
    Game game = new Game(2);
    GodCard godCard = new Nobody(game);
    String nickname = "Guest";
    Point start = new Point(0,0);
    Point end = new Point(1,1);
    Block block_1 = new Block(1);

    Player player = new Player(game, godCard, nickname);

    game.getBoard().place(block_1, start);
    game.getBoard().place(player.getCurrentWorker(), start);
    Item item = game.getBoard().remove(start);
    game.getBoard().place(item, end);

    assertEquals(block_1, game.getBoard().getTopmostItem(start));
    assertEquals(game.getBoard().getTopmostItem(end), player.getCurrentWorker());
  }
}
