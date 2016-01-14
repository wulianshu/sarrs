package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.RankList;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by wulianshu on 2015/8/26.
 */
public class RankListParser extends ResponseBaseParser<SarrsArrayList> {

    private final String TAG = "RankListParser";
    @Override
    public SarrsArrayList parse(JSONObject data) throws Exception {
        //如果请求数据成功
//        RankList rankList = new RankList();
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            SarrsArrayList list = new SarrsArrayList();
            JSONArray topicArray = data.optJSONArray("items");
            Log.e(TAG, topicArray.toString());
            int topicSize = topicArray.length();
            for (int i = 0; i < topicSize; i++) {
                RankList ranklist = new RankList();
                JSONObject topicObj = topicArray.optJSONObject(i);
                ranklist.setTitle(topicObj.optString("title"));
                ranklist.setPlay_count(topicObj.optString("play_count"));
                ranklist.setRid(topicObj.optString("rid"));
                ranklist.setImage(topicObj.optString("image"));
                ranklist.setLabel(topicObj.optString("label"));
                ranklist.setTitleitems(topicObj.optString("items"));
                list.add(ranklist);
            }
//            rankList.setItems(list);
            return list;
        }
        return null;
    }

}