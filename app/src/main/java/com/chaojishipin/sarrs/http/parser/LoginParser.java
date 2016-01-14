package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;

import org.json.JSONObject;

/*
*   Created by xll 2015 06 07
*   登陆解析类
* */
public class LoginParser extends ResponseBaseParser<BaseUserInfo> {
    @Override
    public BaseUserInfo parse(JSONObject data) throws Exception {
        BaseUserInfo u = new BaseUserInfo();
        if (data.has("code")) {
            if (data.has("data")) {
                JSONObject main = data.optJSONObject("data");
                if (main.has("nick_name")) {
                    u.setName(main.getString("nick_name"));
                }
                if (main.has("img_url")) {
                    u.setAvatar(main.getString("img_url"));
                }
                if (main.has("uid")) {
                    u.setUid(main.getString("uid"));
                }
                if (main.has("sex")) {
                    u.setGender(Integer.valueOf(main.getString("sex")));
                }
                // 设置用户是否第一次登录
                if (main.has("is_first") && main.getString("is_first").equalsIgnoreCase("0")) {
                    u.setIsFirst(true);
                } else {
                    u.setIsFirst(false);
                }
                if (main.has("token")) {
                    u.setToken(main.getString("token"));
                }
            }
            if (data.has("code")) {
                u.setErrorCode(data.getString("code"));
            }
        }
        return u;
    }


}
