package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.exceptions.BoxFullException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.common.exceptions.InvalidPositionException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.moves.Construction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZeusTests {

  private Game game;
  private GodCard zeus;

  @BeforeEach
  public void init() {
    try {
      game = new Game(2);

      game.addPlayer("Guest01");
      game.addPlayer("Guest02");

      zeus = GodCardFactory.create(GodName.Zeus);
      zeus.setGameData(game);
      for (Player player : game.getPlayers()) {
        player.setGodCard(zeus);
      }
    } catch (InvalidNumberOfPlayersException e) {
      e.printStackTrace();
    }
  }

  private static Stream<Arguments> provideConstructionUnderMyself() {
    LinkedList<Arguments> args = new LinkedList<>();

    for (int level = 0; level < Block.MAX_LEVEL; level++) {
      args.add(Arguments.of(level));
    }

    return args.stream();
  }

  @Test
  @DisplayName("Test that a GodCard of type Zeus can be instantiated correctly")
  public void zeusCreation() {
    assertEquals(GodName.Zeus, game.getCurrentPlayer().getGodCard().name);
  }

  @ParameterizedTest
  @MethodSource("provideConstructionUnderMyself")
  @DisplayName("Test that Zeus' validation rules are correctly applied")
  public void testValidationCondition(int level) {

    Board board = game.getBoard();
    Point target = new Point(0, 0);

    for (int i = 0; i < level; i++) {
      try {
        board.place(Block.blocks[i], target);
      } catch (InvalidPositionException | BoxFullException e) {
        e.printStackTrace();
      }
    }
    try {
      board.place(game.getCurrentPlayer().getCurrentWorker(), target);
    } catch (InvalidPositionException | BoxFullException e) {
      e.printStackTrace();
    }

    Construction construction = new Construction(board, Block.blocks[level], target);
    assertEquals(Block.MAX_LEVEL - 1 != level, zeus.validate(zeus.computeBuildablePoints(), construction));
  }
}
