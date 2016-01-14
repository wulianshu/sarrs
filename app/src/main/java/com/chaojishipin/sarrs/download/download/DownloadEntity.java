package com.chaojishipin.sarrs.download.download;

import java.io.Serializable;

/**
 * 数据库下载的实体类，与之相关的类：MediaItem 和 DownloadData
 * @author daipei
 *
 */
public class DownloadEntity implements Serializable {
	private static final long serialVersionUID = 833033735525534615L;
	private String id; //V3之前是hashid,V3之后是serialId
	private String mid; //aid，专辑id
	private String medianame;
	private String taskname;
	private int status;	//下载状态
	private long fileSize;
//	private String url;//请求zeus得到的地址：request_downloadInfo_url
	private String downloadUrl;//真正的下载地址：download_url
	private String mediatype;
	private String displayName;
	private int index = 0;//集数，第几集	
	private String currClarity; //清晰度
	private String path; //下载文件存储路径
//	private String language;//下载语言
	private String downloadType = DownloadInfo.MP4;//CDN/P2P
	private String site;//区分letv和其他源
	private String letvMid;//存放letv源返回的mid，请求真实下载路径时使用
	private String request_site;//请求的源地址
	private String ifWatch; //是否观看过
	private int addTime;//添加时间
	private String folderName;//文件夹名字
	private String vt;//视频类型-电影、综艺等
	private String mp4api;//截流中使用
	private String m3u8api;//截流中使用
	private String snifferUrl;//截流中使用
	private String rule;//截流中使用,mp4对应rule
	private String m3u8Rule;//截流中使用，m3u8对应rule
//	private String cloudId;//云盘下载请求id
	private String src;//表示来源，乐视源=1，站外源=0
	private String globaVid;//唯一id，用于请求filepath
	private String m3u8Url;//临时记录m3u8的下载地址，无需存入数据库
	private String mp4Url;//临时记录mp4的下载地址，无需存入数据库
	private String image;//视频图片
	private boolean useUserAgent;  //是否使用UA, true:使用；false:不使用
	private String desc; //描述
	private int dataType; //如：1-专辑，2-视频，等
	private String cid;//channel_id

	public int getmIndex() {
		return mIndex;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public int getmKey() {
		return mKey;
	}

	public void setmKey(int mKey) {
		this.mKey = mKey;
	}

	private int mKey;//SparseArray中的key，实际就是第几个分页的key
	private int mIndex;//SparseArray中key对应的Arraylist的index
	
	public String getMp4Url() {
		return mp4Url;
	}
	public void setMp4Url(String mp4Url) {
		this.mp4Url = mp4Url;
	}
	public String getM3u8Url() {
		return m3u8Url;
	}
	public void setM3u8Url(String m3u8Url) {
		this.m3u8Url = m3u8Url;
	}
	public String getM3u8Rule() {
		return m3u8Rule;
	}
	public void setM3u8Rule(String m3u8Rule) {
		this.m3u8Rule = m3u8Rule;
	}
	public String getGlobaVid() {
		return globaVid;
	}
	public void setGlobaVid(String globaVid) {
		this.globaVid = globaVid;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}

	private String ext; //便于以后扩展，以便以后新增字段
	
//	public String getCloudId() {
//		return cloudId;
//	}
//	public void setCloudId(String cloudId) {
//		this.cloudId = cloudId;
//	}

	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getSnifferUrl() {
		return snifferUrl;
	}
	public void setSnifferUrl(String snifferUrl) {
		this.snifferUrl = snifferUrl;
	}

	public String getMp4api() {
		return mp4api;
	}
	public void setMp4api(String mp4api) {
		this.mp4api = mp4api;
	}
	public String getM3u8api() {
		return m3u8api;
	}
	public void setM3u8api(String m3u8api) {
		this.m3u8api = m3u8api;
	}
	
	public String getVt() {
		return vt;
	}
	public void setVt(String vt) {
		this.vt = vt;
	}
	
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	public int getAddTime() {
		return addTime;
	}
	public void setAddTime(int addTime) {
		this.addTime = addTime;
	}
	
	/**
	 * 用于查找当前播放影片在播放列表中的位置
	 */
	private String porder;
	
	public String getIfWatch() {
		return ifWatch;
	}
	public void setIfWatch(String ifWatch) {
		this.ifWatch = ifWatch;
	}
	
	public String getRequest_site() {
		return request_site;
	}
	public void setRequest_site(String request_site) {
		this.request_site = request_site;
	}
			
	public String getPorder() {
		return porder;
	}
	public void setPorder(String porder) {
		this.porder = porder;
	}
	public String getLetvMid() {
		return letvMid;
	}
	public void setLetvMid(String letvMid) {
		this.letvMid = letvMid;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getMedianame() {
		return medianame;
	}
	public void setMedianame(String medianame) {
		this.medianame = medianame;
	}
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
//	public String getUrl() {
//		return url;
//	}
//	public void setUrl(String url) {
//		this.url = url;
//	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getMediatype() {
		return mediatype;
	}
	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getCurrClarity() {
		return currClarity;
	}
	public void setCurrClarity(String currClarity) {
		this.currClarity = currClarity;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
//	public String getLanguage() {
//		return language;
//	}
//	public void setLanguage(String language) {
//		this.language = language;
//	}
	public String getDownloadType() {
		return downloadType;
	}
	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	public String getSaveName(){
//		return displayName + DownloadHelper.convertClarity(currClarity);
		return displayName + "流畅";
	}
	
//	@Override
//	public String toString() {
//		return "DownloadEntity [id=" + id + ", mid=" + mid + ", medianame="
//				+ medianame + ", taskname=" + taskname + ", status=" + status
//				+ ", fileSize=" + fileSize + ", url=" + url + ", downloadUrl="
//				+ downloadUrl + ", mediatype=" + mediatype + ", displayName="
//				+ displayName + ", index=" + index + ", currClarity="
//				+ currClarity + ", path=" + path + ", language=" + language
//				+ ", downloadType=" + downloadType + ", ext=" + ext + "]";
//	}
	@Override
	public String toString() {
		return "DownloadEntity [id=" + id + ", mid=" + mid + ", medianame="
				+ medianame + ", taskname=" + taskname + ", status=" + status
				+ ", fileSize=" + fileSize + ", url=" +  ", downloadUrl="
				+ downloadUrl + ", mediatype=" + mediatype + ", displayName="
				+ displayName + ", index=" + index + ", currClarity="
				+ ", path=" + path + ", language="
				+ ", downloadType=" + downloadType + ", ext=" + ext + "]";
	}


	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isUseUserAgent() {
		return useUserAgent;
	}

	public void setUseUserAgent(boolean useUserAgent) {
		this.useUserAgent = useUserAgent;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}
}
