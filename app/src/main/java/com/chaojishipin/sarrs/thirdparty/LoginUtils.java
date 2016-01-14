package com.chaojishipin.sarrs.thirdparty;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class LoginUtils {
    public static void setLastLoginType(String type){
        SharedPreferences sp = ChaoJiShiPinApplication.getInstatnce().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        Editor e = sp.edit();
        e.putString("type", type).commit();
    }
    public static String getLastLoginType(){
        SharedPreferences sp =  ChaoJiShiPinApplication.getInstatnce().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        return sp.getString("type", "none");
    }
    public static void clearAllLoginInfo(Context context){
        UserLoginState.getInstance().setLogin(false);
        AccessTokenKeeper.clear(context);
        //腾讯微博的登陆清空
       // Util.clearSharePersistent(context);
        SharedPreferences sp =  ChaoJiShiPinApplication.getInstatnce().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        sp.edit().clear().commit();
        ACache.get(context).remove(Constant.CACHE.LOGGED_USER_INFO+Constant.CACHE.VERSION);
        assert null == ACache.get(context).getAsObject(Constant.CACHE.LOGGED_USER_INFO+Constant.CACHE.VERSION);
    }
    public static boolean requestLastLoginUserInfo(Activity activity,LoginListener listener){
        String type = getLastLoginType();
        if(TextUtils.equals("none", type)){
            return false;
        }
        else if(TextUtils.equals(LoginManager.TYPE_SINA_WEIBO, type)){
            loadSinaWeiboUserInfo(activity,listener);
        }
        else if(TextUtils.equals(LoginManager.TYPE_TENCENT_WEIBO, type)){
           // loadTencentWeiboUserInfo(activity,listener);
        }
        return true;
    }
    public static void loadSinaWeiboUserInfo(Activity activity,LoginListener listener){
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity);
        if (accessToken != null) {
            // 直接加载图片
            if (!accessToken.getUid().equals("")) {
                WeiboLoginHelper helper = (WeiboLoginHelper)( LoginManager.from(activity).getHelper(LoginManager.TYPE_SINA_WEIBO));
                helper.requestUserInfo(accessToken,listener);
            }
        }
    }
  /*  public static void loadTencentWeiboUserInfo(Activity activity,LoginListener listener){
            TWeiboLoginHelper helper = (TWeiboLoginHelper) LoginManager.from(activity).getHelper(LoginManager.TYPE_TENCENT_WEIBO);
            String token = Util.getSharePersistent(activity, "ACCESS_TOKEN");
            if(!TextUtils.isEmpty(token)){
                helper.requestUserInfo(token, listener);
            }
        }*/
}
