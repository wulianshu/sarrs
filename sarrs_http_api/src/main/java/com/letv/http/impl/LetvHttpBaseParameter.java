package com.letv.http.impl;

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
/**
 * 请求参数组装类
 * 封装：
 * 请求地址
 * 参数
 * 解析器
 * 刷新ID
 * */
public abstract class LetvHttpBaseParameter<T extends LetvBaseBean , D , P> {
	/**
	 * 请求类型
	 * */
	public interface Type {
		public int POST = 0x2001;
		public int GET = 0x2002;
	}
	/**
	 * baseUrl请求地址
	 * */
	private String baseUrl;
	
	/**
	 * 参数
	 * */
	private P params ;

	/**
	 * 请求完成后的更新ID
	 * */
	private int updataId = -1;
	
	/**
	 * 请求方式 post 或  get
	 * */
	private int type;
	
	/**
	 * 请求结束后的解析器
	 * */
	private LetvBaseParser<T , D> parser ;
	
	/**
	 * 请求回调，会在请求之前与请求之后回调相应函数
	 * */
	private LetvHttpParameterCallback callback ;
	
	private String mReferer;
	
	private String mUserAgent;

    public LetvHttpBaseParameter(String baseUrl, P params , int type ,LetvBaseParser<T , D> parser , int updataId) {
		this.baseUrl = baseUrl;
		this.params = params;
		this.type = type;
		this.parser = parser ;
		this.updataId = updataId ;
	}

	/**
	 * 得到baseUrl请求地址
	 * */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * 设置baseUrl请求地址
	 * */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * 得到参数
	 * */
	public P getParams() {
		return params;
	}

	/**
	 * 设置参数
	 * */
	public void setParams(P params) {
		this.params = params;
	}

	/**
	 * 得到请求完成后的更新ID
	 * */
	public int getUpdataId() {
		return updataId;
	}

	/**
	 * 设置请求完成后的更新ID
	 * */
	public void setUpdataId(int updataId) {
		this.updataId = updataId;
	}

	/**
	 * 得到请求结束后的解析器
	 * */
	public LetvBaseParser<T , D> getParser() {
		return parser;
	}

	/**
	 * 设置请求结束后的解析器
	 * */
	public void setParser(LetvBaseParser<T , D> parser) {
		this.parser = parser;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public LetvHttpParameterCallback getCallback() {
		return callback;
	}

	public void setCallback(LetvHttpParameterCallback callback) {
		this.callback = callback;
	}

	/**
	 * 将参数组装成字符串
	 * */
	public abstract StringBuilder encodeUrl() ;
	
	public String getmReferer() {
        return mReferer;
    }

    public void setmReferer(String mReferer) {
        this.mReferer = mReferer;
    }
    
    public String getmUserAgent() {
        return mUserAgent;
    }

    public void setmUserAgent(String mUserAgent) {
        this.mUserAgent = mUserAgent;
    }
}
