package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SearchSuggestDataList;
import com.chaojishipin.sarrs.bean.SearchSuggestInfos;
import com.chaojishipin.sarrs.bean.SearchSuggest_playinfo;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.letv.http.bean.LetvBaseBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daipei on 2015/6/18.
 */

public class SearchSuggestParser extends ResponseBaseParser<SearchSuggestInfos> {

    @Override
    protected JSONObject getData(String data) throws JSONException {
        JSONObject jsonObjectdata = new JSONObject(data);
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(jsonObjectdata.optString("status"))) {
            return super.getData(data);
        } else {
            throw new JSONException("data is null");
        }
    }

    @Override
    public SearchSuggestInfos parse(JSONObject data) throws Exception {
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            SearchSuggestInfos mInfos = JsonUtil.parseObject(data.toString(), SearchSuggestInfos.class);
            SarrsArrayList<SearchSuggestDataList> items = mInfos.getItems();
            for (LetvBaseBean item : items
                 ) {
                /**
                 * \u0001 \u0002 是用来标红，暂时先过滤掉
                 */
                String title = ((SearchSuggestDataList)item).getTitle();
                title = title.replace("\u0001", "");
                title = title.replace("\u0002", "");
                ((SearchSuggestDataList)item).setTitle(title);
            }
            return mInfos;
        }
        return null;
    }
}
