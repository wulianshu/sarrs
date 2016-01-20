package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.RankListDetail;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class HistoryRecordParser extends ResponseBaseParser<SarrsArrayList> {
    private final String TAG = "HistoryRecordParser";
    @Override
    public SarrsArrayList parse(JSONObject data) throws Exception {
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            SarrsArrayList list = new SarrsArrayList();
            JSONArray topicArray = data.optJSONArray("items");
            Log.e(TAG,topicArray.toString());
            int topicSize = topicArray.length();
            for (int i = 0; i < topicSize; i++) {
                HistoryRecord historyRecord = new HistoryRecord();
                JSONObject topicObj = topicArray.optJSONObject(i);
                historyRecord.setTimestamp(topicObj.optString("timestamp"));
                historyRecord.setId(topicObj.optString("id"));
                historyRecord.setTitle(topicObj.optString("title"));
                historyRecord.setSource(topicObj.optString("source"));
                historyRecord.setCategory_name(topicObj.optString("category_name"));
                historyRecord.setPlay_time(topicObj.optString("play_time"));
                historyRecord.setGvid(topicObj.optString("gvid"));
                historyRecord.setImage(topicObj.optString("image"));
                historyRecord.setCategory_id(topicObj.optString("category_id"));
                historyRecord.setContent_type(topicObj.optString("content_type"));
                historyRecord.setContent_type(topicObj.optString("durationtime"));
                historyRecord.setUrl(topicObj.optString("url"));
                list.add(historyRecord);
            }
            return list;
        }
        return null;
    }
}