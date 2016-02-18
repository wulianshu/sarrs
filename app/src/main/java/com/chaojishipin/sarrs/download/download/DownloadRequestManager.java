package com.chaojishipin.sarrs.download.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.CloudDiskBean;
import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.bean.SingleInfo;
import com.chaojishipin.sarrs.bean.VStreamInfoList;
import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.download.http.parser.VStreamInfoListParser;
import com.chaojishipin.sarrs.download.util.UpdateSnifferManager;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayDecodeMananger;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.http.parser.CloudDiskParser;
import com.chaojishipin.sarrs.http.parser.HtmlParser;
import com.chaojishipin.sarrs.http.parser.SingleInfoParser;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.http.LetvHttpConstant;
import com.letv.http.impl.LetvHttpParameter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DownloadRequestManager {

    private WebView mWebView;

    private TestJavaScriptInterface mTestInterface;
    private String mSnifferParamter = "";
    private Handler mHandler;
    private Runnable mRunnable;
    private int loopMaxNum = 20;
    private int num = 0;
    private String mCurrSinffType;
    private String SNIFF_SUCCESS = "success";
    private String mDefaultDefinition = "";
    private String downLoadUrl;
    private String downLoadType;
    private final String type = "0"; //0:mp4, 1:m3u8


    public DownloadRequestManager() {
        super();
    }

    public DownloadRequestManager(Context context) {
        super();
        mTestInterface = new TestJavaScriptInterface();
        final Context cotexttemp = context;
        AllActivityManager.getInstance().getCurrentActivity().runOnUiThread(new Runnable() // 工作线程刷新UI
        { //这部分代码将在UI线程执行，实际上是runOnUiThread post Runnable到UI线程执行了
            @Override
            public void run() {
                initWebView(cotexttemp);
            }
        });
        num = 0;
    }

	public VStreamInfoList getDownloadData(DownloadEntity downloadEntity) {
		String url;
		try {
			url = initUrl(downloadEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		String json = getHttpRequest(url);

		VStreamInfoList data = getJsonObject(json);
		int retryNum= 0;
		while(data == null&&retryNum < 2 ){
			retryNum++;
			json = getHttpRequest(url);
			data = getJsonObject(json);
		}
		return data;
	}
    public String getSnifDownloadData(DownloadEntity downloadEntity) {
        String url;
        try {
            url = initUrl(downloadEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String json = getHttpRequest(url);
        String result = decodeString(json);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }
        int retryNum = 0;
        while (json == null && retryNum < 2) {
            retryNum++;
            json = getHttpRequest(url);
            result = decodeString(json);
        }

        return result;
    }
	private String initUrl(DownloadEntity downloadEntity) throws UnsupportedEncodingException {
		
		Bundle bundle = new Bundle();
		bundle.putString(MoviesHttpApi.LeTvBitStreamParam.KEY_VID, downloadEntity.getGlobaVid());
		if (downloadEntity.getSrc() != null && downloadEntity.getSrc().equals("letv"))
		{
			String vType = PlayerUtils.PLS_MP4_720p_db + "," + PlayerUtils.PLS_MP4_350 + "," + PlayerUtils.PLS_MP4;
			bundle.putString("vtype", vType); //乐视网存在
		}
		HashMap<String, String> map = new HashMap<>();
		MoviesHttpApi.addVer(map);
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String)entry.getKey();
			String val = (String)entry.getValue();
			bundle.putString(key, val);
		}
		LetvHttpParameter<?, ?> httpParameter = MoviesHttpApi.getLetvStreamParameter(new VStreamInfoListParser(), bundle);
		String originalStr = httpParameter.getBaseUrl() + httpParameter.encodeUrl();
		
//		LogUtils.e("dyf", "!!!!!!!加密后的字符串!!!!!!" + originalStr);
		return originalStr;
	}
	
	
	private VStreamInfoList getJsonObject(String json) {
		if (!TextUtils.isEmpty(json)) {
			try {
				VStreamInfoListParser infoListParser = new VStreamInfoListParser();
				JSONObject mContent = infoListParser.getData(json);
				VStreamInfoList data = new VStreamInfoListParser().parse(mContent);
				
				return data;

			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	
	public String getHttpRequest(String url) {
		String json = null;
		InputStream is = null;
		
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			conn.setInstanceFollowRedirects(true);
			is = conn.getInputStream();
			json = readData(is, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		
		return json;
	}
	
	private String readData(InputStream inSream, String charsetName) throws Exception {
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inSream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		
		final byte[] data = outStream.toByteArray();
		outStream.close();
		inSream.close();
		
		return new String(data, charsetName);
	}
	/**
	 * 截流逻辑,返回downLoadUrl
	 * @param api
	 * @param referer
	 * @param sniffType
	 */
	public String requestSniffer(String api, String referer, String sniffType,String rule){
		mCurrSinffType = sniffType;
		HtmlDataBean data = requestSnifferData(api, referer, sniffType);
		try {
            String apiContent = data.getHtmlData();
            if (!TextUtils.isEmpty(apiContent) && null != mWebView && !TextUtils.isEmpty(referer)) {
                JSONObject obj = new JSONObject();
                obj.put("requestUrl", referer);
                obj.put("apiContent", apiContent);
                obj.put("type", sniffType);
                obj.put("jsonParam", rule);
                mSnifferParamter = obj.toString();
                LogUtil.e("dyf", "@@@@@@@"+mSnifferParamter);
				AllActivityManager.getInstance().getCurrentActivity().runOnUiThread(new Runnable() // 工作线程刷新UI
                { //这部分代码将在UI线程执行，实际上是runOnUiThread post Runnable到UI线程执行了  
                    @Override
                    public void run() {
                        mHandler = new Handler();
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (num < 20) {
                                    num++;
                                    mWebView.clearCache(true);
                                    mWebView.loadUrl("javascript:androidrequest()");
                                    LogUtil.i("dyf", "执行handleMessage----" + num);
                                    mHandler.postDelayed(mRunnable, 300);
                                } else {
                                    mHandler.removeCallbacks(mRunnable);
                                }

                            }
                        };
                        mHandler.postDelayed(mRunnable, 300);
                    }
                });
                while (num < loopMaxNum) {
                    try {
                        LogUtil.i("dyf", "执行sleep----" + num);
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mHandler.removeCallbacks(mRunnable);
                LogUtil.i("dyf", "返回url---" + downLoadUrl);
                return downLoadUrl;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return downLoadUrl;
	}
	
	public HtmlDataBean requestSnifferData(String api, String referer, String sniffType) {
		String json = getSnifferHttpRequest(api,referer);
		HtmlDataBean data = getSnifferJsonObject(json);
		int retryNum= 0;
		while(data == null&&retryNum < 2 ){
			retryNum++;
			json = getSnifferHttpRequest(api,referer);
			data = getSnifferJsonObject(json);
		}
		if(data == null){ //tuner容灾

		}
		return data;
	}
	
	public String getSnifferHttpRequest(String url,String referer) {
		String json = null;
		InputStream is = null;
		
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			if (!TextUtils.isEmpty(referer)) {
				conn.setRequestProperty(LetvHttpConstant.REFERER, referer);
	        }
			conn.setRequestProperty("User-agent", PlayerUtils.MOBILEAGENT);
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			conn.setInstanceFollowRedirects(true);
			is = conn.getInputStream();
			json = readData(is, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		
		return json;
	}
	
	private HtmlDataBean getSnifferJsonObject(String json) {
		if (!TextUtils.isEmpty(json)) {
			try {
				HtmlDataBean data = new HtmlParser().initialParse(json);
				return data;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
//	private void initHandler(){
//		/**
//	     * 处理消息的handler
//	     */
//	    mHandler = new Handler() {
//
//	        @Override
//	        public void handleMessage(Message msg) {
//	            super.handleMessage(msg);
//	            switch (msg.what) {
//	                case Utils.GET_JS_RESULT:
//	                    if (mSniffRetryCount < 20) {
//	                        ++mSniffRetryCount;
//	                        MoviesApplication.getInstance().getActivity().runOnUiThread(new Runnable() // 工作线程刷新UI  
//	                        { //这部分代码将在UI线程执行，实际上是runOnUiThread post Runnable到UI线程执行了  
//	                            @Override  
//	                            public void run()  
//	                            {  
//	                            	mWebView.clearCache(true);
//	     	                        mWebView.loadUrl("javascript:androidrequest()");
//	     	                       LogUtil.i("dyf", "执行handleMessage----"+mSniffRetryCount);
//	                            }  
//	                        }); 
//	                        mHandler.sendEmptyMessageDelayed(Utils.GET_JS_RESULT, Utils.EXECUTE_JS_TIME);
//	                    } else {
//	                        resetSniffToIdle();
//	                    }
//	                    break;
//	                default:
//	                    break;
//	            }
//	        }
//	    };
//	 // 发送消息调用JS代码
//        mHandler.sendEmptyMessage(Utils.GET_JS_RESULT);
//        LogUtil.i("dyf", "执行mHandler");
//        Looper.loop();
//	}
	
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(Context activity) {
        mWebView = new WebView(activity);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearCache(true);
        String htmlUrl = UpdateSnifferManager.getInstance(activity).getHtmlURL();
//        htmlUrl ="file:///android_asset/extractor.html";
        mWebView.loadUrl(htmlUrl);
        mWebView.addJavascriptInterface(mTestInterface, "dataresult");
    }
    
    public class TestJavaScriptInterface {
        @JavascriptInterface
        public void startFunction(String result) {
//            LogUtils.e("dyf", "!!!!!startFunction!!!!!" + result);
            if (!TextUtils.isEmpty(result)) {
            	downLoadUrl = parseSniffResult(result);
            } else {
//                sniffCallBackError();
            	downLoadUrl = "";
            }
            num = 20;
            mHandler.removeCallbacks(mRunnable);
           
        }

        @JavascriptInterface
        public String getmSnifferParamter() {
            return mSnifferParamter;
        }
    }
    
    private String parseSniffResult(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject datatObj = obj.optJSONObject("data");
            String state = obj.optString("state");
            JSONObject playObj = datatObj.optJSONObject(mCurrSinffType);
            if (SNIFF_SUCCESS.equalsIgnoreCase(state) && null != playObj
                    && !TextUtils.isEmpty(playObj.toString())) {
                HashMap<String, String> playMap = new HashMap<String, String>();
                PlayerUtils.setPlayMapVaule(playMap, playObj);
                getDefaultDefinition();
                String playUrl = getDefaultPlayUrl(playMap);
//                Type currPlayerType =DownloadInfo.M3U8.equals(mCurrSinffType) ? Type.MOBILE_H264_M3U8 : Type.MOBILE_H264_MP4;
               return playUrl;
            } else {
//                sniffCallBackError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取当前默认播放的清晰度
     */
    private void getDefaultDefinition(){
        mDefaultDefinition = PlayerUtils.getPlayDefinition();
//        LogUtils.e("dyf", "!!!!当前默认的清晰度为!!!!!" + mDefaultDefinition);
    }
    
    public String getDefaultPlayUrl(HashMap<String, String> playMap) {
        String playUrl = "";
        if (null != playMap && playMap.size() > 0) {
            playUrl = playMap.get(mDefaultDefinition);
            if (TextUtils.isEmpty(playUrl)) {
                if (PlayerUtils.SMOOTHURL.equals(mDefaultDefinition)) {
                    mDefaultDefinition = PlayerUtils.STANDARDURL;
                    playUrl = playMap.get(mDefaultDefinition);
                } else if (PlayerUtils.STANDARDURL.equals(mDefaultDefinition)) {
                    mDefaultDefinition = PlayerUtils.SMOOTHURL;
                    playUrl = playMap.get(mDefaultDefinition);
                } else if (PlayerUtils.HIGHURL.equals(mDefaultDefinition)) {
                    mDefaultDefinition = PlayerUtils.STANDARDURL;
                    playUrl = playMap.get(mDefaultDefinition);
                    if (TextUtils.isEmpty(playUrl)) {
                        getDefaultPlayUrl(playMap);
                    }
                } else if (PlayerUtils.SUPERURL.equals(mDefaultDefinition)) {
                    mDefaultDefinition = PlayerUtils.HIGHURL;
                    playUrl = playMap.get(mDefaultDefinition);
                    if (TextUtils.isEmpty(playUrl)) {
                        getDefaultPlayUrl(playMap);
                    }
                }
            }
        } else {
            playUrl = "";
        }
        return playUrl;
    }
    
    public String getmDefaultDefinition() {
		return mDefaultDefinition;
	}
    /**
     * 云盘请求下载url
     */
    public String getCloudDiskDownloadUrl(DownloadEntity downloadEntity) {
        Bundle bundle = new Bundle();
//		bundle.putString(MoviesHttpApi.LeTvBitStreamParam.KEY_CLOUD_CODE,downloadEntity.getCloudId());
        bundle.putString(MoviesHttpApi.LeTvBitStreamParam.KEY_TYPE, type);
//		bundle.putString(MoviesHttpApi.LeTvBitStreamParam.KEY_SRC, "nets");
//		bundle.putString(MoviesHttpApi.LeTvBitStreamParam.KEY_CDETYPE, MoviesHttpApi.LeTvBitStreamParam.KEY_CDETYPE_AES);
//		bundle.putString(MoviesHttpApi.LeTvBitStreamParam.KEY_REQUESTTYPE, MoviesHttpApi.LeTvBitStreamParam.KEY_DOWNLOAD);
        bundle.putString(MoviesHttpApi.LeTvBitStreamParam.KEY_VID, downloadEntity.getGlobaVid());
        HashMap<String, String> map = new HashMap<>();
        MoviesHttpApi.addVer(map);
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            bundle.putString(key, val);
        }

		LetvHttpParameter<?, ?> httpParameter = MoviesHttpApi.getLetvStreamParameter(new CloudDiskParser(),bundle);
		String url = httpParameter.getBaseUrl() + httpParameter.encodeUrl();
		
//		LogUtils.e("dyf", "!!!!!!!云盘url!!!!!!" + url);
    	
		String json = getHttpRequest(url);
		CloudDiskBean bean = getCloudDiskBean(json, downloadEntity);
        if(bean == null)
            return "";
		String downLoadUrl = getCloudDiskUrl(bean);
		int retryNum= 0;
		while(downLoadUrl == null&&retryNum < 2 ){
			retryNum++;
			json = getHttpRequest(url);
			bean = getCloudDiskBean(json, downloadEntity);
			downLoadUrl = getCloudDiskUrl(bean);
		}
		downloadEntity.setCurrClarity(bean.getVtype());
		return downLoadUrl;
	}

	private String getCloudDiskUrl(CloudDiskBean bean) {
		if (bean != null) {

			String cloudPlayUrl = null;
			if(null!=bean.getmPlayUrls() && bean.getmPlayUrls().size() > 0){
				cloudPlayUrl = bean.getmPlayUrls().get(0);
			}
			return cloudPlayUrl;

		}
		return null;
	}

	private CloudDiskBean getCloudDiskBean(String json, DownloadEntity entity)
	{
		if (!TextUtils.isEmpty(json)) {
			try {
				CloudDiskParser cloudDiskParser = new CloudDiskParser();
				cloudDiskParser.setEntity(entity);
				JSONObject object = cloudDiskParser.getData(json);
				CloudDiskBean data = new CloudDiskParser().parse(object);

				return data;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	/**
	 * 站外源
	 */
	public String getThirdSiteData(DownloadEntity downloadEntity)
	{
		String url;
		try {
			url = initUrl(downloadEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		String json = getHttpRequest(url);

		int retryNum= 0;
		while(json == null&&retryNum < 2 ){
			retryNum++;
			json = getHttpRequest(url);
		}

		return json;
	}
    /**
     * 站外源请求下载filepath
     */
    public void setFilePath(DownloadEntity downloadEntity) {
    	LetvHttpParameter<?,?> httpParameter = MoviesHttpApi.getLetvHttpParameter(
				new SingleInfoParser(), downloadEntity.getGlobaVid(), downloadEntity.getSite(), downloadEntity.getSrc());
    	String url = httpParameter.getBaseUrl() + httpParameter.encodeUrl();
//		LogUtils.e("dyf", "!!!!!!!唯一id请求url!!!!!!"+url);
		String json = getHttpRequest(url);
		SingleInfo singleInfo = getSingleInfo(json);
		int retryNum= 0;
		while(singleInfo == null&&retryNum < 2 ){
			retryNum++;
			json = getHttpRequest(url);
			singleInfo = getSingleInfo(json);
		}

		setDownloadEntity(singleInfo,downloadEntity);
	}
    
    private SingleInfo getSingleInfo(String json) {
		if (!TextUtils.isEmpty(json)) {
			try {
				JSONObject mContent = new JSONObject(json);
					
				SingleInfo data = new SingleInfoParser().parse(mContent);
				if(null==data){
					return null;
				}
				return data;

			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	/**
	 * 给DownloadEntity赋值，
//	 * @see setDownloadUrl(defaultUrl);
//	 * @see setMp4api(mp4api);
//	 * @see setM3u8api(m3u8api);
//	 * @see setRule(mp4param);
//	 * @see setM3u8Rule(m3u8param);
	 */
	public void setDownloadEntity(SingleInfo singleInfo,DownloadEntity downloadEntity){
		HashMap<String, String> mp4Map= singleInfo.getMp4PlayMap();
		HashMap<String, String> mp4ApiMap= singleInfo.getMp4apiMap();
		HashMap<String, String> mp4paramMap= singleInfo.getMp4paramMap();
		HashMap<String, String> m3u8Map= singleInfo.getM3u8PlayMap();
		HashMap<String, String> m3u8ApiMap= singleInfo.getM3u8apiMap();
		HashMap<String, String> m3u8paramMap= singleInfo.getM3u8paramMap();

		/**
		 * m3u8api,m3u8playUrl,mp4api,mp4playUrl使用相同的清晰度
		 */
		DownloadEntity entity = DataUtils.getInstance().getDownloadEntity(downloadEntity);
		if (entity != null && entity.getCurrClarity() != null)
			mDefaultDefinition = entity.getCurrClarity();
		else
			getDefaultDefinition();

		String mp4Url,m3u8Url,mp4api,m3u8api,mp4param,m3u8param;
		downLoadType = downloadEntity.getDownloadType();
		if(StringUtil.isEmpty(downLoadType)){
			downLoadType = DownloadInfo.MP4;
		}

		mp4Url = getDefaultPlayUrl(mp4Map);

		mp4api = mp4ApiMap.get(mDefaultDefinition);
		mp4param = mp4paramMap.get(mDefaultDefinition);

		//m3u8
//		getDefaultDefinition();//重置默认清晰度
		m3u8Url = getDefaultPlayUrl(m3u8Map);

		m3u8api = m3u8ApiMap.get(mDefaultDefinition);
		m3u8param = m3u8paramMap.get(mDefaultDefinition);
		
		/////////////////
		downloadEntity.setCurrClarity(mDefaultDefinition);
		downloadEntity.setDownloadType(downLoadType);
		downloadEntity.setDownloadUrl(mp4Url);
		downloadEntity.setMp4Url(mp4Url);
		downloadEntity.setM3u8Url(m3u8Url);
		downloadEntity.setMp4api(mp4api);
		downloadEntity.setM3u8api(m3u8api);
		downloadEntity.setRule(mp4param);
		downloadEntity.setM3u8Rule(m3u8param);
		downloadEntity.setSnifferUrl(singleInfo.getSnifferUrl());
//		LogUtils.e("dyf", "!!!!!!!唯一id请求数据!!!!!!"+"downLoadType---"+downLoadType+"\n"+"mp4Url"+mp4Url+"\n"
//				+"m3u8Url"+m3u8Url+"\n"+"mp4api"+mp4api+"\n"+"m3u8api"+m3u8api+"\n"+"mp4param"+mp4param+"\n"+"m3u8param"+m3u8param);
	}

	private String decodeString(String in)
	{
		byte[] decodeData = Base64.decode(in, Base64.DEFAULT);
		String result = Utils.AES256_decode(decodeData, Utils.AES_KEY);
		return result;
	}

	public VStreamInfoList getStreamInfoList(String in)
	{
		VStreamInfoList data = getJsonObject(in);
		return data;
	}

	public void setFilePath(DownloadEntity downloadEntity, String in) {
		String json = decodeString(in);
		SingleInfo singleInfo = getSingleInfo(json);
		setDownloadEntity(singleInfo,downloadEntity);
	}
}
