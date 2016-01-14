package com.chaojishipin.sarrs.download.bean;

import com.letv.http.bean.LetvBaseBean;

public class UpdateSnifferInfo implements LetvBaseBean {

	/**
	 * js截流解析实体类
	 * @author daipei
	 * @since 2014年10月8日 15:29:27
	 */
	private static final long serialVersionUID = -8581188561716872054L;
	
	private String code;
	private String version;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
