package com.chaojishipin.sarrs.bean;

import java.util.ArrayList;

import com.letv.http.bean.LetvBaseBean;

public class CloudDiskBean implements LetvBaseBean{

	/**
	 * zhangshuo
	 * 2014年7月31日 下午4:34:01
	 */
	private static final long serialVersionUID = 1L;
	
	private String user_id;
	
	private String video_id;
	
	private String media_id;
	
	private String default_play;
	
	private String video_duration;
	
	private String video_name;
	
	private String type;
	
	private String main_url;
	
	private String vwidth;
	
	private String vheight;
	
	private String gbr;
	
	private String storePath;
	
	private String vtype;
	
	private String definition;
	
	private ArrayList<String> mPlayUrls;

	public ArrayList<String> getmPlayUrls() {
		return mPlayUrls;
	}

	public void setmPlayUrls(ArrayList<String> mPlayUrls) {
		this.mPlayUrls = mPlayUrls;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public String getDefault_play() {
		return default_play;
	}

	public void setDefault_play(String default_play) {
		this.default_play = default_play;
	}

	public String getVideo_duration() {
		return video_duration;
	}

	public void setVideo_duration(String video_duration) {
		this.video_duration = video_duration;
	}

	public String getVideo_name() {
		return video_name;
	}

	public void setVideo_name(String video_name) {
		this.video_name = video_name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMain_url() {
		return main_url;
	}

	public void setMain_url(String main_url) {
		this.main_url = main_url;
	}

	public String getVwidth() {
		return vwidth;
	}

	public void setVwidth(String vwidth) {
		this.vwidth = vwidth;
	}

	public String getVheight() {
		return vheight;
	}

	public void setVheight(String vheight) {
		this.vheight = vheight;
	}

	public String getGbr() {
		return gbr;
	}

	public void setGbr(String gbr) {
		this.gbr = gbr;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

}
