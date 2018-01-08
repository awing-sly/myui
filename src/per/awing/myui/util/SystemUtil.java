package per.awing.myui.util;

import java.util.HashMap;
import java.util.Map;

import per.awing.myui.GlobalConfig;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
//import android.os.ServiceManager;
//import android.view.IWindowManager;

public class SystemUtil {
	public static final String TAG = "SystemUtil";
	private static final String SVK_MULTI_WINDOW = "multi_window";
    public static void requestMultiWindow(boolean enabled) {
        //IWindowManager wm= IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
        //try {
        //    wm.requestMultiWindow(enabled);
        //} catch (RemoteException ex) {
        //    MLOG.d(TAG, "fail to requestMultiWindow", ex);
        //    return;
        //}
        setMultiWindowMode(enabled);
    }

    public static final ComponentName MULTI_WINDOW_COMPONET =
    		new ComponentName(GlobalConfig.MY_PACKAGE,GlobalConfig.MY_PACKAGE + ".MultiWindow");

    private static Map<String, String> systemValues = new HashMap<String, String>();
    public static String getSystemValue(String key) {
    	return systemValues.get(key);
    }

    public static void setSystemValue(String key, String value) {
    	systemValues.put(key, value);
    }

    public static boolean isMultiWindowMode() {
    	String val = getSystemValue(SVK_MULTI_WINDOW);
    	if ("1".equals(val)) {
    		return true;
    	}
    	return false;
    }

    private static void setMultiWindowMode(boolean enabled) {
    	String val;
    	if (enabled) {
    		val = "1";
    	} else {
    		val = "0";
    	}
    	systemValues.put(SVK_MULTI_WINDOW, val);
    }
}
