package per.awing.myui.touch;

import android.graphics.Point;
import android.view.DragEvent;

import per.awing.myui.util.MLOG;

public class DragHandler{
    private static final String TAG = "DragHandler";
    private Point mLastPoint = new Point();
    public void resetDragState() {
        mLastPoint.set(0, 0);
    }
    public Point getLastPoint() {
        return mLastPoint;
    }
    private void updateDragState(DragEvent event) {
        mLastPoint.set((int)event.getX(), (int)event.getY());
    }
    protected boolean onDragStart(DragEvent event) {
        return false;
    }
    protected boolean onDragStop(DragEvent event) {
        return false;
    }
    protected boolean onDragLocation(DragEvent event) {
        return false;
    }
    protected boolean onDragEnter(DragEvent event) {
        return false;
    }
    protected boolean onDragDrop(DragEvent event) {
        return false;
    }
    protected boolean onDragExit(DragEvent event) {
        return false;
    }
    public boolean dispatchDragEvent(DragEvent event) {
        int action = event.getAction();
        boolean result = false;
        MLOG.d(TAG, "dispatchDragEvent action:" + action);
        switch (action) {
        case DragEvent.ACTION_DRAG_STARTED:
            resetDragState();
            result = onDragStart(event);
            updateDragState(event);
            break;
        case DragEvent.ACTION_DRAG_ENDED:
            result = onDragStop(event);
            resetDragState();
            break;
        case DragEvent.ACTION_DRAG_LOCATION:
            result = onDragLocation(event);
            updateDragState(event);
            break;
        case DragEvent.ACTION_DRAG_ENTERED:
            result = onDragEnter(event);
            break;
        case DragEvent.ACTION_DRAG_EXITED:
            result = onDragExit(event);
            break;
        case DragEvent.ACTION_DROP:
            result = onDragDrop(event);
            updateDragState(event);
            break;
        default:
            MLOG.d(TAG, "unhandled drag event:" + action);
        }
        return result;
    }
}
