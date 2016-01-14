package com.chaojishipin.sarrs.download.util;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.thirdparty.Utils;


public class MoviesConstant {
	public static final String HOTEST="3";
	public static final String LASTEST="1";
	public static final String GOODPING="8";
//	public static final boolean DEBUG = KuaikanConfiguration.isDebug();
//	public static final String PKNAME = KuaikanConfiguration.getPkName();
//	public static final int VERSIONCODE = KuaikanConfiguration.getVersionCode();
//	public static final String SUBCHANNEL = Utils.getSubchannel(MoviesApplication.getInstance());
//
	public static final String UUID = new UUIDFactory(ChaoJiShiPinApplication.getInstatnce()).getDeviceUuid().toString();
	public static final String EPISODE_UPDATE = "0";
	public static final String EPISODE_UPDATE_END = "1";
//	public static final String TEST_HOST = "http://115.182.93.104:7070/";
	public static final String TEST_HOST = "http://play.chaojishipin.com/";
	public static final String PRODUCT_HOST = "http://play.chaojishipin.com/";
	public static final String TEST_PUSH_HOST = "http://proxy.le123.com/";
	public static final String PRODUCT_PUSH_HOST = "http://proxy.le123.com/";
	public static final int TOPIC_REQUEST_SIZE = 10;	
	public static final int CHANNEL_DETAIL_LIST_REQUEST_SIZE = 18;
	public static final String VT_MOVIE = "2";
	public static final String VT_TV = "1";
	public static final String VT_CARTOON = "3";
	public static final String VT_ZONGYI = "4";
	public static final String VT_LIVE = "100";
	public static final String NAME_VT_MOVIE = "电影";
	public static final String NAME_VT_TV = "电视剧";
	public static final String NAME_VT_CARTOON = "动漫";
	public static final String NAME_VT_ZONGYI = "综艺";
	public static final String NAME_VT_LIVE = "直播";
	public static final int SEARCH_RESULT_REQUEST_SIZE = 10;
	public static final int TOPIC_DETAIL_REQUEST_SIZE = 18;
	public static final int PLAY_RECORD_MAXIMUM_SIZE = 100;
	public static final int FAVORITE_MAXIMUM_SIZE = 100;
	public static final int SEARCH_RECORD_MAXIMUM_SIZE = 5;
	public static final long RECOMMEND_FOCUS_TIMESPAN = 3500;
	public static final String SPREAD_SECRET = "video7~8!5g@9e#6today$7%0^3*le_123app";
	
	public static final String PACKAGE_NAME = ChaoJiShiPinApplication.getInstatnce().getPackageName();
	public static final String YINGSHIDAQUAN = "com.le123.ysdq";
	public static final String KUANKAN ="com.elinkway.infinitemovies";
	public static final String SIGNATURE = Utils.getSignInfo();
	public static final int DETAIL_RELATION_NUM = 6;
	public static final String BRAND = Utils.getBrandName();
	public static final String SERIAL_NUMBER = Utils.getSerialNumber();
	public static final int LIVE_CHANNEL_PAGE_SIZE = 10;
	public static String getVtName(String vt){
		if("1".equals(vt)){
			return NAME_VT_TV;
		}
		else if("2".equals(vt)){
			return NAME_VT_MOVIE;
		}
		else if("3".equals(vt)){
			return NAME_VT_CARTOON;
		}
		else if("4".equals(vt)){
			return NAME_VT_ZONGYI;
		}else if(VT_LIVE.equals(vt)){
			return NAME_VT_LIVE;
		}
		return "";
		
	}
}
