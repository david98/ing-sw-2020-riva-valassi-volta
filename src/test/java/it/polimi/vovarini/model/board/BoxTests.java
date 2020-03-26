package it.polimi.vovarini.model.board;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoxTests {

  @Test
  @DisplayName("Test that a Box can be instantiated correctly")
  void boxCreation() {
    Box box = new Box();
    BoxEmptyException thrown =
        assertThrows(
            BoxEmptyException.class,
            box::getTopmost,
            "Expected getTopmost() to throw BoxEmptyException, but it didn't");

    thrown =
        assertThrows(
            BoxEmptyException.class,
            box::removeTopmost,
            "Expected removeTopmost() to throw BoxEmptyException, but it didn't");
  }
}
