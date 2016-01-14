package com.chaojishipin.sarrs.bean;

import java.util.ArrayList;
import java.util.List;

import com.letv.http.bean.LetvBaseBean;

public class AlbumUpdateInfoList implements LetvBaseBean {

	/**
	 * 
	 */

	private static final long serialVersionUID = 2111486008205656016L;
	private List<AlbumUpdateInfoBean> albumUpdateInfoList = new ArrayList<AlbumUpdateInfoBean>();
	

	public List<AlbumUpdateInfoBean> getAlbumUpdateInfoList() {
		return albumUpdateInfoList;
	}

	public void setAlbumUpdateInfoList(List<AlbumUpdateInfoBean> albumUpdateInfoList) {
		this.albumUpdateInfoList = albumUpdateInfoList;
	}
	
	public void addAlbumUpdateInfo(AlbumUpdateInfoBean albumUpdateInfoBean){
		
		albumUpdateInfoList.add(albumUpdateInfoBean);
	}
	
	
	
}
