package per.awing.myui.util;

import android.util.Log;

import per.awing.myui.GlobalConfig;

import static  per.awing.myui.GlobalConfig.LOG_ENABLED;

public class MLOG {
    public static void d(String tag, String msg) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_DEBUG)) {
            return;
        }
        Log.d(tag, msg);
    }
    public static void d(String tag, String msg, Throwable tr) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_DEBUG)) {
            return;
        }
        Log.d(tag, msg, tr);
    }
    public static void i(String tag, String msg) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_INFO)) {
            return;
        }
        Log.i(tag, msg);
    }
    public static void i(String tag, String msg, Throwable tr) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_INFO)) {
            return;
        }
        Log.i(tag, msg, tr);
    }
    public static void w(String tag, String msg) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_WARN)) {
            return;
        }
        Log.w(tag, msg);
    }
    public static void w(String tag, String msg, Throwable tr) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_WARN)) {
            return;
        }
        Log.w(tag, msg, tr);
    }
    public static void e(String tag, String msg) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_ERROR)) {
            return;
        }
        Log.e(tag, msg);
    }
    public static void e(String tag, String msg, Throwable tr) {
        if (!is_log_policy_enabled(GlobalConfig.LOG_ERROR)) {
            return;
        }
        Log.e(tag, msg, tr);
    }
    
    private static boolean is_log_policy_enabled(boolean logConfig) {
        return LOG_ENABLED && logConfig;
    }
}
