package com.chaojishipin.sarrs.bean;


import com.letv.http.bean.LetvBaseBean;

import java.util.List;

/**
 * 收藏
 *
 */
public class CheckFavorite implements LetvBaseBean {

	private int code;

	public boolean isExists() {
		return isExists;
	}

	public void setIsExists(boolean isExists) {
		this.isExists = isExists;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	private boolean isExists;
}
