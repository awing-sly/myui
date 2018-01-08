package per.awing.myui.launcher;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import android.view.View;

public class TileInfo {
    private static int sNextIndex = 0;
    public int index;
    public String name;
    public Drawable icon;
    public int left;
    public int top;
    public int width;
    public int height;
    public View view;
    public Object extras;
    public ComponentName component;
    public int page;
    public int position;
    public TileInfo() {
        index = sNextIndex++;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TileInfo:\n");
        sb.append("index:").append(index).append("\n");
        sb.append("name:").append(name).append("\n");
        sb.append("page:").append(page).append("\n");
        sb.append("positon:").append(position).append("\n");
        sb.append("left:").append(left).append("\n");
        sb.append("top:").append(top).append("\n");
        sb.append("width:").append(width).append("\n");
        sb.append("height:").append(height).append("\n");
        sb.append("component:").append(component).append("\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TileInfo) {
            TileInfo ti = (TileInfo)o;
            return ti.index == index;
        }
        return false;
    }
}
