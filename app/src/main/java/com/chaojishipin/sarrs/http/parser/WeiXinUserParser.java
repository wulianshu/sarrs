package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.LoginManager;
import com.chaojishipin.sarrs.thirdparty.Utils;
import com.chaojishipin.sarrs.thirdparty.WeiboInfo;
import com.chaojishipin.sarrs.thirdparty.WeiboUser;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
*   Created by xll 2015 06 07
*   微信登陆授权返回用户信息解析接口
* */
public class WeiXinUserParser extends ResponseBaseParser<BaseUserInfo> {
    private String token;

    public WeiXinUserParser(String refresh_token) {
        super();
        this.token = refresh_token;
    }

    @Override
    public BaseUserInfo parse(JSONObject data) throws Exception {
        Log.i("weixinlogin", data.toString());
        BaseUserInfo user = new BaseUserInfo();
        user.setAvatar(data.getString("headimgurl"));
        user.setName(data.getString("nickname"));
        user.setOpenId(data.getString("openid"));
        user.setGender(Integer.valueOf(data.getString("sex")));
        user.setType(LoginManager.TYPE_WEIXIN);
        //user.setToken(token);
        return user;
    }
}