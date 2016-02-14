package com.chaojishipin.sarrs.download.http.api;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.CloudDiskBean;
import com.chaojishipin.sarrs.download.bean.SnifferReport;
import com.chaojishipin.sarrs.download.util.MoviesConstant;
import com.chaojishipin.sarrs.download.util.MoviesUtils;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayDecodeMananger;
import com.chaojishipin.sarrs.http.parser.CloudDiskParser;
import com.chaojishipin.sarrs.http.volley.SarrsRequest;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.MD5Utils;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.impl.LetvHttpBaseParameter;
import com.letv.http.impl.LetvHttpParameter;
import com.letv.http.impl.LetvHttpTool;
import com.letv.http.parse.LetvBaseParser;

import java.util.HashMap;
import java.util.Map;

public class MoviesHttpApi {

    private final static String TAG = "MoviesHttpApi";

	public static final boolean TEST =   false;
 
	/**
	 * 公共参数
	 * */
//	public static final String PLATFORM;
	
	
	/**
	 * 升级接口所用的SYSTEM 参数
	 */
	public static final String PLATFORM_IN_UPGRADE = "Le123Plat002";

	public static final String CODE = "346e5b9d1bd97036";
	
	public static final String PLATTYPE_VAULE = "aphone";
	
//	public static final String CHANNEL;

	/**
	 * 当前版本
	 * */
	public static final String VERSION;

	static{
		VERSION = MoviesUtils.getClientVersionName();
//		CHANNEL = MoviesUtils.getClientChannel();
//		PLATFORM = "Le123Plat002"+CHANNEL;
//		PLATFORM = "Le123Plat002";
	}

    public static interface PUBLIC_PARAMETERS {
        public String PLATFORM_KEY = "platform";
        public String CODE_KEY = "code";
        public String VERSION_KEY = "version";
        public String PLATTYPE_KEY = "plattype";
        public String DEFINITION = "definition";
        public String LOCATION = "city";
        public String IP = "ip";
    }

	private static final String GET_TOPIC_DATA_URL = "kuaikan/apithemelist_json.so";

	private static interface TOPIC_DATA_PARAMETERS {
		public String PAGE_KEY = "pageindex";
		public String PAGE_SIZE_KEY = "pagesize";
	}

	private static final String GET_CHANNEL_DETAIL_LIST_URL = "kuaikan/apilistnew_json.so";

	private static interface CHANNEL_DETAIL_LIST_PARAMETERS {
		public String ORDERBY_KEY = "orderby";
		public String PAGEINDEX_KEY = "pageindex";
		public String SUBCATEGORY_KEY = "subcategory";
		public String PAGESIZE_KEY = "pagesize";
		public String AREA_KEY = "area";
		public String VT_KEY = "vt";
		public String YEAR_KEY = "year";
	}


	private static final String GET_CHANNEL_LIST_URL = "kuaikan/apichannel_json.so";

	private static interface CHANNEL_LIST_PARAMETERS {

	}



	// request VideoData api
//	private static final String GET_VIDEO_DATA_URL = "kuaikan/apidetail_json.so";
	private static final String GET_VIDEO_DATA_URL = "kuaikan/apisimpledetail_json.so";
	

	private static interface VIDEO_DATA_PARAMETERS {
		public String AID_KEY = "aid";
		public String SRC_KEY = "src";
	}


	/**
	 * 二级分类
	 **/
	private static final String GET_SIFT_LIST_URL = "kuaikan/apisubcategory_json.so";

