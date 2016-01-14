package com.chaojishipin.sarrs.bean;


import com.letv.http.bean.LetvBaseBean;

/**
 * 收藏
 *
 */
public class Favorite implements LetvBaseBean {
	private int id;
	private String type;
	private String cid;
	private String img;
	private String aid;
	private String gvid;



	private String source;
	private String title;
	private String totalepisode;
	private String latestepisode;
	private String history;
	private int isend;
	private String totaltime;
	private String totalspecail;
	private boolean isCheck;
	private String createTime;
    // dataCount 专题数据类型
	private String dataCount;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}



	public String getDataCount() {
		return dataCount;
	}

	public void setDataCount(String dataCount) {
		this.dataCount = dataCount;
	}


	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	private String createDate;
	public boolean isShowTag() {
		return isShowTag;
	}

	public void setIsShowTag(boolean isShowTag) {
		this.isShowTag = isShowTag;
	}

	private boolean isShowTag;


	public String getBaseId() {
		return baseId;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	// 服务端返回Id
	private String baseId;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	private String tid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getGvid() {
		return gvid;
	}

	public void setGvid(String gvid) {
		this.gvid = gvid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTotalepisode() {
		return totalepisode;
	}

	public void setTotalepisode(String totalepisode) {
		this.totalepisode = totalepisode;
	}

	public String getLatestepisode() {
		return latestepisode;
	}

	public void setLatestepisode(String latestepisode) {
		this.latestepisode = latestepisode;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public int getIsend() {
		return isend;
	}

	public void setIsend(int isend) {
		this.isend = isend;
	}

	public String getTotaltime() {
		return totaltime;
	}

	public void setTotaltime(String totaltime) {
		this.totaltime = totaltime;
	}

	public String getTotalspecail() {
		return totalspecail;
	}

	public void setTotalspecail(String totalspecail) {
		this.totalspecail = totalspecail;
	}




	public Favorite(){
		super();
	}
	/**
	 *  专辑
	 * */
	public Favorite(String type,String cid,String aid,String gvid,String title,String latestepisode,String totalepisode,int isend,String history,String img ,String createTime,String createDate,String source){
		this.type=type;
		this.cid=cid;
		this.aid=aid;
		this.gvid=gvid;
		this.title=title;
		this.latestepisode=latestepisode;
		this.totalepisode=totalepisode;
		this.history=history;
		this.img=img;
		this.isend=isend;
		this.createTime=createTime;
		this.createDate=createDate;
		this.source=source;

	}

	/**
	 *  专题
	 * */
	public Favorite(String type,String cid,String title,String totalspecail,String img ,String tid,String createTime,String createDate,String source){
		this.type=type;
		this.cid=cid;
		this.title=title;
		this.tid=tid;
		this.totalspecail=totalspecail;
		this.img=img;
		this.createTime=createTime;
		this.createDate=createDate;
		this.source=source;

	}
/**
 *  所有类型
 * */
	public Favorite(int id, String type, String cid, String img, String aid, String gvid, String title, String totalepisode, String latestepisode, String history, int isend, String totaltime, String totalspecail,String tid,String source) {
		this.id = id;
		this.type = type;
		this.cid = cid;
		this.img = img;
		this.aid = aid;
		this.gvid = gvid;
		this.title = title;
		this.totalepisode = totalepisode;
		this.latestepisode = latestepisode;
		this.history = history;
		this.isend = isend;
		this.totaltime = totaltime;
		this.totalspecail = totalspecail;
		this.tid=tid;
		this.source=source;
	}

	/**
	 *  单视频
	 * */
	public Favorite(String type,String cid,String aid,String gvid,String title,String totaltime,String img ,String history,String createTime,String createDate,String source){
		this.type=type;
		this.cid=cid;
		this.gvid=gvid;
		this.aid=aid;
		this.title=title;
	    this.totaltime=totaltime;
		this.img=img;
		this.history=history;
		this.createTime=createTime;
		this.createDate=createDate;
        this.source=source;

	}


	public boolean isCheck() {
		return isCheck;
	}

	public void setIsCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
