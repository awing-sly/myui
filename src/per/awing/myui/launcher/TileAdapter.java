package per.awing.myui.launcher;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class TileAdapter extends BaseAdapter {
    List<TileInfo> mTileList = new ArrayList<TileInfo>();
    public void addTile(TileInfo ti) {
        if (ti != null) {
            mTileList.add(ti);
        }
    }

    @Override
    public int getCount() {
        return mTileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TileInfo ti = mTileList.get(position);
        TileView tv = (TileView) ti.view;
        if (tv == null) {
            tv = new TileView(parent.getContext(), ti);
            ti.view = tv;
        }
        return tv;
    }

}
