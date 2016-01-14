package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.InterestEntity;
import com.chaojishipin.sarrs.bean.InterestRecommend;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 兴趣推荐解析
 * Created by wangyemin on 2015/10/13.
 */
public class InterestRecommendParser extends ResponseBaseParser<InterestRecommend> {
    @Override
    public InterestRecommend parse(JSONObject data) throws Exception {
//        SarrsArrayList<InterestEntity> list = new SarrsArrayList<>();
        InterestRecommend interestRecommend = null;
        if (data.has("status") && data.optString("status").equalsIgnoreCase("200")) {
//            list = (SarrsArrayList) JsonUtil.parseArray(data.optString("items"), InterestEntity.class);
            interestRecommend = JsonUtil.parseObject(data.toString(), InterestRecommend.class);
        }
        return interestRecommend;
    }
}
