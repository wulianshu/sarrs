package com.chaojishipin.sarrs.download.http.parser;


import com.chaojishipin.sarrs.bean.SingleInfo;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class SingleInfoParser extends LetvMobileParser<SingleInfo> {

    @Override
    public SingleInfo parse(JSONObject data) throws Exception {
        SingleInfo single = null;
        if (null != data) {
            single = new SingleInfo();
            if (data.has("filepath")) {
                JSONObject filepathObj = data.optJSONObject("filepath");
                if (null != filepathObj) {
                    JSONObject m3u8apiObj = filepathObj.optJSONObject("m3u8api");
                    if (null != m3u8apiObj) {
                        HashMap<String, String> m3u8apiMap = new HashMap<String, String>(3);
                        setMapVaule(m3u8apiMap, m3u8apiObj);
                        single.setM3u8apiMap(m3u8apiMap);
                    }

                    JSONObject mp4apiObj = filepathObj.optJSONObject("mp4api");
                    if (null != mp4apiObj) {
                        HashMap<String, String> mp4ApiMap = new HashMap<String, String>(3);
                        setMapVaule(mp4ApiMap, mp4apiObj);
                        single.setMp4apiMap(mp4ApiMap);
                    }

                    JSONObject m3u8PlayObj = filepathObj.optJSONObject("m3u8");
                    if (null != m3u8PlayObj) {
                        HashMap<String, String> m3u8PlayMap = new HashMap<String, String>(3);
                        setMapVaule(m3u8PlayMap, m3u8PlayObj);
                        single.setM3u8PlayMap(m3u8PlayMap);
                    }

                    JSONObject mp4PlayObj = filepathObj.optJSONObject("mp4");
                    if (null != mp4PlayObj) {
                        HashMap<String, String> mp4PlayMap = new HashMap<String, String>(3);
                        setMapVaule(mp4PlayMap, mp4PlayObj);
                        single.setMp4PlayMap(mp4PlayMap);
                    }

                    JSONObject m3u8paramObj = filepathObj.optJSONObject("m3u8param");
                    if (null != m3u8paramObj) {
                        HashMap<String, String> m3u8paramMap = new HashMap<String, String>(3);
                        setMapVaule(m3u8paramMap, m3u8paramObj);
                        single.setM3u8paramMap(m3u8paramMap);
                    }

                    JSONObject mp4paramObj = filepathObj.optJSONObject("mp4param");
                    if (null != mp4paramObj) {
                        HashMap<String, String> mp4paramMap = new HashMap<String, String>(3);
                        setMapVaule(mp4paramMap, mp4paramObj);
                        single.setMp4paramMap(mp4paramMap);
                    }
                }
            }
        }
        return single;
    }

    private void setMapVaule(HashMap<String, String> map, JSONObject obj) {
        map.put(PlayerUtils.SMOOTHURL, obj.optString(PlayerUtils.SMOOTHURL));
        map.put(PlayerUtils.STANDARDURL, obj.optString(PlayerUtils.STANDARDURL));
        map.put(PlayerUtils.HIGHURL, obj.optString(PlayerUtils.HIGHURL));
    }
}
