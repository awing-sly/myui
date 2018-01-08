package per.awing.myui.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;


import per.awing.myui.touch.DragHandler;
import per.awing.myui.touch.TouchSlideHandler;
import per.awing.myui.util.MLOG;

public class LauncherContainerView extends AdapterView<ListAdapter> {
    private static final String TAG = "LauncherContainerView";

    private static final int BOUND_SCROLL_SCALE = 2;
    private static final int DRAG_SWITCH_BOUND_WIDTH = 8;
    private static final int DRAG_LOCATION_DISABLE_DURATION = 800;
    public static final int MODE_IDLE = 0;
    public static final int MODE_SLIDE = 1;
    public static final int MODE_DRAG = 2;
    private static LauncherContainerView sInstance = null;

    private PageManager mPageManager = new PageManager();
    private  ListAdapter mTileAdapter;
    private int mValidSlideDistance;
    private int mPageScrollDuration = 300;
    private int mPageScrollX;
    private int mBoundScrollWidth;
    private boolean mAnimating = false;
    private boolean mTouchInAnimating = false;
    private Scroller mScroller;
    private int mMode = MODE_IDLE;
    private long mDisableLocationTime;
    private TouchSlideHandler mSlideHandler = new TouchSlideHandler() {
        protected boolean onSlideStart(MotionEvent event) {
            mPageScrollX = 0;
            mMode = MODE_SLIDE;
            return true;
        }

        protected boolean onSlideTo(MotionEvent to) {
            int pageIndex = mPageManager.getCurrentPage();
            int pageCount = mPageManager.getPageCount();
            int offset = getOffsetToLast(MotionEvent.AXIS_X, to);
            if (offset == 0) {
                MLOG.d(TAG, "onSlideTo Offset is 0, don't scroll");
                return true;
            }

            int scrollXDisp = getSlideDisplacement(MotionEvent.AXIS_X);
            // handle the page bound
            if (pageIndex == 0) {
                if (mPageScrollX >= mBoundScrollWidth) {
                    return true;
                } else if (scrollXDisp >= mBoundScrollWidth) {
                    scrollXDisp = mBoundScrollWidth;
                }
            }
            if (pageIndex == pageCount - 1 ) {
                if (mPageScrollX <= -mBoundScrollWidth) {
                    return true;
                } else if (scrollXDisp <= -mBoundScrollWidth) {
                    scrollXDisp = -mBoundScrollWidth;
                }
            }

            // reach bound, slow down scroll velocity
            if (pageIndex == 0 && scrollXDisp >= 0) {
                scrollXDisp /= BOUND_SCROLL_SCALE;
            }
            if (pageIndex == pageCount - 1 && scrollXDisp <= 0) {
                scrollXDisp /= BOUND_SCROLL_SCALE;
            }
            mPageScrollX = scrollXDisp;

            MLOG.d(TAG, "onSlideTo scrollBy offset:" + offset + " mPageIndex:" + pageIndex
                    + " OrignalScrollXDisp:" + getSlideDisplacement(MotionEvent.AXIS_X) + " mPageScrollX:" + mPageScrollX);

            scrollTo(-mPageScrollX + pageIndex * mPageManager.getPageWidth(), 0);
            return true;
        }

        protected boolean onSlideStop(MotionEvent event) {
            slidePage();
            mMode = MODE_IDLE;
            mPageScrollX = 0;
            return true;
        }
    };

    private DragHandler mDragHandler= new DragHandler() {
        protected boolean onDragStop(DragEvent event) {
            Point p = getLastPoint();
            MLOG.d(TAG, "onDragStop enter p:" + p);
            PageManager pageMgr = getPageManager();
            TileInfo tile = (TileInfo) event.getLocalState();
            boolean moved = pageMgr.moveTileToPage(pageMgr.getCurrentPage(), tile, p.x, p.y);
            tile.view.setVisibility(View.VISIBLE);
            if (moved) {
                requestLayout();
            }
            mDisableLocationTime = 0;
            mMode = MODE_IDLE;
            return true;
        }

        protected boolean onDragStart(DragEvent event) {
            mMode = MODE_DRAG;
            TileInfo tile = (TileInfo)event.getLocalState();
            if (tile != null) {
                tile.view.setVisibility(View.GONE);
            }
            return true;
        }

        protected boolean onDragLocation(DragEvent event) {
            long now = SystemClock.uptimeMillis();
            long elapse = now - mDisableLocationTime;
            if (mDisableLocationTime != 0 && elapse <= DRAG_LOCATION_DISABLE_DURATION) {
                return false;
            }
            int x = (int)event.getX();
            int pageWidth = mPageManager.getPageWidth();
            int currentPage = mPageManager.getCurrentPage();
            int nextPage = -1;
            if (x >= 0 && x <= DRAG_SWITCH_BOUND_WIDTH) {
                if (currentPage != 0) {
                    nextPage = currentPage - 1;
                }
            }
            if (x >= pageWidth - DRAG_SWITCH_BOUND_WIDTH && x <= pageWidth) {
                if (currentPage != mPageManager.getPageCount() -1) {
                    nextPage = currentPage + 1;
                }
            }
            if (nextPage != -1) {
                slideToPage(nextPage);
            }
            return true;
        }
    };
    public PageManager getPageManager() {
        return mPageManager;
    }

