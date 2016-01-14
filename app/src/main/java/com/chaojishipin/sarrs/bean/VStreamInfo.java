package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

public class VStreamInfo implements LetvBaseBean {

	/**
	 * 码流信息
	 */
	private static final long serialVersionUID = -8143889673099639354L;
	
	/**
	 * 码流类型
	 */
	private String streamType;
	
	/**
	 * 主地址
	 */
	private String mainUrl;
	
	/**
	 * 备用地址
	 */
	private String backUrl1;
	private String backUrl0;
	
	private String vtype;
	
	public String getStreamType() {
		return streamType;
	}
	public void setStreamType(String streamType) {
		this.streamType = streamType;
	}
	public String getMainUrl() {
		return mainUrl;
	}
	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}
	public String getBackUrl1() {
		return backUrl1;
	}
	public void setBackUrl1(String backUrl1) {
		this.backUrl1 = backUrl1;
	}
	public String getBackUrl0() {
		return backUrl0;
	}
	public void setBackUrl0(String backUrl0) {
		this.backUrl0 = backUrl0;
	}
	public String getVtype() {
		return vtype;
	}
	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

}
