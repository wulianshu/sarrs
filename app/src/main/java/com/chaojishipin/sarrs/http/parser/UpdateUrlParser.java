package com.chaojishipin.sarrs.http.parser;
import com.chaojishipin.sarrs.bean.UpdateUrlInfo;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

import org.json.JSONObject;

/**
 *  外网视频请求解析类
 *  @author xll
 */
public class UpdateUrlParser extends ResponseBaseParser<UpdateUrlInfo> {

    @Override
    public UpdateUrlInfo initialParse(String data) throws JsonCanNotParseException, DataIsNullException, ParseException, DataIsErrException, DataNoUpdateException {
        return super.initialParse(data);
    }

    @Override
    public UpdateUrlInfo parse(JSONObject main) throws Exception {
        UpdateUrlInfo info=new UpdateUrlInfo();
        LogUtil.e("xll",""+main.toString());
        return info;
    }

}


