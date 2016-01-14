package com.chaojishipin.sarrs.bean;


import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * 收藏
 *
 */
public class FavoriteInfos implements LetvBaseBean {

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String status;

	public List<String> getBaseIdList() {
		return baseIdList;
	}

	public void setBaseIdList(List<String> baseIdList) {
		this.baseIdList = baseIdList;
	}

	private List<String> baseIdList;
	public List<Favorite> getFs() {
		return fs;
	}

	public void setFs(List<Favorite> fs) {
		this.fs = fs;
	}

	List<Favorite> fs;
}
