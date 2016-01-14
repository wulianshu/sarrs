package com.chaojishipin.sarrs.download.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 用于提供客户端解析各家视频流地址用到的参数
 * @author zhangshuo
 *
 */
public class UrlAnalysisParamter implements LetvBaseBean {
	
	/**
	 * zhangshuo
	 * 2014年6月13日 下午2:09:41
	 */
	private static final long serialVersionUID = 1L;
	
	private String api_key;

	private String vid;
	
	private String type;
	
	private String kk;
	
	private String rid;
	
	private String ip;
	
	private String path;
	
	private String gcid;
	
	private String tvid;
	
	private String url_key;
	
	private String code;
	
	private String file;
	
	private String mid;
	
	private String c1;
	
	private String c2;
	
	private String pid;
	
	private String plat;
	
	private String sver;
	
	private String partner;
	
	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getSver() {
		return sver;
	}

	public void setSver(String sver) {
		this.sver = sver;
	}

	public String getPlat() {
		return plat;
	}

	public void setPlat(String plat) {
		this.plat = plat;
	}

	public String getApi_key() {
		return api_key;
	}

	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKk() {
		return kk;
	}

	public void setKk(String kk) {
		this.kk = kk;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getGcid() {
		return gcid;
	}

	public void setGcid(String gcid) {
		this.gcid = gcid;
	}

	public String getTvid() {
		return tvid;
	}

	public void setTvid(String tvid) {
		this.tvid = tvid;
	}

	public String getUrl_key() {
		return url_key;
	}

	public void setUrl_key(String url_key) {
		this.url_key = url_key;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getC1() {
		return c1;
	}

	public void setC1(String c1) {
		this.c1 = c1;
	}

	public String getC2() {
		return c2;
	}

	public void setC2(String c2) {
		this.c2 = c2;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	
}
