package it.polimi.vovarini.view.cli.input;

import java.util.Map;

public class KeycodeToKey {

  private KeycodeToKey() {
    throw new IllegalStateException("Utility class");
  }

  public static final Map<Integer, Key> map = Map.of(
          97, Key.A,
          100, Key.D,
          119, Key.W,
          115, Key.S,
          32, Key.Spacebar,
          110, Key.N,
          111, Key.O,
          52, Key.Four);
}
