package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SearchResultDataList;
import com.chaojishipin.sarrs.bean.SearchResultInfos;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;

import org.json.JSONObject;

/**
 * Created by daipei on 2015/6/15.
 */
public class SearchResultParser extends ResponseBaseParser<SearchResultInfos> {

    private SearchResultInfos mInfos;

//    @Override
//    protected JSONObject getData(String data) throws JSONException {
//        FileCacheManager fileCacheManager = FileCacheManager.getInstance();
//        //进行数据的缓存
//        fileCacheManager.writeDataToFile(ConstantUtils.FILECACHE_SLIDINGMENU_DATA, data);
//        return super.getData(data);
//    }


    public SearchResultParser(SearchResultInfos infos) {
        super();
        mInfos = infos;
    }

    @Override
    public SearchResultInfos parse(JSONObject data) throws Exception {
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            String resultStr = data.toString();
            /**
             * \u0001 \u0002 是用来标红，暂时先过滤掉
             */
            resultStr = resultStr.replace("\\u0001", "");
            resultStr = resultStr.replace("\\u0002", "");
            SearchResultInfos infos = JsonUtil.parseObject(resultStr, SearchResultInfos.class);

            if (null == mInfos){
                mInfos =new SearchResultInfos(infos);
                SearchResultDataList searchResultDataList = new  SearchResultDataList();
                searchResultDataList.setView_type(1);//手动添加第一个数据，用来显示head
                SarrsArrayList<SearchResultDataList> items = mInfos.getItems();
                items.add(0, searchResultDataList);

            }else {
                mInfos.getItems().addAll(infos.getItems());
            }

            return mInfos;
        }
        return null;
    }
}
