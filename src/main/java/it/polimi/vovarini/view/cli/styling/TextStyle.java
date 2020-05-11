package it.polimi.vovarini.view.cli.styling;

public class TextStyle {
  public static String bold(String s){
    return "\033[1m" + s + "\033[0m";
  }
}
