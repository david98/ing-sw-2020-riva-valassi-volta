package it.polimi.vovarini.view.cli;

public class Utils {
  public static int clamp(int n, int minInclusive, int maxExclusive){
    return Math.max(minInclusive, Math.min(n, maxExclusive - 1));
  }
}
