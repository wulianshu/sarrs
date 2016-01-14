package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.VerifyCode;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
*   Created by xll 2015 06 07
*   获取验证码解析类
* */
public class VerifyCodeParser extends ResponseBaseParser<VerifyCode> {
    @Override
    public VerifyCode parse(JSONObject data) throws Exception {
        VerifyCode code=new VerifyCode();
        if (data.has("code") && data.getString("code").equalsIgnoreCase("0")) {
            if (data.has("data")) {
                JSONObject main = data.optJSONObject("data");
                if (main.has("sm_code")) {
                    code.setCode(main.getString("sm_code"));
                }
            }

        }
        return code;
    }
}
