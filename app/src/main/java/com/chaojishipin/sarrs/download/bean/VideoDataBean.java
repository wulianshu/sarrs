package com.chaojishipin.sarrs.download.bean;

import com.chaojishipin.sarrs.bean.Episode;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;

public class VideoDataBean implements LetvBaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6535666258339996109L;
	private float rating;
	private String subcategory;
	private String othername;
	private String episodes;
	private String tag;
	private String vt;
	private String sitename;
	private String category;
	private String categoryname;
	private String area;
	private String isend;
	private String subsrc;
	private String areaname;
	private String description;
	private String poster;
	private String shortdesc;
	private String starring;
	private String nowepisodes;
	private String englishname;
	private String subcategoryname;
	private String directoryname;
	private String src;
	private String name;
	private String url;
	private String starringname;
	private String releasedate;
	private String subname;
	private String directory;
	private String aid;
	private String vid;
	private String logo;
	private String mid;
	private String pls;
	private String shareLink = "";
	private PlaySrcBean playSrcBean;
	private ArrayList<Episode> episodeList = new ArrayList<Episode>();
	private PlaySrcList srcList = new PlaySrcList();
	private ArrayList<String> mPorderLists;//porder的集合，给播放器分页时使用
	
	public ArrayList<String> getmPorderLists() {
		return mPorderLists;
	}
	public void setmPorderLists(ArrayList<String> mPorderLists) {
		this.mPorderLists = mPorderLists;
	}
	public PlaySrcBean getPlaySrcBean() {
		return playSrcBean;
	}
	public void setPlaySrcBean(PlaySrcBean playSrcBean) {
		this.playSrcBean = playSrcBean;
	}
	
	public PlaySrcList getSrcList() {
		return srcList;
	}
	public void setSrcList(PlaySrcList srcList) {
		this.srcList = srcList;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getPls() {
		return pls;
	}
	public void setPls(String pls) {
		this.pls = pls;
	}
	
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	public String getSitename() {
		return sitename;
	}
	public void setSitename(String sitename) {
		this.sitename = sitename;
	}
	public String getOthername() {
		return othername;
	}
	public void setOthername(String othername) {
		this.othername = othername;
	}
	public String getEpisodes() {
		return episodes;
	}
	public void setEpisodes(String episodes) {
		this.episodes = episodes;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getVt() {
		return vt;
	}
	public void setVt(String vt) {
		this.vt = vt;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategoryname() {
		return categoryname;
	}
	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getIsend() {
		return isend;
	}
	public void setIsend(String isend) {
		this.isend = isend;
	}
	public String getSubsrc() {
		return subsrc;
	}
	public void setSubsrc(String subsrc) {
		this.subsrc = subsrc;
	}
	public String getAreaname() {
		return areaname;
	}
	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public String getShortdesc() {
		return shortdesc;
	}
	public void setShortdesc(String shortdesc) {
		this.shortdesc = shortdesc;
	}
	public String getStarring() {
		return starring;
	}
	public void setStarring(String starring) {
		this.starring = starring;
	}
	public String getNowepisodes() {
		return nowepisodes;
	}
	public void setNowepisodes(String nowepisodes) {
		this.nowepisodes = nowepisodes;
	}
	public String getEnglishname() {
		return englishname;
	}
	public void setEnglishname(String englishname) {
		this.englishname = englishname;
	}
	public String getSubcategoryname() {
		return subcategoryname;
	}
	public void setSubcategoryname(String subcategoryname) {
		this.subcategoryname = subcategoryname;
	}
	public String getDirectoryname() {
		return directoryname;
	}
	public void setDirectoryname(String directoryname) {
		this.directoryname = directoryname;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStarringname() {
		return starringname;
	}
	public void setStarringname(String starringname) {
		this.starringname = starringname;
	}
	public String getReleasedate() {
		return releasedate;
	}
	public void setReleasedate(String releasedate) {
		this.releasedate = releasedate;
	}
	public String getSubname() {
		return subname;
	}
	public void setSubname(String subname) {
		this.subname = subname;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public ArrayList<Episode> getEpisodeList() {
		return episodeList;
	}
	public void setEpisodeList(ArrayList<Episode> episodeList) {
		this.episodeList = episodeList;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getShareLink() {
		return shareLink;
	}
	public void setShareLink(String shareLink) {
		this.shareLink = shareLink;
	}

}
