package com.chaojishipin.sarrs.bean;


import com.letv.http.bean.LetvBaseBean;

public class AlbumUpdateInfoBean implements LetvBaseBean {



	private static final long serialVersionUID = 4415785537434820488L;
	

	private String episodeTotal;
	private String nowEpisode;
	private String aid;
	private String isend;

	public String getIsend(){
		
		return isend;
	}
	

	public String getNowEpisode() {
		return nowEpisode;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	

}
