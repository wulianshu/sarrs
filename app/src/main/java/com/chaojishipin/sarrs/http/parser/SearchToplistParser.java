package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by vicky on 15/11/17.
 */
public class SearchToplistParser extends ResponseBaseParser<SarrsArrayList> {
    @Override
    public SarrsArrayList parse(JSONObject data) throws Exception {
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            SarrsArrayList titles = new SarrsArrayList<>();
            JSONArray toplistArray = data.optJSONArray("items");
            int topicSize = toplistArray.length();
            for (int i = 0; i < topicSize; i++) {
                JSONObject topistObj = toplistArray.optJSONObject(i);
                titles.add(topistObj.optString("title"));
            }
            return titles;
        }
        return null;
    }

}
