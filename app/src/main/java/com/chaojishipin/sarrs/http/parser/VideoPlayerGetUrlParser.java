package com.chaojishipin.sarrs.http.parser;

import android.text.TextUtils;
import android.util.Base64;
import com.chaojishipin.sarrs.bean.VStreamInfo;
import com.chaojishipin.sarrs.bean.VStreamInfoList;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daipei on 2015/6/18.
 */

public class VideoPlayerGetUrlParser extends ResponseBaseParser<VStreamInfoList> {

    @Override
    protected JSONObject getData(String data) throws JSONException {

        byte[] decodeData = Base64.decode(data, Base64.DEFAULT);
        String result = Utils.AES256_decode(decodeData, ConstantUtils.AES_KEY);
        JSONObject jsonObjectdata = new JSONObject(result);
        LogUtil.e("xll"," letv "+result);
        //如果请求数据不成功
        if (!ConstantUtils.RESULT_OK.equals(jsonObjectdata.optString("status"))) {
            throw new JSONException("data is null");
        } else {
            return jsonObjectdata;
        }

    }

    @Override
    public VStreamInfoList parse(JSONObject data) throws Exception {
        VStreamInfoList infoList = new VStreamInfoList();
        JSONArray arr_infos = data.getJSONArray("data");
        JSONArray arr_streams = arr_infos.optJSONObject(0).optJSONArray("infos");
        String  source=arr_infos.optJSONObject(0).optString("source");
        if(source.equalsIgnoreCase(PlayerUtils.SIET_LETV)){
            // 乐视源数据解析

            for (int i = 0; i < arr_streams.length(); i++) {
                JSONObject stream = arr_streams.getJSONObject(i);
                VStreamInfo streamInfo = new VStreamInfo();
                streamInfo.setBackUrl0(stream.optString("backUrl0"));
                streamInfo.setBackUrl1(stream.optString("backUrl1"));
                streamInfo.setMainUrl(stream.optString("mainUrl"));
                LogUtil.d("dyf", "*************************" + stream.optString("mainUrl"));
                String vType = stream.optString("vtype");
                if(!TextUtils.isEmpty(vType)) {
                    streamInfo.setVtype(stream.optString("vtype"));
                    infoList.put(stream.optString("vtype"), streamInfo);
                }
            }

        }





        return infoList;
    }
}
