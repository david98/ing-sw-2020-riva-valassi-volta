package it.polimi.vovarini.view.cli.console;

import java.io.Reader;
import java.util.Scanner;

/**
 * This interface contains all the methods that a console is expected
 * to provide.
 *
 * @author Davide Volta
 */
public interface Console {

  /**
   * Completely clears the console.
   */
  public void clear();

  public void print(String str);

  public void println(String str);

  /**
   * Puts the console into raw mode, which means that it will switch from line mode
   * to char mode.
   */
  public void enterRawMode();

  public Scanner getScanner();

  public Reader getReader();
}
