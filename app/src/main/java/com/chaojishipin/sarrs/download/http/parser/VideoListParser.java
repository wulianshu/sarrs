package com.chaojishipin.sarrs.download.http.parser;



import com.chaojishipin.sarrs.bean.Episode;
import com.chaojishipin.sarrs.download.bean.PlaySrcBean;
import com.chaojishipin.sarrs.download.bean.PlaySrcList;
import com.chaojishipin.sarrs.download.bean.VideoDataBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class VideoListParser extends LetvMobileParser<VideoDataBean> {

	private static final String SITE = "site";
	private static final String EPISODES = "episodes";
	private static final String NOWEPISODES = "nowEpisodes";
	private static final String AID = "aid";
	private static final String STELOGO = "sitelogo";
	private static final String SITENAME = "sitename";
	private JSONArray arr ;
	
	private static final String VIDEO_LIST = "videoList";
	private static final String ORDERLIST = "orderlist";
	
	private String site;
	private VideoDataBean video;
	
	private static final String SRC = "src";
	private static final String EP_NAME = "name";
	private static final String EP_VID = "vid";
	private static final String EP_URL = "url";
	private static final String EP_PORDER = "porder";
	private static final String EP_RELEASEDATE = "releasedate";
	private static final String EP_SUBNAME = "subname";
	private static final String EP_PLS = "pls";
	private static final String EP_MID = "mid";
	private static final String VT = "vt";
	
	public VideoListParser(String site,VideoDataBean video) {
		super();
		this.site = site;
		this.video = video;
	}
	
	@Override
	public VideoDataBean parse(JSONObject data) throws Exception {
		if(null == video){
		   video = new VideoDataBean();
		}
//		VideoDataBean video = new VideoDataBean();
//		PlaySrcList srcList = new PlaySrcList();
		if(null==data || arr.length()<1)
			return video;
		PlaySrcList srcList = video.getSrcList();
		srcList.getPlaySrcList().clear();//清除现有的数据
		for(int i =0; i<arr.length();i ++){
			//解析playsrc
			JSONObject obj = arr.getJSONObject(i);
			PlaySrcBean playsrc=new PlaySrcBean();
				playsrc.setSite(obj.optString(SITE));
				playsrc.setEpisodeNum(obj.optString(EPISODES));
				playsrc.setNowEpisode(obj.optString(NOWEPISODES));
				playsrc.setLogo(obj.optString(STELOGO));
				playsrc.setAid(obj.getString(AID));
				playsrc.setSitename(obj.optString(SITENAME));
			srcList.getPlaySrcList().add(playsrc);
			//解析videolist
			if(null!=site && site.equals(obj.optString(SITE))){
				video.setSrc(obj.optString(SRC));
//				video.setVt(obj.optString(VT));
				if(obj.has(ORDERLIST)){
					String tempList = obj.optString(ORDERLIST);
					String[] porder = null;
					try {
						porder = tempList.split(";");
						if(null != porder && porder.length>0){
							ArrayList<String> porderLists = new ArrayList<String>(Arrays.asList(porder));
							video.setmPorderLists(porderLists); 
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (obj.has(VIDEO_LIST)) {
				JSONArray arr = obj.getJSONArray(VIDEO_LIST);
				for (int j = 0; j < arr.length(); j++) {
					Episode ep = new Episode();
					JSONObject jsonObject = arr.getJSONObject(j);
					ep.setSrc(obj.optString(SRC));
					ep.setName(jsonObject.optString(EP_NAME));
					ep.setSubName(jsonObject.optString(EP_NAME));
					ep.setVid(jsonObject.optString(EP_VID));
					ep.setPlay_url(jsonObject.optString(EP_URL));
					ep.setPorder(jsonObject.optString(EP_PORDER));
					ep.setReleaseDate(jsonObject.optString(EP_RELEASEDATE));
					ep.setPls(jsonObject.optString(EP_PLS));
					ep.setMid(jsonObject.optString(EP_MID));
					ep.setGlobaVid(jsonObject.optString("globalVid"));
					ep.setSerialid(jsonObject.optString("globalVid"));
//					ep.setSerialid(obj.optString(AID)+jsonObject.optString(EP_PORDER));
//					ep.setRequest_site(PlayRecord.REQUEST_TYPE_FROM_DETAIL);
//					ep.setCloudId(jsonObject.optString("cloudId"));
					ep.setIsdownload(jsonObject.optString("isdownload"));
					if (obj.has(EP_SUBNAME)) {
						try {
							String subname = jsonObject.getString(EP_SUBNAME);
							ep.setSubName(subname);
						} catch (JSONException e) {
							ep.setSubName("");
						}
					}
					video.getEpisodeList().add(ep);
					playsrc.getEpisodes().add(ep);
				}
				video.setPlaySrcBean(playsrc);
				
			  }
			}
		}
		video.setSrcList(srcList);
		return video;

	}

	@Override
	protected JSONObject getData(String data) throws JSONException {
		// TODO Auto-generated method stub
		arr =new JSONArray(data);
		data="{\"data\":\"data\"}";
		return super.getData(data);
	}

}
