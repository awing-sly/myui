package per.awing.myui.launcher;

import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import per.awing.myui.util.MLOG;

public class PageManager {
    private static final String TAG = "PageManager";
    private List<Page> mPageList = new ArrayList<Page>();
    public static final int DEFAULT_PAGE_ROW = 4;
    public static final int DEFAULT_PAGE_COLUM = 3;
    private int mCurrentPage = -1;
    private int mPageWidth;
    private int mPageHeight;
    private int mRowCount;
    private int mColumCount;
    private int mCellCount;
    private int mCellWidth;
    private int mCellHeight;
    private int mPagePaddingLeft;
    private int mPagePaddingRight;
    private int mPagePaddingTop;
    private int mPagePaddingBottom;
    private boolean mInitialized = false;

    private List<PageChangeListener> mPageListeners = new ArrayList<PageChangeListener>();
    public interface PageChangeListener{
        public void onPageChanged(int pageCount, int oldPage, int pageIndex);
    }

    public void registerListener(PageChangeListener listener) {
        if (mPageListeners.contains(listener)) {
            return;
        }
        mPageListeners.add(listener);
        if (mInitialized) {
            listener.onPageChanged(getPageCount(), -1, mCurrentPage);
        }
    }

    public void createPageFromAdapter(ListAdapter adapter, int row, int colum) {
    	mRowCount = row;
    	mColumCount = colum;
    	mCellCount = mRowCount * mColumCount;
        int pageCount = (adapter.getCount() + mCellCount - 1) / mCellCount;
        int tileCount = adapter.getCount();
        int nextTile = 0;
        for (int i = 0; i < pageCount; i++) {
            Page page = createPage(i);
            for (int j = 0; j < mCellCount && nextTile < tileCount; j++) {
                TileInfo tile = (TileInfo)adapter.getItem(nextTile);
                page.setTile(j, tile);
                nextTile++;
            }
        }
    }

    private Page createPage(int pageIndex) {
        Page page = new Page(pageIndex, mRowCount, mColumCount);
        mPageList.add(page);
        return page;
    }
    public int getCurrentPage() {
        return mCurrentPage;
    }

    public int getPageCount() {
        return  mPageList.size();
    }

    public int getPageWidth() {
        return mPageWidth;
    }

    public int getPageHeight() {
        return mPageHeight;
    }

    public int getCellWidth() {
        return mCellWidth;
    }

    public int getCellHeight() {
        return mCellHeight;
    }

    public void resetPages() {
    	for (Page page: mPageList) {
    		page.reset();
    	}
    	mPageList.clear();
    }
    public void switchPage(int newPage) {
        int oldPage = mCurrentPage;
        mCurrentPage = newPage;
        notifyPageChange(oldPage, mCurrentPage);
    }

    private void notifyPageChange(int oldPage, int newPage) {
        for (PageChangeListener listener : mPageListeners) {
            listener.onPageChanged(getPageCount(), oldPage, newPage);
        }
    }

    public boolean initPageIfNeed(int displayFrameWidth, int displayFrameHeight) {
        if (mInitialized) {
            return false;
        }
        mPageWidth = displayFrameWidth;
        mPageHeight = displayFrameHeight - PageIndicatorView.INDICATOR_HEIGHT;
        int paddingHor = mPageWidth % mColumCount;
        int paddingVer = mPageHeight % mRowCount;
        mPagePaddingLeft = paddingHor / 2;
        mPagePaddingRight = paddingHor - mPagePaddingLeft;
        mPagePaddingTop = paddingVer / 2;
        mPagePaddingBottom = paddingVer - mPagePaddingTop;
        mCellWidth = mPageWidth / mColumCount;
        mCellHeight = mPageHeight / mRowCount;
        mCurrentPage = 0;
        initPageTiles();
        if (mPageListeners.size() > 0) {
            notifyPageChange(-1, mCurrentPage);
        }
        mInitialized = true;
        MLOG.d(TAG, "mPageWidth:" + mPageWidth + " mPageHeight:" + mPageHeight + " mCellWidth:" + mCellWidth + " mCellHeight:" + mCellHeight);
        return true;
    }

