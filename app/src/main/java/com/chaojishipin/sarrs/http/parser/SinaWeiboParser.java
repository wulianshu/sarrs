package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.thirdparty.Utils;
import com.chaojishipin.sarrs.thirdparty.WeiboInfo;
import com.chaojishipin.sarrs.thirdparty.WeiboUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
*   Created by xll 2015 06 07
*   新浪微博登陆授权解析接口
* */
public class SinaWeiboParser {
    public static List<WeiboInfo> parse(String json) throws Exception {
        List<WeiboInfo> ret = new ArrayList<WeiboInfo>();
        JSONObject data = new JSONObject(json);
        if (data.has("error")) {
            throw new Exception("Error in Weibo return.");
        }
        JSONArray array = data.getJSONArray("statuses");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            WeiboInfo info = new WeiboInfo();
            Calendar c = Utils.parseTime(object.optString("created_at"), "EEE MMM dd HH:mm:ss Z yyyy");
            info.setCreate_at(c==null?0:c.getTimeInMillis());
            if (object.has("retweeted_status")) {
                info.setTitle(object.getJSONObject("retweeted_status").optString("text"));
                info.setImageUrl(object.getJSONObject("retweeted_status").optString("original_pic"));
            } else {
                info.setTitle(object.optString("text"));
                info.setImageUrl(object.optString("original_pic"));
            }
            WeiboUser user = new WeiboUser();
            user.setName(object.getJSONObject("user").optString("screen_name"));
            user.setAvatar(object.getJSONObject("user").optString("avatar_large"));
            info.setUser(user);
            ret.add(info);

        }
        return ret;
    }

}
