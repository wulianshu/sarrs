package com.chaojishipin.sarrs.thirdparty.umeng;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by vicky on 15/10/29.
 */
public class UMengAnalysis {

    public static void setCatchUncaughtExceptions(boolean var0)
    {
        MobclickAgent.setCatchUncaughtExceptions(var0);
    }

    public static void onResume(Activity activity)
    {
        MobclickAgent.onResume(activity);
    }

    public static void onPause(Activity activity)
    {
        MobclickAgent.onPause(activity);
    }
}
