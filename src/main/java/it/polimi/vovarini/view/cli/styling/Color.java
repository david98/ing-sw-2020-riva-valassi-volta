package it.polimi.vovarini.view.cli.styling;

import java.io.Serializable;

/**
 * Represents an RGB coded color.
 *
 * @author Davide Volta
 * @version 0.1
 * @since 0.1
 */
public class Color implements Serializable {

  public final static Color Red = new Color(255, 0, 0);
  public final static Color Green = new Color(0, 255, 0);
  public final static Color Blue = new Color(0, 0, 255);
  public final static Color White = new Color(255, 255, 255);
  public final static Color Black = new Color(0, 0, 0);

  public int r, g, b;

  /**
   * Creates a color with the given decimal red, green and blue values.
   *
   * @param r
   * @param g
   * @param b
   */
  public Color(int r, int g, int b) {
    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
      // we should log this
      System.err.println("What are you doing man?");
    }
    this.r = r;
    this.g = g;
    this.b = b;
  }

  /**
   * Wraps a string in ANSI escape sequences that produce output colored in the
   * color represented by this object (on terminals that support such escape sequences).
   *
   * @param s The string to be colored.
   * @return {@code s} surrounded by the correct escape sequences.
   */
  public String fgWrap(String s) {
    // 27 is \x1b which is not supported by Java lol
    return (char) 27 + "[38;2;" + r + ";" + g + ";" + b + "m" + s + (char) 27 + "[0m";
  }

  /**
   * Wraps a string in ANSI escape sequences that produce output with the background
   * color represented by this object (on terminals that support such escape sequences).
   *
   * @param s The string to be colored.
   * @return {@code s} surrounded by the correct escape sequences.
   */
  public String bgWrap(String s) {
    // 27 is \x1b which is not supported by Java lol
    return (char) 27 + "[48;2;" + r + ";" + g + ";" + b + "m" + s + (char) 27 + "[0m";
  }

  public static void main(String[] args) {
    Color c = new Color(255, 0, 0);
    System.out.println(c.bgWrap(Color.Green.fgWrap("hey")));
  }
}
