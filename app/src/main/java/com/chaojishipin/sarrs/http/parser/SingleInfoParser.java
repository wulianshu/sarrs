package com.chaojishipin.sarrs.http.parser;

import android.text.TextUtils;
import android.util.Base64;

import com.chaojishipin.sarrs.bean.SingleInfo;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *  外网单视频请求解析类
 */
public class SingleInfoParser extends ResponseBaseParser<SingleInfo> {

    @Override
    protected JSONObject getData(String data) throws JSONException {

        byte[] decodeData = Base64.decode(data, Base64.DEFAULT);
        String result = Utils.AES256_decode(decodeData, ConstantUtils.AES_KEY);
        if (!TextUtils.isEmpty(result)) {
            LogUtil.e(" outSite ：result ",""+result);
            return new JSONObject(result);
        }
        return null;

    }


    @Override
    public SingleInfo parse(JSONObject main) throws Exception {
        SingleInfo single = null;
        JSONObject  data=main.getJSONObject("data");
        if (null != data) {

            single = new SingleInfo();
                    JSONObject m3u8apiObj = data.optJSONObject("m3u8_api");
                    if (null != m3u8apiObj) {
                        HashMap<String, String> m3u8apiMap = new HashMap<String, String>(3);
                        setMapVaule(m3u8apiMap, m3u8apiObj);
                        single.setM3u8apiMap(m3u8apiMap);
                    }
                       single.setUrl(data.optString("url"));

                    JSONObject mp4apiObj = data.optJSONObject("mp4_api");
                    if (null != mp4apiObj) {
                        HashMap<String, String> mp4ApiMap = new HashMap<String, String>(3);
                        setMapVaule(mp4ApiMap, mp4apiObj);
                        single.setMp4apiMap(mp4ApiMap);
                    }

                    JSONObject m3u8PlayObj = data.optJSONObject("m3u8_play_url");
                    if (null != m3u8PlayObj) {
                        HashMap<String, String> m3u8PlayMap = new HashMap<String, String>(3);
                        setMapVaule(m3u8PlayMap, m3u8PlayObj);
                        single.setM3u8PlayMap(m3u8PlayMap);
                    }

                    JSONObject mp4PlayObj = data.optJSONObject("mp4_play_url");
                    if (null != mp4PlayObj) {
                        HashMap<String, String> mp4PlayMap = new HashMap<String, String>(3);
                        setMapVaule(mp4PlayMap, mp4PlayObj);
                        single.setMp4PlayMap(mp4PlayMap);
                    }

                    JSONObject m3u8paramObj = data.optJSONObject("m3u8_param");
                    if (null != m3u8paramObj) {
                        HashMap<String, String> m3u8paramMap = new HashMap<String, String>(3);
                        setMapVaule(m3u8paramMap, m3u8paramObj);
                        single.setM3u8paramMap(m3u8paramMap);
                    }

                    JSONObject mp4paramObj = data.optJSONObject("mp4_param");
                    if (null != mp4paramObj) {
                        HashMap<String, String> mp4paramMap = new HashMap<String, String>(3);
                        setMapVaule(mp4paramMap, mp4paramObj);
                        single.setMp4paramMap(mp4paramMap);
                    }

                    single.setSnifferUrl(data.getString("url"));
        }
        return single;
    }


    private void setMapVaule(HashMap<String, String> map, JSONObject obj) {
        map.put(PlayerUtils.SMOOTHURL, obj.optString(PlayerUtils.SMOOTHURL));
        map.put(PlayerUtils.STANDARDURL, obj.optString(PlayerUtils.STANDARDURL));
        map.put(PlayerUtils.HIGHURL, obj.optString(PlayerUtils.HIGHURL));
    }
}
