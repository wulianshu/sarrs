package com.chaojishipin.sarrs.http.parser;


import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;

import com.chaojishipin.sarrs.bean.LiveClassifyInfos;
import com.chaojishipin.sarrs.bean.LiveInfos;

import java.util.ArrayList;

/**
 * 仅作为demo，需要删除
 */
public class LiveInfoParser extends ResponseBaseParser<LiveInfos> {
    @Override
    public LiveInfos parse(JSONObject data) throws Exception {
        LiveInfos liveInfos = null;
        if (data.has("data")) {
            liveInfos = new LiveInfos();
            JSONArray dataArray = data.optJSONArray("data");
            int infoSize = dataArray.length();
            ArrayList<LiveClassifyInfos> infoLists = new ArrayList<LiveClassifyInfos>(infoSize);
            for (int i = 0; i < infoSize; i++) {
                LiveClassifyInfos classifyInfo = new LiveClassifyInfos();
                // 先去直播分类信息
                JSONObject classifyObj = dataArray.optJSONObject(i);
                classifyInfo.setCheineseName(classifyObj.optString("chineseName"));
                classifyInfo.setIdentifier(classifyObj.optString("identifier"));
                if(!(TextUtils.isEmpty(classifyInfo.getCheineseName()) ||TextUtils.isEmpty(classifyInfo.getIdentifier()))){
                	infoLists.add(classifyInfo);
                }
	        }
            liveInfos.setAllInfos(infoLists);
        }
        return liveInfos;
    }
}
