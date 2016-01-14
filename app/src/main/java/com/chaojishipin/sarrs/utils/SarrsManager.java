package com.chaojishipin.sarrs.utils;

import android.content.Context;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.dao.DBHelper;
import com.chaojishipin.sarrs.dao.DatabaseManager;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.iflytek.cloud.SpeechUtility;
import com.letv.component.player.core.LetvMediaPlayerManager;

/**
 * sarrs管理类
 * @author daipei
 */
public class SarrsManager {
    private static boolean mLog_debug;
    private static boolean mHttp_test;

    /**
     * 播放器APPKEY
     */
    private final static String PLAYER_APPKEY = "01012020301000100010";

    private final static String APP_ID = "1";

    /**
     * 播放器PCODE
     */
    private final static String PLAYER_PCODE = "010110698";

    /**
     * 初始化http等
     * @param context
     */
    public static void init(Context context) {
        context = context.getApplicationContext();
        HttpManager.init(context);
        initXunFei(context);
        DatabaseManager.initializeInstance(new DBHelper(context));
        // 初始化播放器模块
        LetvMediaPlayerManager.getInstance().init(context, PLAYER_APPKEY, APP_ID, PLAYER_PCODE, Utils.getClientVersionName());
        // 初始化CDEManager 启动CDE
        CDEManager cdeManager = CDEManager.getInstance(context);
        cdeManager.startCde();
    }

    /**
     * 初始化讯飞语音
     */
    private static void initXunFei(Context context){
        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid
        SpeechUtility.createUtility(context, "appid=" + context.getString(R.string.xunfei_app_id));
    }

    /**
     * log开关
     * @param isDebug
     */
    public static void setLogDebug(boolean isDebug) { mLog_debug = isDebug; }

    public static boolean isLogDebug(){ return mLog_debug; }

    /**
     * http请求正式与测试接口开关
     * @param isTest
     */
    public static void setHttpTest(boolean isTest){ mHttp_test = isTest; }

    public static boolean isHttpTest(){ return mHttp_test; }


}
