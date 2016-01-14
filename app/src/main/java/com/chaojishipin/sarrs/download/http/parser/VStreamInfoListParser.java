package com.chaojishipin.sarrs.download.http.parser;

import android.text.TextUtils;
import android.util.Base64;


import com.chaojishipin.sarrs.bean.VStreamInfo;
import com.chaojishipin.sarrs.bean.VStreamInfoList;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VStreamInfoListParser extends LetvMobileParser<VStreamInfoList> {
	
	private final String SUCCESS = "1001";
	public static final String RESULT_OK="200";
	
	public VStreamInfoListParser() {
		super();
	}
	
	

	@Override
    public JSONObject getData(String data) throws JSONException {
        byte[] decodeData = Base64.decode(data, Base64.DEFAULT);
        String result = Utils.AES256_decode(decodeData, Utils.AES_KEY);
        if (!TextUtils.isEmpty(result)) {
            return new JSONObject(result);
        }
        return null;
    }

    @Override
	public VStreamInfoList parse(JSONObject data) throws Exception {

		LogUtil.e("xll"," download  "+data.toString());
		if (!RESULT_OK.equals(data.optString("status"))) {
			return null;
		}
		VStreamInfoList infoList = new VStreamInfoList();

		JSONArray arry_data = data.optJSONArray("data");
        JSONObject dataitem=arry_data.getJSONObject(0);
		JSONArray arr_streams = dataitem.getJSONArray("infos");
		for (int i = 0; i < arr_streams.length(); i++) {
			JSONObject stream = arr_streams.getJSONObject(i);
			VStreamInfo streamInfo = new VStreamInfo();
			streamInfo.setBackUrl0(stream.optString("backUrl0"));
			streamInfo.setBackUrl1(stream.optString("backUrl1"));
			streamInfo.setMainUrl(stream.optString("mainUrl"));
			String vType = stream.optString("vtype");
			if(!TextUtils.isEmpty(vType)) {
				streamInfo.setVtype(stream.optString("vtype"));
				infoList.put(stream.optString("vtype"), streamInfo);
			}
		}
		return infoList;
	}

}
