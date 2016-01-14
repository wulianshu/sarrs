package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.LoginManager;
import com.chaojishipin.sarrs.thirdparty.WeiXinToken;

import org.json.JSONObject;

/*
*   Created by xll 2015 06 07
*   微信登陆授权返回Token信息解析接口
* */
public class WeiXinTokenParser extends ResponseBaseParser<WeiXinToken> {

    @Override
    public WeiXinToken parse(JSONObject data) throws Exception {
        Log.i("weixinlogin", data.toString());
        WeiXinToken ret = new WeiXinToken();
        ret.access_token = data.getString("access_token");
        ret.refresh_token = data.getString("refresh_token");
        ret.openId = data.getString("openid");
        return ret;
    }

}