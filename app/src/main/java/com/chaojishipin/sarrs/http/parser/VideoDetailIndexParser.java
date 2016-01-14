package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.VideoDetailIndex;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
*   Created by xll 2015 06 07
*   半屏播放解析类
* */
public class VideoDetailIndexParser extends ResponseBaseParser<VideoDetailIndex> {
    @Override
    public VideoDetailIndex parse(JSONObject data) throws Exception {
        VideoDetailIndex indexItem=new VideoDetailIndex();
        if (data.has("status") && data.getString("status").equalsIgnoreCase(ConstantUtils.REQUEST_SUCCESS)) {
            if (data.has("item")) {
                JSONObject itemJson = data.optJSONObject("item");
                if (itemJson.has("index")) {
                    indexItem.setIndex(itemJson.getInt("index"));
                }

            }


        }


        return indexItem;
    }
}
