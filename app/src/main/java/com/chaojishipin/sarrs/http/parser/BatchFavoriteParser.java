package com.chaojishipin.sarrs.http.parser;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.chaojishipin.sarrs.bean.BatichFavoriteInfos;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

import org.json.JSONObject;

/**
 * Created by xll on 2015/10/10.
 */
public class BatchFavoriteParser extends ResponseBaseParser<BatichFavoriteInfos> {

    @Override
    public BatichFavoriteInfos initialParse(String data) throws JsonCanNotParseException, DataIsNullException, ParseException, DataIsErrException, DataNoUpdateException {
        BatichFavoriteInfos c=null;
        LogUtil.e("xll","resposne "+data);
        com.alibaba.fastjson.JSONObject root= JSON.parseObject(data);
        if (ConstantUtils.REQUEST_SUCCESS.equals(root.getString("status"))) {
            c=new BatichFavoriteInfos();
            c.setCode(root.getIntValue("code"));

        }
        return c;
    }

    @Override
    public BatichFavoriteInfos parse(JSONObject data) throws Exception {
        BatichFavoriteInfos c=null;
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
             c=new BatichFavoriteInfos();
            if(data.has("code")){
                c.setCode(data.getInt("code"));

            }
        }
        return c;
    }
}
