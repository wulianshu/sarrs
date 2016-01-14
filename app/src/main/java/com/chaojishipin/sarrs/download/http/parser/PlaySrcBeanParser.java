package com.chaojishipin.sarrs.download.http.parser;


import com.chaojishipin.sarrs.bean.Episode;
import com.chaojishipin.sarrs.download.bean.PlaySrcBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaySrcBeanParser extends LetvMobileParser<PlaySrcBean> {

	private static final String SITE = "site";
	private static final String SITENAME = "sitename";
	private static final String VIDEO_LIST = "videoList";
	private static final String EP_AORDER = "aorder";
	private static final String EP_NAME = "name";
	private static final String EP_URL = "url";
	private static final String EPISODES = "episodes";
	private static final String INTRO = "intro";
	private static final String NOWEPISODES = "nowEpisodes";
	private static final String AID = "aid";
    private static final String GVID = "globalVid";
	private static final String LOGO = "sitelogo";
	private static final String EP_MID = "mid";
	private static final String EP_PLS = "pls";
	private static final String VT = "vt";
	private final String SUPERURL = "SuperUrl";
    private final String HIGHURL = "HighUrl";
    private final String STANDARDURL = "StandardUrl";
    private final String SMOOTHURL = "SmoothUrl";
	private String site;
	private JSONArray arr ;
	
	public PlaySrcBeanParser(String site){
		super();
		this.site=site;
	}
	@Override
    public PlaySrcBean parse(JSONObject data) throws Exception {
        PlaySrcBean playSrc = new PlaySrcBean();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.has(SITE)) {
                if (obj.getString(SITE).equals(site)) {
                    playSrc.setLogo(obj.optString(LOGO));
                    playSrc.setSite(obj.getString(SITE));
                    playSrc.setSitename(obj.optString(SITENAME));
                    if (obj.has(VIDEO_LIST)) {
                        JSONArray episodeList = obj.getJSONArray(VIDEO_LIST);
                        for (int j = 0; j < episodeList.length(); j++) {
                            JSONObject epJson = episodeList.getJSONObject(j);
                            Episode ep = new Episode();
                            ep.setSrc(obj.optString("src"));
                            ep.setPorder(epJson.optString(EP_AORDER));
                            ep.setName(epJson.optString(EP_NAME));
                            ep.setSubName(epJson.optString(EP_NAME));
                            ep.setPlay_url(epJson.optString(EP_URL));
                            ep.setMid(epJson.optString(EP_MID));
                            ep.setPls(epJson.optString(EP_PLS));
                            ep.setGlobaVid(epJson.optString("globalVid"));
//                            ep.setSerialid(obj.optString(AID) + epJson.optString(EP_AORDER));
                            ep.setSerialid(obj.optString(GVID));
//                            ep.setRequest_site(PlayRecord.REQUEST_TYPE_FROM_WEBSITE);
                            ep.setIsdownload(epJson.optString("isdownload"));

                            playSrc.getEpisodes().add(ep);
                        }
                    }
                    playSrc.setEpisodeNum(obj.optString(EPISODES));
                    playSrc.setNowEpisode(obj.optString(NOWEPISODES));
                    playSrc.setAid(obj.optString(AID));
                }
            } else {
                continue;
            }
        }
        return playSrc;
    }
	@Override
	protected JSONObject getData(String data) throws JSONException {
		arr =new JSONArray(data);
		data="{\"data\":\"data\"}";
		return super.getData(data);
	}
	
//    private void setPlayMapVaule(HashMap<String, String> playUrlMap, JSONObject obj) {
//        if (null != obj) {
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
//
//    }
}
