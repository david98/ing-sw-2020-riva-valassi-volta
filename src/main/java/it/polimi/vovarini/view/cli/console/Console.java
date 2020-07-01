package it.polimi.vovarini.view.cli.console;

import java.io.Reader;
import java.util.Scanner;

public interface Console {
  public void clear();

  public void print(String str);

  public void println(String str);

  public void enterRawMode();

  public Scanner getScanner();

  public Reader getReader();
}
