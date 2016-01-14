package com.chaojishipin.sarrs.thirdparty;

import android.app.Activity;


public class LoginManager {
    // weibo
    public static final String TYPE_SINA_WEIBO = "3";
    public static final String TYPE_TENCENT_WEIBO = "tencentweibo";
    // qq
    public static final String TYPE_QQ = "2";
    // weixin
    public static final String TYPE_WEIXIN = "1";
    // phone
    public static final String TYPE_PHONE = "0";
    // 未登录
    public static final String TYPE_NONE = "none";
    private Activity mActivity;

    public LoginManager(Activity activity) {
        mActivity = activity;
    }

    public static LoginManager from(Activity activity) {
        return new LoginManager(activity);
    }

    public LoginHelper getHelper(String type) {
        if (type.equals(TYPE_SINA_WEIBO)) {
            return new WeiboLoginHelper(mActivity);
        } else if (type.equals(TYPE_TENCENT_WEIBO)) {
          // return new TWeiboLoginHelper(mActivity);
        } else if (type.equals(TYPE_QQ)) {
            return new QQLoginHelper(mActivity);
        } else if (type.equals(TYPE_WEIXIN)) {
            return new WeiXinLoginHelper(mActivity);
        } else if(type.equals(TYPE_NONE)){
            return null;
        } else {
            throw new IllegalArgumentException("unknown login type");
        }
        return null;
    }
}
