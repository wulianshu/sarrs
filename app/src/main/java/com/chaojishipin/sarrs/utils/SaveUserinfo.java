package com.chaojishipin.sarrs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.LoginManager;
import com.chaojishipin.sarrs.thirdparty.LoginUtils;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;

/**
 * Created by wulianshu on 2015/11/16.
 */
public class SaveUserinfo {
    public final static String TAG_LOGINFO = "logininfo";
    public final static String TAG_ISLOGIN = "islogin";
    public final static String TAG_IJSON = "ujson";
//    public final static String TAG_LOGIN_TYPE = "login_type";
    public static void saveuserinfo2Sharepre(Context context,BaseUserInfo userInfo,boolean islogin){
        //缓存本地
        SharedPreferences mySharedPreferences= context.getSharedPreferences(TAG_LOGINFO,
                context.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putBoolean(TAG_ISLOGIN, islogin);
        editor.putString(TAG_IJSON, JsonUtil.toJSONString(userInfo));
//        editor.putString(TAG_LOGIN_TYPE, logtype);
        //提交当前数据
        editor.commit();
    }

    /**
     * TODO：获取登录的用户信息 赋值给单例 UserLoginState
     */
    public static void getLoginuserinfo(Context context){
        SharedPreferences mySharedPreferences= context.getSharedPreferences(TAG_LOGINFO,
                context.MODE_PRIVATE);
        BaseUserInfo  userInfo = new BaseUserInfo();
        if(mySharedPreferences.getBoolean(TAG_ISLOGIN,false)){
            String ujson =  mySharedPreferences.getString(TAG_IJSON,"");
            if(ujson!=null) {
                userInfo = JsonUtil.parseObject(ujson, BaseUserInfo.class);
            }
            UserLoginState.getInstance().setLogin(true);
            UserLoginState.getInstance().setUserInfo(userInfo);
            LoginUtils.setLastLoginType(userInfo.getType());
        }
    }

    public static void logout(Context context){
        SharedPreferences mySharedPreferences= context.getSharedPreferences(TAG_LOGINFO,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
