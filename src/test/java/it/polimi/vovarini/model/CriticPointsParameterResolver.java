package it.polimi.vovarini.model;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Random;

public class CriticPointsParameterResolver implements ParameterResolver {

  public static Point[] VALID_POINTS = {
    new Point(0, 0),
    new Point(0, 2),
    new Point(0, 4),
    new Point(1, 1),
    new Point(2, 4),
    new Point(3, 2),
    new Point(4, 4)
  };

  @Override
  public boolean supportsParameter(ParameterContext context, ExtensionContext extension)
      throws ParameterResolutionException {
    boolean ret = false;
    if (context.getParameter().getType().equals(Point.class)) {
      ret = true;
    }
    return ret;
  }

  @Override
  public Object resolveParameter(ParameterContext context, ExtensionContext extension)
      throws ParameterResolutionException {
    Object ret = null;
    if (context.getParameter().getType().equals(Point.class)) {
      ret = VALID_POINTS[new Random().nextInt(VALID_POINTS.length)];
    }
    return ret;
  }
}
