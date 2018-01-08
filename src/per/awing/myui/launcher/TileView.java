package per.awing.myui.launcher;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import per.awing.myui.R;
import per.awing.myui.touch.TouchUtil;
import per.awing.myui.util.MLOG;
import per.awing.myui.util.SystemUtil;

public class TileView extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "TileView";
    private static final float TOUCH_SHAKE_RANGE = 5.0f;
    private TileInfo mTileInfo;
    private View mView;
    private boolean mClearPressed = false;
    private Point mDownPoint;

    private static int sTouchHandlerCount = 0;

    public TileView(Context context, TileInfo tileInfo) {
        super(context);
        setTileInfo(tileInfo);
    }
    public TileView(Context context) {
        super(context);
    }
    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setTileInfo(TileInfo info) {
        mTileInfo = info;
        initView();
    }

    private void updateDownPoint(MotionEvent event) {
        if (mDownPoint == null) {
            mDownPoint = new Point();
        }
        mDownPoint.x = (int) event.getX();
        mDownPoint.y = (int) event.getY();
    }
    public TileInfo getTileInfo() {
        return mTileInfo;
    }
    private void initView() {
        mView = inflateTile();
        mView.setOnClickListener(this);
        mView.setOnLongClickListener(this);
    }

    private View inflateTile() {
        View view = inflate(getContext(), R.layout.tile_layout, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tile_icon);
        icon.setImageDrawable(mTileInfo.icon);
        TextView name = (TextView) view.findViewById(R.id.tile_name);
        name.setText(mTileInfo.name);
        name.setTextColor(Color.WHITE);        
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        addView(view, lp);
        return view;
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(msg)
        .setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        if (mTileInfo.component == SystemUtil.MULTI_WINDOW_COMPONET) {
    		SystemUtil.requestMultiWindow(true);
    		return;
    	}
        String tileMsg = mTileInfo.toString();
        showDialog(tileMsg);
    }

    @Override
    public boolean onLongClick(View v) {
        startDrag(ClipData.newPlainText(mTileInfo.name, mTileInfo.name),
                new DragShadowBuilder(this), mTileInfo, 0);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        MLOG.d(TAG, "onInterceptTouchEvent event:" + event.getAction() + " actionMasked:" + event.getActionMasked());
        int action = event.getActionMasked();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            // intercept multi-pointer touch event
            if (sTouchHandlerCount > 0) {
                return true;
            }
            sTouchHandlerCount++;
            mClearPressed = false;
            updateDownPoint(event);
            break;
        case MotionEvent.ACTION_MOVE:
        {
            if (!mClearPressed && (float)TouchUtil.distance(event, mDownPoint) > TOUCH_SHAKE_RANGE) {
                mView.setPressed(false);
                mView.cancelLongPress();
                mClearPressed = true;
            }
            break;
        }
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            sTouchHandlerCount--;
            mDownPoint = null;
            mClearPressed = false;
            break;
        default:
            MLOG.d(TAG, "Unhandled touch event:" + action);
            break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        MLOG.d(TAG, "onDraw:" + mTileInfo.name);
        super.onDraw(canvas);
    }

}
