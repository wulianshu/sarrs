package com.chaojishipin.sarrs.uploadstat;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by wulianshu on 2016/1/28.
 */
public class UmengPagePath {
    public static void beginpage(String pagename,Context context){
        MobclickAgent.onPageStart(pagename); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(context);          //统计时长
    }
    public static void endpage(String pagename,Context context){
        MobclickAgent.onPageEnd(pagename); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(context);
    }
}
