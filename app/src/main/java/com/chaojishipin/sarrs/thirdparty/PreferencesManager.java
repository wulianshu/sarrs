package com.chaojishipin.sarrs.thirdparty;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

public class PreferencesManager {

    private Context context;
    private static final String FIRSTLAUNCH = "firstlaunch";
    private static final String GUIDE = "guide";
    // 是否接收push通知
    public static final String IS_RECEIVE_PUSH_NOTIFICATION = "is_receive_push_notification";
    // push通知相关数据保存
    private static final String PUSH = "push";
    private static final String LAST_REFRESH_TIME = "last_refresh_time";

    private PreferencesManager(Context context) {
        this.context = context;
    }

    private static PreferencesManager instance =
            new PreferencesManager(ChaoJiShiPinApplication.getInstatnce());

    public static PreferencesManager getInstance() {
        return instance;
    }

    /**
     * 是否第一次启动
     *
     * @return
     */
    public boolean isFirstLaunch() {
        SharedPreferences sp = context.getSharedPreferences(FIRSTLAUNCH, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        boolean firstlaunch = sp.getBoolean("firstlaunch", true);
        if (firstlaunch) {
            editor.putBoolean("firstlaunch", false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否第一次全屏引导
     *
     * @return
     */
    public boolean isFullScreenGuided() {
        SharedPreferences sp = context.getSharedPreferences(GUIDE, Context.MODE_PRIVATE);
        boolean firstGuide = sp.getBoolean("full_screen", true);
        return firstGuide;
    }
    public void setFullScreenGuided() {
        SharedPreferences sp = context.getSharedPreferences(GUIDE, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("full_screen", false);
        editor.commit();
    }

    public boolean getApplyAutoPlay() {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(ChaoJiShiPinApplication.getInstatnce());
        boolean apply_auto_play = sp.getBoolean("apply_auto_play", true);
        return apply_auto_play;
    }

    /**
     * 推送是否开启
     * */
    public boolean isPush() {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(ChaoJiShiPinApplication.getInstatnce());
        return sp.getBoolean("apply_push", true);
    }


    public long getPushTime() {
        SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

        long time = sp.getLong("time", 0);

        return time;
    }

    public void savePushDistance(int time) {
        SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

        sp.edit().putInt("distance", time).commit();
    }

    public int getPushDistance() {
        SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

        int time =
                sp.getInt("distance", Constant.DEBUG ? 20
                        : PushConfiguration.DEFAULT_PUSH_TIME_SPAN);

        return time;
    }

     class PushConfiguration {
        public static final int DEFAULT_PUSH_TIME_SPAN = 60 * 20;
    }


    public void savePushTime(long time) {
        SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

        sp.edit().putLong("time", time).commit();
    }

    /**
     * 启动程序的次数
     * */
    public int isshowTimes() {
        SharedPreferences sp = context.getSharedPreferences(FIRSTLAUNCH, Context.MODE_PRIVATE);
        int launchTime = sp.getInt("is_show_score_pop", 0);
        sp.edit().putInt("is_show_score_pop", launchTime + 1).commit();
        return launchTime;
    }
    public void setRefreshTime(String channelId){
        SharedPreferences sp = context.getSharedPreferences(LAST_REFRESH_TIME, Context.MODE_PRIVATE);
        sp.edit().putLong(channelId,System.currentTimeMillis()).commit();
    }
    public long getRefreshTime(String channelId){
        SharedPreferences sp = context.getSharedPreferences(LAST_REFRESH_TIME, Context.MODE_PRIVATE);
        return sp.getLong(channelId,System.currentTimeMillis());
    }
}