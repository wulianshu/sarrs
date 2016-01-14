package com.letv.http.impl;

import java.net.URLEncoder;

import android.os.Bundle;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.parse.LetvBaseParser;
/**
 * 请求参数组装类
 * 封装：
 * 请求地址
 * 参数
 * 解析器
 * 刷新ID
 * */
public class LetvHttpParameter<T extends LetvBaseBean , D> extends LetvHttpBaseParameter<T, D, Bundle>{

	public LetvHttpParameter(String baseUrl, Bundle params, int type, LetvBaseParser<T, D> parser,int updataId) {
		super(baseUrl, params, type, parser, updataId);
	}

	@Override
	public StringBuilder encodeUrl() {
		StringBuilder sb = new StringBuilder();
		if (getParams() == null) {
			return sb ;
		}
		boolean first = true;
		for (String key : getParams().keySet()) {
			if (first) {
				if(getType() == Type.GET){
					sb.append("?");
				}
				first = false;
			} else {
				sb.append("&");
			}
			String pa = getParams().getString(key);
			if (pa != null) {
				try {
					sb.append(key + "=" + URLEncoder.encode(pa, "UTF-8"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				sb.append(key + "=");
			}
		}
		return sb;
	}
}
