package com.chaojishipin.sarrs.bean;


import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 *  批量操作收藏
 *
 */
public class BatichFavoriteInfos implements LetvBaseBean {
    private int code;
	private String cost;
	private String data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