    public int getMode() {
        return mMode;
    }
    public static LauncherContainerView getInstance() {
        return sInstance;
    }
    private boolean slidePage() {
        int slideDisp = mPageScrollX;
        if (slideDisp == 0) {
            MLOG.d(TAG, "onSlideStop slideDistance is 0, don't scroll");
            return false;
        }
        int currentPage = mPageManager.getCurrentPage();
        int pageCount = mPageManager.getPageCount();
        int nextPage = currentPage;

        if (Math.abs(slideDisp) > mValidSlideDistance) {
            if (slideDisp < 0) {
                nextPage = currentPage + 1;
            } else {
                nextPage = currentPage -1;
            }
        }

        if (nextPage < 0 || nextPage > pageCount - 1) {
            nextPage = currentPage;
        }

        MLOG.d(TAG, "slidePage " + " nextPage:" + nextPage + " currentPage:" + currentPage 
                + " slideDisp:" + slideDisp + " mValidSlideDistance:" + mValidSlideDistance);
        return slideToPage(nextPage);
    }

    private boolean slideToPage(int targetPage) {
        int pageScrollX = targetPage * mPageManager.getPageWidth();
        int currentPage = mPageManager.getCurrentPage();
        scrollToSmoothly(pageScrollX, mPageScrollDuration);
        if (currentPage != targetPage) {
            mPageManager.switchPage(targetPage);
            return true;
        } else {
            return false;
        }
    }

    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            setScrollX(mScroller.getCurrX());
            postInvalidate();
        } else {
            // scroll complete
            handleResetAnimation();
        }
    }

    private void handleResetAnimation() {
        mAnimating = false;
        // if touch happen, during animation, force reset touch Data
        if (mTouchInAnimating) {
            mSlideHandler.setSlideEnabled(true);
            mTouchInAnimating = false;
        }
    }
    private void scrollToSmoothly(int to, int duration) {
        int from = getScrollX();
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        mScroller.startScroll(from, 0, to - from, 0, duration);
        mAnimating = true;
        mDisableLocationTime = SystemClock.uptimeMillis();
        invalidate();
    }
    public LauncherContainerView(Context context) {
        super(context);
    }

    public LauncherContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LauncherContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScroller = new Scroller(getContext());
        sInstance = this;
    }

    private int getStatusBarHeight() {
        Resources res = getContext().getResources();
        int resid = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resid <= 0) {
            MLOG.e(TAG, "fail to get status bar height resid:" + resid);
            return 0;
        }
        return res.getDimensionPixelSize(resid);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MLOG.d(TAG, "onMeasure widthMeasureSpec: " + MeasureSpec.toString(widthMeasureSpec)
                + " heightMeasureSpec:" + MeasureSpec.toString(heightMeasureSpec));
        if (mPageManager.initPageIfNeed(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec))) {
            mValidSlideDistance = mPageManager.getPageWidth() / 3;
            mBoundScrollWidth = mPageManager.getPageWidth() / 3 * BOUND_SCROLL_SCALE;
        }

        if (mTileAdapter == null) {
            return;
        }
        int tileCount = mTileAdapter.getCount();
        for (int i = 0; i < tileCount; i++) {
            TileView child = (TileView) mTileAdapter.getView(i, null, this);
            int childWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, child.getTileInfo().width);
            int childHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, child.getTileInfo().height);
            //MLOG.d(TAG, "i:" + i + " onMeasure childWidthSpec: " + MeasureSpec.toString(childWidthSpec)
            //        + " childHeightSpec:" + MeasureSpec.toString(childHeightSpec));
            child.measure(childWidthSpec, childHeightSpec);
        }
        int widthSize = mPageManager.getPageWidth() * mPageManager.getPageCount();
        int heightSize = mPageManager.getPageHeight();
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        MLOG.d(TAG, "onLayout changed:" + changed);
        layoutTileChildren();
    }

    private void layoutTileChildren() {
        if (mTileAdapter == null) {
            return;
        }

        int pageCount = mPageManager.getPageCount();
        MLOG.d(TAG, "layoutTileChildren pageCount:" + pageCount);
        boolean addChild = false;
        if (getChildCount() == 0) {
            addChild = true;
        }
        for (int i = 0; i < pageCount; i++) {
            Page page = mPageManager.getPage(i);
            TileInfo[] tileArray = page.getTiles();
            for (int j = 0; j < tileArray.length; j++) {
                TileInfo tile = tileArray[j];
                if (tile != null) {
                    if (tile.view == null) {
                        continue;
                    }
                    tile.view.layout(tile.left, tile.top, tile.left + tile.width, tile.top + tile.height);
                    if (addChild) {
                        addViewInLayout(tile.view, -1, 
                                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    }
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        MLOG.d(TAG, "onInterceptTouchEvent Event:" + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }
    private boolean deliverTouchEvent(MotionEvent event) {
        boolean handled = false;
        // during animating, ignore touch event until animation finish
        if (mAnimating) {
            mTouchInAnimating = true;
            mSlideHandler.setSlideEnabled(false);
        }
        handled = mSlideHandler.dispatchTouchEvent(event);
        return handled;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        MLOG.d(TAG, "dispatchTouchEvent Event:" + ev.getAction());
        boolean result = super.dispatchTouchEvent(ev);
        return deliverTouchEvent(ev) || result;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MLOG.d(TAG, "onTouchEvent Event:" + event.getAction());
        // force parent deliver MotionEvent to this view
        return true;
    }
    @Override
    public ListAdapter getAdapter() {
        return mTileAdapter;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return mDragHandler.dispatchDragEvent(event);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mTileAdapter = adapter;
        mPageManager.createPageFromAdapter(adapter,
                PageManager.DEFAULT_PAGE_ROW,
                PageManager.DEFAULT_PAGE_COLUM);
        invalidate();
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException("unsupport operation getSelectedView");
    }

    @Override
    public void setSelection(int position) {
        throw new UnsupportedOperationException("unsupport operation setSelection");
    }

}
