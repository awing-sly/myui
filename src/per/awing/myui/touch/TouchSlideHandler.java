package per.awing.myui.touch;

import android.view.MotionEvent;

import per.awing.myui.util.MLOG;

public class TouchSlideHandler {
    private static final String TAG = "TouchSlideHandler";
    private static final int ACTION_NO_INIT = -1;
    private class LocalTouchEvent {
        float x;
        float y;
        int actionMasked;
        LocalTouchEvent() {
            reset();
        }
        void updateTouchEvent(MotionEvent event) {
            x = event.getX();
            y = event.getY();
            actionMasked = event.getActionMasked();
        }
        void reset() {
            x = 0;
            y = 0;
            actionMasked = ACTION_NO_INIT;
        }
    };

    private LocalTouchEvent mLastEvent = new LocalTouchEvent();
    private LocalTouchEvent mDownEvent = new LocalTouchEvent();
    private int mXSlideDisp = 0;
    private int mYSlideDisp = 0;
    private boolean mTouchSequenceStarted = false;
    private boolean mSlideEnabled = true;

    public void setSlideEnabled(boolean enabled) {
        mSlideEnabled = enabled;
    }

    public void resetTouchState() {
        mXSlideDisp = 0;
        mYSlideDisp = 0;
        mLastEvent.reset();
        mDownEvent.reset();
    }

    public int getSlideDisplacement(int axis) {
        if (axis != MotionEvent.AXIS_X && axis != MotionEvent.AXIS_Y) {
            throw new IllegalArgumentException("axis must be MotionEvent.AXIS_X or AXIS_Y, now " + axis);
        }
        if (axis == MotionEvent.AXIS_X) {
            return mXSlideDisp;
        } else {
            return mYSlideDisp;
        }
    }

    protected boolean onSlideStart(MotionEvent event) {
        return false;
    }

    protected boolean onSlideTo(MotionEvent to) {
        return false;
    }

    protected int getOffsetToLast(int axis, MotionEvent to) {
        return computeOffset(axis, mLastEvent, to);
    }

    protected boolean onSlideStop(MotionEvent event) {
        return false;
    }

    private int computeOffset(int axis, LocalTouchEvent from, MotionEvent to) {
        if (axis != MotionEvent.AXIS_X && axis != MotionEvent.AXIS_Y) {
            throw new IllegalArgumentException("invalid axis, Must be MotionEvent.AXIS_X or AXIS_Y");
        }
        if (from.actionMasked == ACTION_NO_INIT) {
            return 0;
        }
        MLOG.d(TAG, "from: x:" + from.x + " y:" + from.y + " to: x" + to.getX() + " y:" + to.getY());
        if (axis == MotionEvent.AXIS_X) {
            float xoffset = to.getX() - from.x;
            return (int)xoffset;
        } else {
            float yoffset = to.getY() - from.y;
            return (int)yoffset;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handled = false;
        int action = event.getActionMasked();
        MLOG.d(TAG, "dispatchTouchEvent action:" + action +
                " mTouchSequenceStarted:" + mTouchSequenceStarted + " mSlideEnabled:" + mSlideEnabled);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
                resetTouchState();
                mDownEvent.updateTouchEvent(event);
                mLastEvent.updateTouchEvent(event);
                handled = true;
                mTouchSequenceStarted = true;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mTouchSequenceStarted) {
                mXSlideDisp = computeOffset(MotionEvent.AXIS_X, mDownEvent, event);
                mYSlideDisp = computeOffset(MotionEvent.AXIS_Y, mDownEvent, event);
                if (mSlideEnabled) {
                    if (mLastEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                        handled = onSlideStart(event);
                    }
                    handled = handled || onSlideTo(event);
                }
                mLastEvent.updateTouchEvent(event);
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (mTouchSequenceStarted) {
                if (mSlideEnabled) {
                    handled = onSlideStop(event);
                }
                mTouchSequenceStarted = false;
                resetTouchState();
            }
            break;
        default:
            MLOG.w(TAG, "unhandled touch action:" + action);
            break;
        }
        return handled;
    }

}
