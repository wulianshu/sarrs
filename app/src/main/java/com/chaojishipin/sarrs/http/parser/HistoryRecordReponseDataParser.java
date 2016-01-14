package com.chaojishipin.sarrs.http.parser;


import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.HistoryRecordResponseData;
import com.chaojishipin.sarrs.bean.LiveClassifyInfos;
import com.chaojishipin.sarrs.bean.LiveInfos;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 仅作为demo，需要删除
 */
public class HistoryRecordReponseDataParser extends ResponseBaseParser<HistoryRecordResponseData> {
    @Override
    public HistoryRecordResponseData parse(JSONObject data) throws Exception {
//         System.out.println(data.toString());
//        HistoryRecordResponseData historyRecordResponseData = (HistoryRecordResponseData) JsonUtil.parse(data.toString());
         return null;
    }

    @Override
    public HistoryRecordResponseData initialParse(String data) throws JsonCanNotParseException, DataIsNullException, ParseException, DataIsErrException, DataNoUpdateException {
//        System.out.println("returndata:"+data.toString());
//        HistoryRecordResponseData historyRecordResponseData = (HistoryRecordResponseData) JsonUtil.parse(data.toString());
        return null;
    }
}
