package com.chaojishipin.sarrs.download.bean;

import com.chaojishipin.sarrs.bean.Episode;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;

public class PlaySrcBean implements LetvBaseBean {



	private static final long serialVersionUID = 4415785537434820471L;
	private String sitename;
	private String site;
	private String defLink;
	private String logo;
	private int id;
	private ArrayList<Episode> episodes = new ArrayList<Episode>();
	private String episodeNum;
	private String nowEpisode;
	private String aid;


	public String getSitename() {
		return sitename;
	}
	
	public void setSitename(String sitename) {
		this.sitename = sitename;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Episode> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(ArrayList<Episode> episodes) {
		this.episodes = episodes;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getDefLink() {
		return defLink;
	}

	public void setDefLink(String defLink) {
		this.defLink = defLink;
	}
	public String getEpisodeNum() {
		return episodeNum;
	}
	public void setEpisodeNum(String episodeNum) {
		this.episodeNum = episodeNum;
	}

	public String getNowEpisode() {
		return nowEpisode;
	}

	public void setNowEpisode(String nowEpisode) {
		this.nowEpisode = nowEpisode;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
