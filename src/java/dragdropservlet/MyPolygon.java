package dragdropservlet;

import java.awt.Polygon;

/**
 * Custom polygon class.
 */
public class MyPolygon extends Polygon {

    private static final int SNAP_TOLERANCE = 20;

    public MyPolygon(int[] xCoords, int[] yCoords) {
        super(xCoords, yCoords, xCoords.length);
    }

    /**
     * Check if current polygon is close to snap polygon. If it is, then translate the polygon to snap location.
     */
    public boolean isCloseTo(MyPolygon snapPolygon) {
        boolean isCloseTo = Math.abs(getBounds().x - snapPolygon.getBounds().x) < SNAP_TOLERANCE
                && Math.abs(getBounds().y - snapPolygon.getBounds().y) < SNAP_TOLERANCE;
        if (isCloseTo) {
            //move polygon to snap polygon location
            int deltaX = snapPolygon.getBounds().x - getBounds().x;
            int deltaY = snapPolygon.getBounds().y - getBounds().y;
            translate(deltaX, deltaY);
        }
        return isCloseTo;
    }

}
