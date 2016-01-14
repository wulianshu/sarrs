package com.chaojishipin.sarrs.http.parser;


import android.util.Log;

import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wulianshu on 2015/8/24.
 */
public class TopicParser extends ResponseBaseParser<SarrsArrayList> {
    private String TAG = "TopicParser";
    @Override
    public SarrsArrayList parse(JSONObject data) throws Exception {
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            SarrsArrayList topicList = new SarrsArrayList();
            JSONArray topicArray = data.optJSONArray("items");
            Log.e(TAG,topicArray.toString());
            int topicSize = topicArray.length();
            for (int i = 0; i < topicSize; i++) {
                Topic topic = new Topic();
                JSONObject topicObj = topicArray.optJSONObject(i);

                topic.setData_count(topicObj.optInt("data_count"));
                topic.setDescription(topicObj.optString("description"));
                topic.setTitle(topicObj.optString("title"));
                topic.setImage(topicObj.optString("image"));
                topic.setLabel(topicObj.optString("label"));
                topic.setSource(topicObj.optString("source"));
                topic.setPlay_count(topicObj.optString("play_count"));
                topic.setTid(topicObj.optString("tid"));
                topicList.add(topic);
            }
            return topicList;
        }
        return null;
    }

//
}
