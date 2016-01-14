package com.chaojishipin.sarrs.download.http.parser;


import com.chaojishipin.sarrs.download.bean.PlaySrcBean;
import com.chaojishipin.sarrs.download.bean.PlaySrcList;
import com.chaojishipin.sarrs.download.bean.VideoDataBean;

import org.json.JSONArray;
import org.json.JSONObject;

public class VideoDetailParser extends LetvMobileParser<VideoDataBean> {

	private static final String RATING = "rating";
	private static final String SUBCATEGORY = "subcategory";
	private static final String OTHERNAME = "othername";
	private static final String EPISODES = "episodes";
	private static final String TAG = "tag";
	private static final String VT = "vt";
	private static final String CATEGORY = "category";
	private static final String CATEGORYNAME = "categoryname";
	private static final String AREA = "area";
	private static final String ISEND = "isend";
	private static final String SUBSRC = "subsrc";
	private static final String AREANAME = "areaname";
	private static final String DESCRIPTION = "description";
	private static final String POSTER = "poster";
	private static final String SHORTDESC = "shortdesc";
	private static final String STARRING = "starring";
	private static final String NOWEPISODES = "nowepisodes";
	private static final String ENGLISHNAME = "englishname";
	private static final String SUBCATEGORYNAME = "subcategoryname";
	private static final String DIRECTORYNAME = "directoryname";
	private static final String SRC = "src";
	private static final String NAME = "name";
	private static final String URL = "url";
	private static final String STARRINGNAME = "starringname";
	private static final String RELEASEDATE = "releasedate";
	private static final String SUBNAME = "subname";
	private static final String DIRECTORY = "directory";
	private static final String AID = "aid";
	private static final String VIDEO_LIST = "videolist";
	private static final String LOGO = "sitelogo";
	private static final String PLS = "pls";
	private static final String MID = "mid";
	private static final String SITENAME = "sitename";

	private static final String EP_FILEPATH = "filepath";
	private static final String EP_NAME = "name";
	private static final String EP_VID = "vid";
	private static final String EP_URL = "url";
	private static final String EP_PORDER = "porder";
	private static final String EP_RELEASEDATE = "releasedate";
	private static final String EP_SUBNAME = "subname";
	private static final String EP_PLS = "pls";
	private static final String EP_MID = "mid";
	private static final String SITE_LIST = "siteList";
	
	private static final String SITE = "site";
	private static final String SITE_NOWEPISODES = "nowEpisodes";
	private static final String STELOGO = "sitelogo";
	
	private final String SUPERURL = "SuperUrl";
	private final String HIGHURL = "HighUrl";
	private final String STANDARDURL = "StandardUrl";
	private final String SMOOTHURL = "SmoothUrl";
	private final String SHARELINK = "shareurl";
	

