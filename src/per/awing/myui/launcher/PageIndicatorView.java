package per.awing.myui.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import per.awing.myui.util.MLOG;

public class PageIndicatorView extends View implements PageManager.PageChangeListener{
    private static final String TAG = "PageIndicatorView";
    public static final int INDICATOR_HEIGHT = 12;
    private static final int INDICATOR_INTERVAL = 8;
    private LauncherContainerView mHost;
    private int mCurrentPage;
    private int mPageCount;
    private int mWidth;
    private int mHeight;
    private int mFocusIndicatorWidth;
    private int mDimIndcatorWidht;
    private Paint mIndicatorPaint;

    public PageIndicatorView(Context context) {
        super(context);
        onCreateView();
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateView();
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreateView();
    }
    private void onCreateView() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(Color.GRAY);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
    }
    public void setHost( LauncherContainerView host) {
        mHost = host;
        mHost.getPageManager().registerListener(this);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = INDICATOR_HEIGHT;
        setMeasuredDimension(mWidth, mHeight);
        mFocusIndicatorWidth = mHeight - 4;
        mDimIndcatorWidht = mFocusIndicatorWidth / 2  >= 2 ? mFocusIndicatorWidth / 2 : 2;
    }

    @Override
    public void onPageChanged(int pageCount, int oldPage, int pageIdex) {
        mPageCount = pageCount;
        mCurrentPage = pageIdex;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPageCount <= 0 || mCurrentPage < 0) {
            return;
        }
        int contentWidth = mFocusIndicatorWidth + mDimIndcatorWidht * (mPageCount - 1) 
                + INDICATOR_INTERVAL * (mPageCount -1);
        int startLeft = (mWidth - contentWidth) / 2;

        for (int i = 0; i < mPageCount; i++) {
            if (mCurrentPage == i) {
                drawIndicator(canvas, startLeft, mFocusIndicatorWidth);
                startLeft += mFocusIndicatorWidth;
            } else {
                drawIndicator(canvas, startLeft, mDimIndcatorWidht);
                startLeft += mDimIndcatorWidht;
            }
            startLeft += INDICATOR_INTERVAL;
        }
    }
    
    private void drawIndicator(Canvas canvas, float start, float width) {
        float radius = width / 2;
        canvas.drawCircle(start + radius, mHeight / 2, radius, mIndicatorPaint);
    }
}
