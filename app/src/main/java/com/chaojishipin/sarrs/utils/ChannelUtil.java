package com.chaojishipin.sarrs.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by wulianshu on 2016/1/11.
 */
public class ChannelUtil {
    public static String getCurrentChannel(Context context){
        String channel = "";
        try {
            channel = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channel;
    }
}
