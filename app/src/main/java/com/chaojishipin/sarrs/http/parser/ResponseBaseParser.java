package com.chaojishipin.sarrs.http.parser;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.parse.LetvMainParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析基类
 * @author daipei
 */
public abstract class ResponseBaseParser<T extends LetvBaseBean> extends LetvMainParser<T, JSONObject> {

    @Override
    protected final boolean canParse(String data) {
        return true;
    }

    @Override
    protected JSONObject getData(String data) throws JSONException {
        JSONObject object = new JSONObject(data);
        return object;
    }

}