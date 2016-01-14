package com.chaojishipin.sarrs.download.http.parser;


import com.chaojishipin.sarrs.download.bean.UpdateSnifferInfo;
import com.chaojishipin.sarrs.utils.StringUtil;

import org.json.JSONObject;

import java.net.URLDecoder;

public class UpdateSnifferParser extends LetvMobileParser<UpdateSnifferInfo> {

	private static final String CODE = "code";
	private static final String VERSION = "version";

	@Override
	public UpdateSnifferInfo parse(JSONObject data) throws Exception {

		UpdateSnifferInfo updateInfo = new UpdateSnifferInfo();

		String version = data.getString(VERSION);
		
		String code = null;
		try {
			code = URLDecoder.decode(data.getString(CODE), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!StringUtil.isEmpty(code)) {
			updateInfo.setCode(code);
			updateInfo.setVersion(version);
		} 
		return updateInfo;
	}
}
