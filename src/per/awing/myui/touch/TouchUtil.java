package per.awing.myui.touch;

import android.graphics.Point;
import android.view.MotionEvent;

public class TouchUtil {
    public static double distance(MotionEvent from, Point to) {
        Point np = new Point((int)from.getX(), (int)from.getY());
        return distance(np, to);
    }

    public static double distance(Point from, Point to) {
        return Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
    }
}
