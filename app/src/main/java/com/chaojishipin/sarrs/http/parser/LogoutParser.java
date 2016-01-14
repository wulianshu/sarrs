package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.LogOutInfo;
import org.json.JSONObject;

/*
*   Created by xll 2015 06 07
*   退出登陆
* */
public class LogoutParser extends ResponseBaseParser<LogOutInfo> {
    @Override
    public LogOutInfo parse(JSONObject data) throws Exception {
        LogOutInfo u=new LogOutInfo();
        if (data.has("code") && data.getString("code").equalsIgnoreCase("0")) {
             u.setState(0);
        }
        return u;
    }


}
