package com.chaojishipin.sarrs.download.http.parser;


import com.chaojishipin.sarrs.download.bean.PlaySrcBean;
import com.chaojishipin.sarrs.download.bean.PlaySrcList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaySrcListParser extends LetvMobileParser<PlaySrcList> {

	private static final String SITE = "site";
	private static final String EPISODES = "episodes";
	private static final String INTRO = "intro";
	private static final String NOWEPISODES = "nowEpisodes";
	private static final String AID = "aid";
	private static final String STELOGO = "sitelogo";
	private static final String SITENAME = "sitename";
	private JSONArray arr ;
	@Override
	public PlaySrcList parse(JSONObject data) throws Exception {
		// TODO Auto-generated method stub
		PlaySrcList srcList = new PlaySrcList();
		for(int i =0; i<arr.length();i ++){
			JSONObject obj = arr.getJSONObject(i);
			PlaySrcBean playsrc=new PlaySrcBean();
				playsrc.setSite(obj.optString(SITE));
				playsrc.setEpisodeNum(obj.optString(EPISODES));
				playsrc.setNowEpisode(obj.optString(NOWEPISODES));
				playsrc.setLogo(obj.optString(STELOGO));
				playsrc.setAid(obj.getString(AID));
				playsrc.setSitename(obj.optString(SITENAME));
			srcList.getPlaySrcList().add(playsrc);
		}
		return srcList;

	}

	@Override
	protected JSONObject getData(String data) throws JSONException {
		// TODO Auto-generated method stub
		arr =new JSONArray(data);
		data="{\"data\":\"data\"}";
		return super.getData(data);
	}

}
