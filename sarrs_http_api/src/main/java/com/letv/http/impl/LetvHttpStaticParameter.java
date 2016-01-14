package com.letv.http.impl;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

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
public class LetvHttpStaticParameter<T extends LetvBaseBean , D> extends LetvHttpBaseParameter<T, D, ArrayList<BasicNameValuePair>>{

	/**
	 * 静态请求结尾
	 * */
	private String end ; 
	
	public LetvHttpStaticParameter(String head, String end , ArrayList<BasicNameValuePair> params, LetvBaseParser<T, D> parser,int updataId) {
		super(head, params, Type.GET, parser, updataId);
		this.end = end ;
	}

	@Override
	public StringBuilder encodeUrl() {
		
		ArrayList<BasicNameValuePair> params = getParams() ;
		StringBuilder sb = new StringBuilder();
		if (params == null || params.isEmpty()) {
			return sb ;
		}
		for (BasicNameValuePair key : params) {
			if(!TextUtils.isEmpty(key.getName()) && !TextUtils.isEmpty(key.getValue())){
				sb.append("/");
				sb.append(key.getName());
				sb.append("/");
				sb.append(key.getValue());
			}
		}
		sb.append(end);
		return sb;
	}
}
