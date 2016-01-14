package com.chaojishipin.sarrs.http.parser;

import android.text.TextUtils;

import com.chaojishipin.sarrs.bean.UpdateSnifferInfo;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.net.URLDecoder;

import org.json.JSONObject;
/**
 *  播放器获取本地js代码
 *  @author xll
 *
 * */

public class UpdateSnifferParser extends ResponseBaseParser<UpdateSnifferInfo> {

	private static final String CODE = "code";
	private static final String VERSION = "version";

	@Override
	public UpdateSnifferInfo parse(JSONObject data) throws Exception {

		UpdateSnifferInfo updateInfo = new UpdateSnifferInfo();
		LogUtil.e("xll","NEW jsCode parse : "+data.toString());

		String version = data.getString(VERSION);
		
		String code = null;
		try {
			// decode 处理不了‘+’ 号
			//String decodeStr=data.getString(CODE).replace("+","%20");
			//code = URLDecoder.decode(decodeStr,"UTF-8");
			code=data.getString("code");
			LogUtil.e("xll","js code parse : "+code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(code)) {
			updateInfo.setCode(code);
			updateInfo.setVersion(version);
		} 
		return updateInfo;
	}
}
