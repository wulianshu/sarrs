package com.chaojishipin.sarrs.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import com.letv.pp.func.CdeHelper;
import com.letv.pp.service.CdeService;

public class CDEManager {

    private static final CDEManager mInstance = new CDEManager();

    private CdeHelper mCdeHelper;
    
    private static Context mContext;

    private CDEManager() {

    }
    
    public static CDEManager getInstance(Context context) {
        mContext = context;
        return mInstance;
    }
    

    /**
     * 启动cde服务 获取CdeHelper实例： getInstance(Context context, String params)---默认先升级；
     * getInstance(Context context, String params, boolean isFirstUpgrade)
     * isFirstUpgrade为true标示先升级在启动，为false表示静默升级
     */
    public void startCde() {
        // 注册事件接受CDE完成升级加载
        initCdeHelper();
        this.mCdeHelper.start();
    }
    
    private void initCdeHelper() {
        // app_id 表示当前 APP 的标识，保持和之前 UTP 的一致即可。
        // channel_default_multi 表示是否可以多频道,默认不允许(app_id=10除外),1-允许;0-不允许
        // 目前分配APPID 快看影视为50 影视大全为700 sarrs 731
        String appId = "731"; // 填写对应应用的 ID，具体参考开发接口说明文档&log_level= 0
        String params = "port=6990L&app_id=" + appId + "&channel_default_multi=0&log_level=0";
        this.mCdeHelper = CdeHelper.getInstance(mContext, params);
    }
    
    /**
     * 停止cde服务
     */
    public void stopCde() {
        if (this.mCdeHelper != null) {
            this.mCdeHelper.stop();
        }
        releaseData();
    }
     
    private void releaseData() {
        if (null != mContext) {
            mContext = null;
        }
    }
    
    public CdeHelper getmCdeHelper() {
        if (null == mCdeHelper) {
            initCdeHelper();
        }
        return mCdeHelper;
    }
    
}
