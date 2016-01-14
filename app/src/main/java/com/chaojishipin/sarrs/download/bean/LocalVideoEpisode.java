package com.chaojishipin.sarrs.download.bean;

import com.chaojishipin.sarrs.bean.VideoItem;
import com.letv.http.bean.LetvBaseBean;

public class LocalVideoEpisode extends VideoItem implements LetvBaseBean {
	/**
	 * 本地视频
	 * @author daipei
	 */
	private static final long serialVersionUID = 6046632453129007556L;

	private String name;
	private String subName;
	private String play_url;
	private String porder;
	private String site;
	private String aid;
	private String id;
	private String vt;
	private String downType;
	private String image;
	@Override
	public String getImage() {
		return image;
	}

	@Override
	public void setImage(String image) {
		this.image = image;
	}



	public String getPlayCount() {
		return playCount;
	}

	public void setPlayCount(String playCount) {
		this.playCount = playCount;
	}

	private String playCount;

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	private String cid;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private String des;
	
	public String getDownType() {
		return downType;
	}

	public void setDownType(String downType) {
		this.downType = downType;
	}

	public String getVt() {
		return vt;
	}

	public void setVt(String vt) {
		this.vt = vt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getPorder() {
		return porder;
	}

	public void setPorder(String porder) {
		this.porder = porder;
	}

	private String mid;

	
	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public String getPlay_url() {
		return play_url;
	}

	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}

}
