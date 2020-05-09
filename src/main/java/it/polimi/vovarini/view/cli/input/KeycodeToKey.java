package it.polimi.vovarini.view.cli.input;

import it.polimi.vovarini.view.cli.console.Console;
import it.polimi.vovarini.view.cli.console.FullScreenConsole;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class KeycodeToKey {
  public final static Map<Integer, Key> map = Map.of(
          97, Key.A,
          100, Key.D,
          119, Key.W,
          115, Key.S,
          32, Key.Spacebar,
          110, Key.N,
          111, Key.O);

  public static void main(String[] args) throws IOException {
    Console console = new FullScreenConsole();
    console.clear();
    console.enterRawMode();
    Reader r = console.getReader();
    while(true){
      int input = r.read();
      console.println(Integer.toString(input));
    }
  }
}
