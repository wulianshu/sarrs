package com.chaojishipin.sarrs.thirdparty;


import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.letv.http.bean.LetvBaseBean;

/**
 * 用户登录状态信息单例，用作登录状态查询<br>
 * 退出登录及登录时必须修改此单例状态
 *
 * @author liuliwei
 */
public class UserLoginState {
    private static UserLoginState instance;

    public static UserLoginState getInstance() {
        if (instance == null) {
            instance = new UserLoginState();
        }
        return instance;
    }

    private boolean isLogin = false;
    private String token = "";


    private BaseUserInfo user = new BaseUserInfo();

    public String getToken() {
        return user.getToken();
    }

    public String getUidAndType() {
        if (isLogin && null != user && !TextUtils.isEmpty(user.getUid())) {
            StringBuilder sb = new StringBuilder(user.getType());
            sb.append("_");
            sb.append(user.getUid());
            return sb.toString();
        }
        return "";
    }

    public BaseUserInfo getUserInfo() {
        return this.user;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 设置用户信息，并缓存
     *
     * @param userInfo
     */
    public void setUserInfo(BaseUserInfo userInfo) {
        ACache cache = ACache.get(ChaoJiShiPinApplication.getInstatnce());
        if (null == cache.getAsObject(Constant.CACHE.LOGGED_USER_INFO + Constant.CACHE.VERSION)) {
            cache.put(Constant.CACHE.LOGGED_USER_INFO + Constant.CACHE.VERSION, userInfo, 29 * 24 * 3600);
        }
        this.user = userInfo;
    }

}
