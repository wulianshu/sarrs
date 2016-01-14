package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.ModifyInfo;
import org.json.JSONObject;

/**
*   Created by xll 2015 06 07
*   修改用户资料
* */
public class ModifyUserParser extends ResponseBaseParser<ModifyInfo> {
    @Override
    public ModifyInfo parse(JSONObject data) throws Exception {
        ModifyInfo u=new ModifyInfo();
        if (data.has("code") && data.getString("code").equalsIgnoreCase("0")) {
            // 修改资料成功
            u.setState(1);
        }
        return u;
    }


}
