package per.awing.myui.launcher;

public class Page {
    int index;
    int row;
    int colum;
    private int mCellCount;

    public Page(int id, int row, int colum) {
        index = id;
        this.row = row;
        this.colum = colum;
        mCellCount = row * colum;
        mTiles = new TileInfo[mCellCount];
    }
    private TileInfo[] mTiles;

    public void setTile(int position, TileInfo tile) {
        mTiles[position] = tile;
        if (tile != null) {
            tile.position = position;
            tile.page = index;
        }
    }

    public void setTile(int rowId, int columId, TileInfo tile) {
        int position = rowId * colum + columId;
        setTile(position, tile);
    }
    public void setBlockTile(TileInfo tile, int startRow, int endRow, int startColum, int endColum) {
        int rowCount = endRow - startRow + 1;
        int columCount = endColum - startColum + 1;
        int blockSize = rowCount * columCount;
        if (startRow < 0 || endRow >= row || startColum < 0 || endColum >= colum) {
            throw new IllegalArgumentException("startRow:" + startRow + " endRow:" + endRow
                    + " startColum:" + startColum + " endColum:" + endColum);
        }
        for (int i = startRow * colum, j = 0; j < blockSize; i++, j++) {
            setTile(i, tile);
        }
    }

    public int getIdlePosition(int start) {
        int result = -1;
        for (int i = start; i < mCellCount; i++) {
            if (getTile(i) == null) {
                result = i;
                break;
            }
        }
        return result;
    }
    public TileInfo[] getTiles() {
        return mTiles;
    }

    public TileInfo getTile(int position) {
        return mTiles[position];
    }
    public int getCellCapacity() {
        return mCellCount;
    }
    public boolean isFull() {
        return mTiles[mCellCount - 1] != null;
    }
    public void clearCell(int position) {
        setTile(position, null);
    }

    public void clearAllCell() {
        if (mTiles != null) {
            for (int i = 0; i < mTiles.length; i++) {
                mTiles[i] = null;
            }
        }
    }

    public void reset() {
    	clearAllCell();
    	mTiles = null;    	
    }
}
