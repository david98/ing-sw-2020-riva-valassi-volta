package it.polimi.vovarini.model;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Random;

public class InvalidPointsParameterResolver implements ParameterResolver {

    public static Point[] VALID_POINTS = {
            new Point(0,-1),
            new Point(-1,0),
            new Point(0,-2),
            new Point(0,-3),
            new Point(-1,4),
            new Point(-1,-1),
            new Point(5,1),
            new Point(6,2),
            new Point(1,5),
            new Point(1,6),
            new Point(2,7),
            new Point(5,-1),
            new Point(5,-6),
            new Point(-7,-7),
    };

    @Override
    public boolean supportsParameter(ParameterContext context, ExtensionContext extension) throws ParameterResolutionException {
        boolean ret = false;
        if (context.getParameter().getType().equals(Point.class)){
            ret = true;
        }
        return ret;
    }

    @Override
    public Object resolveParameter(ParameterContext context, ExtensionContext extension) throws ParameterResolutionException {
        Object ret = null;
        if (context.getParameter().getType().equals(Point.class)){
            ret = VALID_POINTS[new Random().nextInt(VALID_POINTS.length)];
        }
        return ret;
    }

}
