package per.awing.myui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Window;

import java.util.List;

import per.awing.myui.launcher.LauncherContainerView;
import per.awing.myui.launcher.PageIndicatorView;
import per.awing.myui.launcher.TileAdapter;
import per.awing.myui.launcher.TileInfo;
import per.awing.myui.util.MLOG;
import per.awing.myui.util.SystemUtil;

public class MyuiMainActivity extends Activity {
    private static final String TAG = "MyuiMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_myui_main);
        createLauncherView();
    }

    private void createLauncherView() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        TileAdapter tileAdapter = new TileAdapter();
        TileInfo multiWin = new TileInfo();
        multiWin.name = "Mult-Window";
        multiWin.component = SystemUtil.MULTI_WINDOW_COMPONET;
        for (int i = 0; i < infos.size(); i++) {
            ResolveInfo resolveInfo = infos.get(i);
            ApplicationInfo ai = resolveInfo.activityInfo.applicationInfo;
            if (ai.icon != 0 && ai.labelRes != 0) {
            	// don't show myself
            	// multi-window use same icon with current app
                if (getPackageName().equals(resolveInfo.activityInfo.packageName)) {
                	multiWin.icon = ai.loadIcon(pm);
                } else {
	                TileInfo ti = new TileInfo();
	                ti.name = ai.loadLabel(pm).toString();
	                ti.icon = ai.loadIcon(pm);
	                tileAdapter.addTile(ti);
	                ti.component = new ComponentName(resolveInfo.activityInfo.packageName,
	                		resolveInfo.activityInfo.name);
                }
            }
        }
        //tileAdapter.addTile(multiWin);
        LauncherContainerView container = (LauncherContainerView) findViewById(R.id.launcher_container);
        container.setAdapter(tileAdapter);
        PageIndicatorView indicator = (PageIndicatorView) findViewById(R.id.page_indicator);
        indicator.setHost(container);
    }
}