	private static interface SIFT_LIST_PARAMETERS {
		public String VT_KEY = "vt";
	}
	static void addVer(Bundle params) {
		params.putString("pl", "1000011");
		params.putString("appv", Utils.getClientVersionName());
		params.putString("appfrom", "0");
		params.putString("pl1", "0");
		params.putString("pl2", "00");
		params.putString("appid", "0");
		params.putString("clientos", Utils.getSystemVer());
		// 分辨率
		params.putString("resolution", SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_HEIGHT, "") + "*" + SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH, ""));
		params.putString("width", SPUtil.getInstance().getString(ConstantUtils.ScreenConstants.SCREEN_WIDTH, ""));
		// 设备ID TODO 两个字段一样需统一
		params.putString("lc", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));
		// 设备ID
		params.putString("auid", Utils.getDeviceId(ChaoJiShiPinApplication.getInstatnce()));

	}

	/**
	 * 首页推荐模块数据
	 */
	private static final String GET_MAINPAGE_RECOMMEND_DATA_URL = "kuaikan/apipage_json.so";

	private static interface MAINPAGE_RECOMMEND_DATA_PARAMETERS {
		public String PAGE_KEY = "page";
	}

	private static interface MAINPAGE_RECOMMEND_DATA_VALUES {
		public String PAGE_VALUE = "page_index";
	}



	private static interface RECOMM_DATA_PARAMETERS {
		public String RECOMMEND_KEY = "recommend";
	}

	/**
	 * 嗅探上报接口
	 */
	private static final String GET_SNIFFER_URL = "kuaikan/apixiutan_json.so";

	/**
	 * 嗅探参数的key
	 * 
	 * @author zhangshuo
	 */
	private static interface SNIFFER_KEY {
		public String AID = "aid";
		public String STATE = "state";
		public String PLAYURL = "playurl";
		public String FILEPATH = "filepath";
		public String SITE = "site";
		public String WAITING = "waiting";
		public String DEFINITION = "definition";
		public String DOWNLOAD = "download";
		public String DURATION = "duration";
	}

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestSnifferReport(
            SnifferReport snifferReport) {

        String url = getStaticHead() + GET_SNIFFER_URL;
        Bundle params = new Bundle();
        params.putString("uuid", MoviesConstant.UUID);
        params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
        params.putString(PUBLIC_PARAMETERS.PLATTYPE_KEY, PLATTYPE_VAULE);
//        params.putString(PUBLIC_PARAMETERS.PLATFORM_KEY, PLATFORM);
        params.putString(PUBLIC_PARAMETERS.CODE_KEY, CODE);
        params.putString(SNIFFER_KEY.AID, snifferReport.getAid());
        params.putString(SNIFFER_KEY.STATE, snifferReport.getState());
        params.putString(SNIFFER_KEY.PLAYURL, snifferReport.getPlayUrl());
        params.putString(SNIFFER_KEY.FILEPATH, snifferReport.getFilepath());
        params.putString(SNIFFER_KEY.SITE, snifferReport.getSite());
        params.putString(SNIFFER_KEY.WAITING, snifferReport.getWaiting());
        params.putString(SNIFFER_KEY.DEFINITION, snifferReport.getDefinition());
        params.putString(SNIFFER_KEY.DOWNLOAD, snifferReport.getDownload());
        params.putString(SNIFFER_KEY.DURATION, snifferReport.getDuration());
//        params.putString(PUBLIC_PARAMETERS.LOCATION, MoviesApplication.LOCATION_INFO);
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, null, -1);
//        LogUtils.e(TAG, "!!!!!!!!!上报----"+params.toString());
        return request(httpParameter);
    }

	/**
	 * 排行榜首页
	 **/
	private static final String GET_HOME_RANK_DATA_URL = "kuaikan/apirank_json.so";

	private static interface RANK_HOME_DATA_PARAMETERS {
		public String RECOMMEND_KEY = "rank";
		public String RECORD_KEY = "record";
	}

	private static interface RANK_HOME_DATA_VALUES {
		public String MOVIE = "m_rank_movie";
		public String TV = "m_rank_tv";
		public String ZONGYI = "m_rank_zongyi";
		public String CARTOON = "m_rank_cartoon";
		public String[] RECOMMEND_VALUE = { MOVIE, TV, CARTOON, ZONGYI };
	}


	// request PlaySrc api
	private static final String GET_PLAY_SRC_URL = "kuaikan/apiwebsite_json.so";
	
	//requset albumupdateinfolisturl
	private static final String GET_ALBUM_UPDATE_INFO_URL = "kuaikan/apialbumupdateinfo_json.so";
	


	private static interface PLAY_SRC_PARAMETERS {
		public String SRC_KEY = "src";
		public String SITE_KEY = "site";
		public String AID_KEY = "aid";
	}

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestPlaySrc(
			LetvBaseParser<T, D> parser, String src, String site, String aid) {
		String url = getStaticHead() + GET_PLAY_SRC_URL;
//        if(MoviesConstant.DEBUG){
//        	url = "http://10.154.28.65:7070/"+ GET_PLAY_SRC_URL;
//        }
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.PLATTYPE_KEY, PLATTYPE_VAULE);
//		params.putString(PUBLIC_PARAMETERS.PLATFORM_KEY, PLATFORM);
		params.putString(PUBLIC_PARAMETERS.CODE_KEY, CODE);
		params.putString(PLAY_SRC_PARAMETERS.SRC_KEY, src);
		params.putString(PLAY_SRC_PARAMETERS.SITE_KEY, site);
		params.putString(PLAY_SRC_PARAMETERS.AID_KEY, aid);
		params.putString("uuid", MoviesConstant.UUID);
		params.putString(PUBLIC_PARAMETERS.DEFINITION, "1");