	@Override
	public VideoDataBean parse(JSONObject data) throws Exception {
		// TODO Auto-generated method stub

		VideoDataBean video = new VideoDataBean();

		if (data.has(RATING)) {
			String rating = data.getString(RATING);
			if ("".equals(rating)) {
				video.setRating(0.0f);
			} else {
				video.setRating(Float.valueOf(rating));
			}
		}
		video.setShareLink(data.optString(SHARELINK));
		video.setSubcategory(data.optString(SUBCATEGORY));
		video.setOthername(data.optString(OTHERNAME));
		String episodes = data.optString(EPISODES);
		video.setEpisodes("".equals(episodes) ? "1" : episodes);
		video.setTag(data.optString(TAG));
		video.setVt(data.optString(VT));
		video.setCategory(data.optString(CATEGORY));
		video.setCategoryname(data.optString(CATEGORYNAME));
		video.setArea(data.optString(AREA));
		video.setIsend(data.optString(ISEND));
		video.setSubsrc(data.optString(SUBSRC));
		video.setAreaname(data.optString(AREANAME));
		video.setDescription(data.optString(DESCRIPTION));
		video.setPoster(data.optString(POSTER));
		video.setShortdesc(data.optString(SHORTDESC));
		video.setStarring(data.optString(STARRING));
		video.setNowepisodes(data.optString(NOWEPISODES));
		video.setEnglishname(data.optString(ENGLISHNAME));
		video.setSubcategoryname(data.optString(SUBCATEGORYNAME));
		video.setDirectoryname(data.optString(DIRECTORYNAME));
		video.setSrc(data.optString(SRC));
		video.setName(data.optString(NAME));
		video.setUrl(data.optString(URL));
		video.setStarringname(data.optString(STARRINGNAME));
		video.setReleasedate(data.optString(RELEASEDATE));
		video.setSubname(data.optString(SUBNAME));
		video.setDirectory(data.optString(DIRECTORY));
		video.setAid(data.optString(AID));
		video.setLogo(data.optString(LOGO));
		video.setMid(data.optString(MID));
		video.setPls(data.optString(PLS));
		video.setSitename(data.optString(SITENAME));
		
		if(data.has(SITE_LIST)){
			PlaySrcList srcList = new PlaySrcList();
			JSONArray arr = data.getJSONArray(SITE_LIST);
			for(int i =0; i<arr.length();i ++){
				JSONObject obj = arr.getJSONObject(i);
				PlaySrcBean playsrc=new PlaySrcBean();
					playsrc.setSite(obj.optString(SITE));
					playsrc.setEpisodeNum(obj.optString(EPISODES));
					playsrc.setNowEpisode(obj.optString(SITE_NOWEPISODES));
					playsrc.setLogo(obj.optString(STELOGO));
					playsrc.setAid(obj.getString(AID));
					playsrc.setSitename(obj.optString(SITENAME));
				srcList.getPlaySrcList().add(playsrc);
			}
			video.setSrcList(srcList);
		}
		
		
		
		
		
		
		
//		if (data.has(VIDEO_LIST)) {
//			JSONArray arr = data.getJSONArray(VIDEO_LIST);
//			for (int i = 0; i < arr.length(); i++) {
//				Episode ep = new Episode();
//				JSONObject obj = arr.getJSONObject(i);
//				ep.setSrc(data.optString(SRC));
//				ep.setName(obj.optString(EP_NAME));
//				ep.setVid(obj.optString(EP_VID));
//				ep.setPlay_url(obj.optString(EP_URL));
//				ep.setPorder(obj.optString(EP_PORDER));
//				ep.setReleaseDate(obj.optString(EP_RELEASEDATE));
//				ep.setPls(obj.optString(EP_PLS));
//				ep.setMid(obj.optString(EP_MID));
//				ep.setGlobaVid(obj.optString("globalVid"));
//				ep.setSerialid(data.optString(AID)+obj.optString(EP_PORDER));
//				ep.setRequest_site(PlayRecord.REQUEST_TYPE_FROM_DETAIL);
//				ep.setCloudId(obj.optString("cloudId"));
//				ep.setIsdownload(obj.optString("isdownload"));
////				ep.setDownload("http://121.18.237.194:80/download/4FFDFCD0C653A8DB7BD7D92A24C1540A627C9D5D.mp4");
//				if (obj.has(EP_SUBNAME)) {
//					try {
//						String subname = obj.getString(EP_SUBNAME);
//						ep.setSubName(subname);
//					} catch (JSONException e) {
//						ep.setSubName("");
//					}
//				}
//				
//				video.getEpisodeList().add(ep);
//			}
//			
//		}
		return video;
	}
	
//    private void setPlayMapVaule(HashMap<String, String> playUrlMap, JSONObject obj) {
//        if(null != obj ) {
//            String smoothUrl = obj.optString(SMOOTHURL);
//            String standardUrl = obj.optString(STANDARDURL);
//            String highUrl = obj.optString(HIGHURL);
//            String superUrl = obj.optString(SUPERURL);
//            if (!TextUtils.isEmpty(smoothUrl)) {
//                playUrlMap.put(SMOOTHURL, smoothUrl);
//            }
//            if (!TextUtils.isEmpty(standardUrl)) {
//                playUrlMap.put(STANDARDURL, standardUrl);
//            }
//            if (!TextUtils.isEmpty(highUrl)) {
//                playUrlMap.put(HIGHURL, highUrl);
//            }
//            if (!TextUtils.isEmpty(superUrl)) {
//                playUrlMap.put(SUPERURL, superUrl);
//            }
//        }
//    }

}
