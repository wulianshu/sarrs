package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.UpgradeInfo;
import com.chaojishipin.sarrs.bean.UploadFile;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.letv.http.bean.LetvBaseBean;

import org.json.JSONObject;

/**
 * Created by vicky on 15/11/15.
 */
public class UploadFileParser extends ResponseBaseParser<UploadFile> implements LetvBaseBean {

    @Override
    public UploadFile parse(JSONObject data) throws Exception {
        UploadFile uploadFile = null;
        if(data !=null) {
//        if (data.has("status") && data.optString("status").equalsIgnoreCase("1")) {
            uploadFile = JsonUtil.parseObject(data.toString(), UploadFile.class);
//        }
        }
        Log.i("upload", " " + uploadFile);
        return uploadFile;
    }
}