//		params.putString(PUBLIC_PARAMETERS.LOCATION, MoviesApplication.LOCATION_INFO);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				url, params, LetvHttpParameter.Type.GET, parser, -1);
		return request(httpParameter);
	}
	
	private static interface RELATION_PARAMETERS {
		public String SRC_KEY = "src";
		public String CID_KEY = "cid";
		public String AID_KEY = "aid";
		public String NUM_KEY = "num";
	}

	
	public static String getStaticHead() {
		// TODO Auto-generated method stub
		if (TEST) {
			return MoviesConstant.TEST_HOST;
		} else {
			return MoviesConstant.PRODUCT_HOST;
		}
	}


	private static <T extends LetvBaseBean, D> LetvDataHull<T> request(
			LetvHttpBaseParameter<T, D, ?> httpParameter) {
		LetvHttpTool<T> handler = new LetvHttpTool<T>();
		return handler.requsetData(httpParameter);
	}

	/**
	 * 乐视网码流参数
	 * 
	 * @author zhangshuo
	 * 
	 */
	public static final String GET_STREAM_URL = "sarrs/geturl";

    public static interface LeTvBitStreamParam {
        public String KEY_MMSID = "mid";
        public String KEY_PLS = "pls";
        public String KEY_TSS = "tss";
        public String KEY_CDETYPE = "cdetype";
        public String KEY_CDETYPE_TEST = "1";
        public String KEY_CDETYPE_AES = "2";
        public String KEY_LSSV = "lssv";
        public String KEY_REQUESTTYPE = "requesttype";
        public String KEY_DOWNLOAD = "download";
        public String KEY_PLAY = "play";
        public String KEY_CLOUD_CODE = "cloudcode";
        public String KEY_TYPE = "type";  //0:mp4, 1:m3u8
        public String KEY_SRC = "src";
        public String KEY_VID = "vid";
    }

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestLetvStream(
            LetvBaseParser<T, D> parser, Bundle params) {
            		
        return request(getLetvStreamParameter(parser,params));
    }
	/**
     * 乐视网请求下载和播放共用，拼串方法
     */
    public static <T extends LetvBaseBean, D> LetvHttpParameter<T,D> getLetvStreamParameter(
            LetvBaseParser<T, D> parser, Bundle params) {
        try {
            String url = getStaticHead()+GET_STREAM_URL;
//            String url = "http://10.154.252.65:7070/" + GET_STREAM_URL;
            String gvid = params.getString(MoviesHttpApi.LeTvBitStreamParam.KEY_VID);
            params.putString(ConstantUtils.LeTvBitStreamParam.KEY_GVID, gvid);
            params.putString("platid", "17");
            params.putString("splatid", "1702");// 网盘源下载客户端不需要传该字段，代理层会返回splatid且值为1409
//			params.putString("vtype", vType); //乐视网存在
			params.putString("playid", "2");
			params.putString("unique_id", MoviesConstant.UUID);

			StringBuilder key_sb = new StringBuilder();
			key_sb.append(gvid);
			key_sb.append(ConstantUtils.LeTvBitStreamParam.KEY_GETPLAYURL_KEY);
			key_sb.append("17");
			String key = MD5Utils.md5(key_sb.toString());
			params.putString(ConstantUtils.LeTvBitStreamParam.KEY_KEY, key);

            String tssType;
//            if (LeTvBitStreamParam.KEY_DOWNLOAD.equals(params
//                    .getString(LeTvBitStreamParam.KEY_REQUESTTYPE))) {
//                tssType = "no";
//            } else {
//                // 如果当前不使用系统硬解，则采用m3u8播放
//                tssType = PlayDecodeMananger.ismNeedSysDecoder() ? "no" : "ios";
//            }
			// 如果当前不使用系统硬解，则采用m3u8播放
			tssType = "no";
            params.putString(LeTvBitStreamParam.KEY_TSS, tssType);
//            params.putString(PUBLIC_PARAMETERS.LOCATION, MoviesApplication.LOCATION_INFO);
            LetvHttpParameter<T, D> httpParameter =
                    new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, parser, -1);
            return httpParameter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	/**
	 * 请求指定站点的URL
	 * @param parser
	 * @param url
	 * @return
	 * zhangshuo
	 * 2014年6月13日 下午3:09:25
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestUrlAnalysis(
			LetvBaseParser<T, D> parser, String url) {
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				url, null, LetvHttpParameter.Type.GET, parser, -1);
		return request(httpParameter);
}

	
	public static String getPushHead() {
		if (TEST) {
			return MoviesConstant.TEST_PUSH_HOST;
		} else {
			return MoviesConstant.PRODUCT_PUSH_HOST;
		}
	}

	
	 /**
     * @param parser
     * @param url
     * @return
     * zhangshuo
     * 2014年9月22日 上午11:23:38
     */
    public static <T extends LetvBaseBean, D> LetvDataHull<T> requestHtmlData(
            LetvBaseParser<T, D> parser, String url, String refererUrl,String userAgent) {
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, null, LetvHttpParameter.Type.GET, parser, -1);
        httpParameter.setmReferer(refererUrl);
        if(!TextUtils.isEmpty(userAgent)){
            httpParameter.setmUserAgent(userAgent);
        }
        return request(httpParameter);
    }
	/**
	 *    流地址播放失败后 更新流地址接口
	 *   @param playUrl  流地址
	 *   @param format  清晰度，0-normal，1-high，2-super，3-super2，4-real
	 *   @param pl     平台（区分产品及客户端类型）
	 *   @param eid 新版外网流地址接口会返回该字段，上报时回传，用于统计播放失败率
	（iOS和Android流地址的两个eid中选择一个上报即可）
	 * */

	public static <T extends LetvBaseBean, D> LetvDataHull<T> updateStreamlistUrl(
			LetvBaseParser<T, D> parser,String reqUrl, String playUrl, String format,String pl,String eid) {

		Bundle params = new Bundle();
		params.putString("playurl", playUrl);
		params.putString("format", format);
		params.putString("eid", eid);
		LetvHttpParameter<T, D> httpParameter =
				new LetvHttpParameter<T,D>(reqUrl, params, LetvHttpParameter.Type.GET, null, -1);
		return request(httpParameter);
	}

	/**
	 * @param parser
	 * @param url
	 * @return
	 * xll
	 * 2015年9月20日
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestRedirect(
			LetvBaseParser<T, D> parser, String url,String userAgent) {
		LetvHttpParameter<T, D> httpParameter =
				new LetvHttpParameter<T, D>(url, null, LetvHttpParameter.Type.GET, parser, -1);
		if(!TextUtils.isEmpty(userAgent)){
			httpParameter.setmUserAgent(userAgent);
		}
		return request(httpParameter);
	}

	public static final String URL_POST_SPREAD_DEVIDE_INFO = "SO-F/today/installersave";



    /**
     * 判断当前UTP是否存活的server
     */
    public static final String UTPSERVERURL = "http://127.0.0.1:6990/state/ok";
    public static <T extends LetvBaseBean, D> LetvDataHull<T> requestUtpLiveServer(
            LetvBaseParser<T, D> parser) {
        String url =UTPSERVERURL;
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, null, LetvHttpParameter.Type.GET, parser, -1);
        return request(httpParameter);
    }
    
    //请求单机信息的URL
    public static final String SINGLE_INFO_URL ="kuaikan/apisingledetail_json.so";
    /**
     * 下载和播放共用，拼串方法
     */
    public static <T extends LetvBaseBean, D> LetvHttpParameter<T,D> getLetvHttpParameter(
    		LetvBaseParser<T, D> parser, String globalVid, String site, String src){
    	String url = getStaticHead() + SINGLE_INFO_URL;
        Bundle params = new Bundle();
        params.putString("soKey", globalVid);
        params.putString("subsrc", site);
        params.putString("src", src);
//        params.putString(PUBLIC_PARAMETERS.LOCATION, MoviesApplication.LOCATION_INFO);
        LetvHttpParameter<T, D> httpParameter =
                new LetvHttpParameter<T, D>(url, params, LetvHttpParameter.Type.GET, parser, -1);
    	return httpParameter;
    }
    
    public static <T extends LetvBaseBean, D> LetvDataHull<T> requestSingleInfo(
            LetvBaseParser<T, D> parser, String globalVid, String site, String src) {
        
        return request(getLetvHttpParameter(parser,globalVid,site,src));
    }

	//js截流更新
	private static final String GET_JSUPDATE_URL = "kuaikan/apiextractjs_json.so";

	public static String VER = "ver";

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestJsUpdate(
			LetvBaseParser<T, D> parser,String ver) {
		String url = getStaticHead() + GET_JSUPDATE_URL;
//		String url = "http://115.182.93.104:7070/" + GET_JSUPDATE_URL;
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.CODE_KEY, CODE);
		params.putString(VER,ver);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
//		params.putString(PUBLIC_PARAMETERS.PLATFORM_KEY, PLATFORM);
		params.putString(PUBLIC_PARAMETERS.PLATTYPE_KEY, PLATTYPE_VAULE);
		params.putString("uuid", MoviesConstant.UUID);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				url, params, LetvHttpParameter.Type.GET, parser, -1);
		return request(httpParameter);
	}

	public static void addVer(Map<String, String> params) {
		params.put("pl", "1000011");
		params.put("appv", Utils.getClientVersionName());
		params.put("appfrom", "0");
	}
}
