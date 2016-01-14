package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.Upgradinfo;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by wulianshu on 2016/1/11.
 */
public class UpgradinfoParser extends ResponseBaseParser<SarrsArrayList> {
    private final String TAG = "UpgradinfoParser";
    @Override
    public SarrsArrayList parse(JSONObject data) throws Exception {
        //如果请求数据成功
        SarrsArrayList list = new SarrsArrayList();
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            Upgradinfo upgradinfo = JsonUtil.parseObject(data.toString(),Upgradinfo.class);
            list.add(upgradinfo);
            }
        return list;
    }
}
