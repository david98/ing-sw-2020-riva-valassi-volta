package it.polimi.vovarini.view;

public class Color {
  public int r, g, b;

  public Color(int r, int g, int b) {
    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
      // we should log this
      System.err.println("What are you doing man?");
    }
    this.r = r;
    this.g = g;
    this.b = b;
  }

  /*
   * returns s surrounded by escape sequences that reproduce this color
   */
  public String wrap(String s) {
    // 27 is \x1b which is not supported by Java lol
    return (char) 27 + "[38;2;" + r + ";" + g + ";" + b + "m" + s + (char) 27 + "[0m";
  }
}
