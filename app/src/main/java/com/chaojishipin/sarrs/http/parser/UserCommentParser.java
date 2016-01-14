package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.UserCommentInfo;
import com.chaojishipin.sarrs.utils.JsonUtil;

import org.json.JSONObject;

/**
 * Created by wangyemin on 2015/9/29.
 */
public class UserCommentParser extends ResponseBaseParser<UserCommentInfo> {
    @Override
    public UserCommentInfo parse(JSONObject data) throws Exception {
        UserCommentInfo commentInfo = null;
        if ("0".equalsIgnoreCase(data.optString("code"))) {
            String dataStr = data.optString("data");
            commentInfo = JsonUtil.parseObject(dataStr, UserCommentInfo.class);
        }
        return commentInfo;
    }
}