    public Page getPage(int idx) {
        return mPageList.get(idx);
    }

    public TileInfo getTile(int pageIdx, int position) {
        return getPage(pageIdx).getTile(position);
    }
    private void initPageTiles() {
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            Page page = getPage(i);
            TileInfo[] tileArray = page.getTiles();
            for (int j = 0; j < tileArray.length; j++) {
                TileInfo tile = tileArray[j];
                if (tile == null) {
                    continue;
                }
                // TODO: should set suitable width height of tile to fit different resolution
                MLOG.d(TAG, "initPageTiles mCellWidth:" + mCellWidth + " mCellHeight:" + mCellHeight + 
                        " icon width:" + tile.icon.getIntrinsicWidth() + " height:" + tile.icon.getIntrinsicHeight());
                tile.width = mCellWidth;
                tile.height = mCellHeight;
                layoutTile(page.index, tile, j);
            }
        }
    }

    public void resetPageLayout(ListAdapter adapter, int row, int colum) {
    	resetPages();
    	createPageFromAdapter(adapter, row, colum);
    }

    public void layoutTile(int pageIndex, TileInfo tile, int position) {
        int rowId = position / mColumCount;
        int columId = position % mColumCount;
        layoutTile(pageIndex, tile, rowId, columId);
    }
    public void layoutTile(int pageIndex, TileInfo tile, int rowId, int columId) {
        tile.left = mPagePaddingLeft + mPageWidth * pageIndex + columId * mCellWidth;
        tile.top = mPagePaddingTop + rowId * mCellHeight;
    }
    public void layoutTileByPoint(int pageIndex, TileInfo tile, int x, int y) {
        int[] rowColum = new int[2];
        getCellByPoint(x, y, rowColum);
        int rowId = rowColum[0];
        int columId = rowColum[1];
        layoutTile(pageIndex, tile, rowId, columId);
    }
    public void getCellByPoint(int x, int y, int[] out) {
        if (!(out != null && out.length == 2)) {
            throw new IllegalArgumentException("out array length must be 2");
        }
        if (x <= mPagePaddingLeft - 1) {
            x = mPagePaddingLeft;
        } else if (x >= mPageWidth - 1 - mPagePaddingRight) {
            x = mPageWidth - mPagePaddingRight - 1;
        }
        if (y <= mPagePaddingTop - 1) {
            y = mPagePaddingTop;
        } else if (y >= mPageHeight - 1 - mPagePaddingBottom) {
            y = mPageHeight - mPagePaddingBottom - 1;
        }
        int rowId = (y - mPagePaddingTop) / mCellHeight;
        rowId = rowId >= 0 ? rowId : 0;
        int columId = (x - mPagePaddingLeft) / mCellWidth;
        columId = columId >= 0 ? columId : 0;
        out[0] = rowId;
        out[1] = columId;
    }

    public int getCellPosition(int rowId, int columId) {
        return rowId * mColumCount + columId;
    }
    public int getCellPosition(int[] rowColum) {
        return rowColum[0] * mColumCount + rowColum[1];
    }
    public boolean moveTileToPage(int pageIndex, TileInfo tile, int x, int y) {
        int[] rowColum = new int[2];
        getCellByPoint(x, y, rowColum);
        int targetPosition = getCellPosition(rowColum);
        return moveTileToPage(pageIndex, tile, targetPosition);
    }

    public boolean moveTileToPage(int pageIndex, TileInfo tile, int position) {
        MLOG.d(TAG, "moveTileToPage pageIndex:" + pageIndex + " tile:" + tile.name + " position:" + position);
        if (tile.page == pageIndex && position == tile.position) {
            MLOG.d(TAG, "original position, no need to move");
            return false;
        }

        int pageCount = getPageCount();
        int targetIndex = pageIndex;
        Page targetPage = null;
        if (targetIndex >= pageCount) {
            targetIndex = pageCount;
            targetPage = createPage(targetIndex);
        } else {
            targetPage = getPage(targetIndex);
        }

        TileInfo[] targetTiles = targetPage.getTiles();
        boolean isSamePage = (tile.page == targetIndex);
        MLOG.d(TAG, "targetIndex:" + targetIndex + " tile.page:" + tile.page + " tile.position:" +  tile.position);

        // for different page, there are two step to implement move operation
        // 1. remove tile from original page
        // 2. insert tile to target page

        // move backward
        // in same page, target position larger than tile's position
        // in different page, move the tiles behind target tile backward to remove target tile
        if ((isSamePage && tile.position < position) ||
                (!isSamePage && getTile(tile.page, tile.position) == tile)) {
            int start = tile.position + 1;
            int end = position;
            Page movePage = getPage(tile.page);
            if (!isSamePage || getTile(movePage.index, position) == null) {
                int idlePos = movePage.getIdlePosition(0);
                end =  idlePos > 0 ? idlePos - 1 : mCellCount - 1;
            }
            MLOG.d(TAG, "Move tile backward start:" + start + " end:" + end);
            for (int i = start; i <= end; i++) {
                TileInfo ti = movePage.getTiles()[i];
                MLOG.d(TAG, "MOVE tile:" + i + " tile:" + ti.name + " left:" + ti.left + " top:" + ti.top + " positon:" + ti.position);
                int movePosition = i - 1;
                movePage.setTile(movePosition, ti);
                layoutTile(movePage.index, ti, movePosition);
                MLOG.d(TAG, "After move >> left:" + ti.left + " top:" + ti.top + " positon:" + ti.position);
            }
            movePage.setTile(end, null);
        }

        // insert to target page
        if (targetTiles[position] == null) {
            // find the first idle cell to put
            for (int i = position; i >= 0; i--) {
                if (targetTiles[i] != null) {
                    break;
                }
                position = i;
            }
            MLOG.d(TAG, "Directly set position:" + position);
            targetPage.setTile(position, tile);
            layoutTile(targetIndex, tile, position);
            return true;
        }

        // move forward
        // in same page, target position smaller than tile's position
        // in different page, move tiles forward to insert tile to target position
        if (isSamePage && tile.position > position || !isSamePage) {
            int start = position;
            int end = position;
            // in the same page, and target position is before the tile's
            if (isSamePage && tile.position > position) {
                start = tile.position - 1;
            } else {
                int idlePos = targetPage.getIdlePosition(position + 1);
                start = idlePos > 0 ? idlePos - 1 : mCellCount - 1;
            }

            if (start == mCellCount -1) { // last is the tail
                TileInfo endTile = getTile(targetIndex, start--);
                moveTileToPage(targetIndex + 1, endTile, 0);
            }
            // move forward
            MLOG.d(TAG, "Move tile forward start:" + start + " end:" + end);
            for (int i = start; i >= end; i--) {
                TileInfo ti = targetTiles[i];
                MLOG.d(TAG, "MOVE tile:" + i + " tile:" + ti.name + " left:" + ti.left + " top:" + ti.top + " positon:" + ti.position);
                int movePosition = i + 1;
                targetPage.setTile(movePosition, ti);
                layoutTile(targetIndex, ti, movePosition);
                MLOG.d(TAG, "After move >> left:" + ti.left + " top:" + ti.top + " positon:" + ti.position);
            }
        }

        targetPage.setTile(position, tile);
        layoutTile(targetIndex, tile, position);
        //MLOG.d(TAG, "insert to position:" + position + " left:" + tile.left + " top:" + tile.top);
        return true;
    }
}
