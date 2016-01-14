package com.chaojishipin.sarrs.download.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;
import java.util.List;

public class PlaySrcList implements LetvBaseBean {

	/**
	 * 
	 */

	private static final long serialVersionUID = 2111486008205656012L;
	private List<PlaySrcBean> playSrcList = new ArrayList<PlaySrcBean>();
	

	public List<PlaySrcBean> getPlaySrcList() {
		return playSrcList;
	}

	public void setPlaySrcList(List<PlaySrcBean> playSrcList) {
		this.playSrcList = playSrcList;
	}
}
