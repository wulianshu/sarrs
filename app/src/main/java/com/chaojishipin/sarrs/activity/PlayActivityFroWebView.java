package com.chaojishipin.sarrs.activity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.HistoryRecordResponseData;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.MyOnClickListener;
import com.chaojishipin.sarrs.thirdparty.UIs;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.widget.VideoEnabledWebChromeClient;
import com.chaojishipin.sarrs.widget.VideoEnabledWebView;


public class PlayActivityFroWebView extends ChaoJiShiPinBaseActivity {
	VideoView video;
	private ImageView topBack;
	private ImageView topRefresh;
	private TextView tv_title;
//	private TextView tv_url;
	private VideoEnabledWebView playWeb;
	private ProgressBar pg;
	private String url;
	private String title;
	private String site;
	private float curr_x;
	private float curr_y;
	private float his_y;
	private float his_x;
	private RelativeLayout topbar;
	private VideoEnabledWebChromeClient client;
	private RelativeLayout nonVideoLayout;
	private FrameLayout videoLayout;
	public CustomViewCallback mCallback;
	public RelativeLayout videoLoading;
	private VideoDetailItem videoDetailItem;
	public View mView;
	private boolean isFulllScreen=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		setTitleBarVisibile(false);
		Bundle bundle = getIntent().getExtras();
		url = bundle.getString("url");
		title = bundle.getString("title");
		site = bundle.getString("site");
		videoDetailItem = (VideoDetailItem) bundle.get("videoDetailItem");
		initView();
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			topbar.setVisibility(View.GONE);
		}
		playWeb.loadUrl(url);
//		ToastUtil.showShortToast(this,url);
		save2LocalandServe();
	}

	private void initView() {
		topbar = (RelativeLayout) findViewById(R.id.play_topbar);
		topBack = (ImageView) findViewById(R.id.play_back);
		topBack.setImageResource(R.drawable.selector_ranklistdetail_titlebar);
		topRefresh = (ImageView) findViewById(R.id.play_refresh);
		tv_title = (TextView) findViewById(R.id.play_title);
//		tv_url = (TextView) findViewById(R.id.play_url);
		playWeb = (VideoEnabledWebView) findViewById(R.id.play_webview);
		pg = (ProgressBar) findViewById(R.id.play_progress);
		videoLayout = (FrameLayout) findViewById(R.id.videoLayout);
		nonVideoLayout = (RelativeLayout) findViewById(R.id.nonVideoLayout);
		videoLoading = (RelativeLayout) UIs.inflate(this, R.layout.video_loading_layout, null);
		tv_title.setText(title);
//		tv_url.setText(url);
		client= new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout,videoLoading,pg);
		client.setOnToggledFullscreen(new MyToggledFullscreenCallback());
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		topBack.setOnClickListener(new MyOnClickListener() {

			@Override
			public void onClickListener(View v) {
				finish();
			}
		});
		topRefresh.setOnClickListener(new MyOnClickListener() {

			@Override
			public void onClickListener(View v) {
				playWeb.reload();
			}
		});
		playWeb.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
		if(!(!StringUtil.isEmpty(site) && site.equals("nets"))){
		playWeb.setWebViewClient(new PlayWebViewClient());
		}
		playWeb.setWebChromeClient(client);
		playWeb.getSettings().setPluginState(PluginState.OFF);
//		playWeb.getSettings().setUserAgentString("kuaikan");
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.ICE_CREAM_SANDWICH&&!checkflash()){
			UIs.showToast(R.string.flash_required);
		}
	}

	@Override
	protected View setContentView() {
		return null;
	}

	@Override
	protected void handleInfo(Message msg) {

	}

	@Override
	public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

	}

	private class PlayWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			tv_url.setText(url);
//			if(url.startsWith("letvdisk")){
//				return false;
//			}
//			view.loadUrl(url);
//			return true;

			return false;
		}

		@Override
		// 转向错误时的处理
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		
	}



	@Override
	public void onBackPressed() {
	        	finish();
	}

	private boolean checkflash(){
		PackageManager pm = getPackageManager();  
        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);  
        for (PackageInfo info : infoList) {  
            if ("com.adobe.flashplayer".equals(info.packageName)) {  
                return true;
            }
        }
        return false;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			playWeb.getClass().getMethod("onPause")
			.invoke(playWeb, (Object[]) null);
			playWeb.clearHistory();
			nonVideoLayout.removeView(playWeb);
			playWeb.removeAllViews();
			playWeb.destroy();
			playWeb = null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		playWeb.pauseTimers();
		try {
			playWeb.getClass().getMethod("onPause")
			.invoke(playWeb, (Object[]) null);
			playWeb.pauseTimers();
			playWeb.stopLoading();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		playWeb.resumeTimers();
		try {
			
			playWeb.getClass().getMethod("onResume")
			.invoke(playWeb, (Object[]) null);
			super.onResume();
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	private class MyToggledFullscreenCallback implements VideoEnabledWebChromeClient.ToggledFullscreenCallback {

		@Override
		public void toggledFullscreen(boolean fullscreen) {
			
			if(fullscreen){
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			else{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			topbar.setVisibility(View.GONE);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			topbar.setVisibility(View.VISIBLE);
		}
		super.onConfigurationChanged(newConfig);
	}
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config=new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}


	public void save2LocalandServe() {
		if (UserLoginState.getInstance().isLogin()&& NetWorkUtils.isNetAvailable() && videoDetailItem!=null) {
			String token = UserLoginState.getInstance().getUserInfo().getToken();
			if (videoDetailItem != null && videoDetailItem.getVideoItems() != null && videoDetailItem.getVideoItems().get(0) != null && videoDetailItem.getVideoItems().get(0).getGvid() != null) {
				UploadRecord uploadRecord = new UploadRecord();
				//TODO  吴联暑检查下 有没有类似问题 ?
				if (!TextUtils.isEmpty(videoDetailItem.getCategory_id())) {
					uploadRecord.setCid(Integer.parseInt(videoDetailItem.getCategory_id()));
				}
				uploadRecord.setAction(0);
				uploadRecord.setDurationTime(0);
				uploadRecord.setPid(videoDetailItem.getId());
				uploadRecord.setPlayTime(0);
				uploadRecord.setUpdateTime(System.currentTimeMillis());
				uploadRecord.setSource(videoDetailItem.getSource());
				uploadRecord.setVid(videoDetailItem.getVideoItems().get(0).getGvid());
				uploadHistoryRecordOneRecord(token, uploadRecord);
				//上报到推荐
			}
		}
		saveOnline();
	}
	/**
	 *   本地播放剧集bean localVideoEpiso
	 *   在线是videoItem
	 *   TODO 下载本地剧集信息替换成VideoItem 字段统一
	 *   播放记录在线
	 * */
	public HistoryRecord saveOnline() {
		if (videoDetailItem!=null&&videoDetailItem.getVideoItems() != null && videoDetailItem.getVideoItems().get(0) != null && videoDetailItem.getVideoItems().get(0).getGvid() != null) {
			DataReporter.reportPlayRecord(videoDetailItem.getVideoItems().get(0).getGvid(),
					videoDetailItem.getId(),
					videoDetailItem.getSource(),
					videoDetailItem.getCategory_id(),
					0,
					UserLoginState.getInstance().getUserInfo().getToken(),
					NetWorkUtils.getNetInfo(),
					videoDetailItem.getBucket(),
					videoDetailItem.getReid());

			LogUtil.e("xll", "record save db");
			HistoryRecord historyRecord = new HistoryRecord();
			historyRecord.setImage(videoDetailItem.getDetailImage());
			historyRecord.setSource(videoDetailItem.getSource());
			historyRecord.setCategory_id(videoDetailItem.getCategory_id());
			historyRecord.setTimestamp(System.currentTimeMillis() + "");
			historyRecord.setDurationTime(0);
//			String stitle = "";
			if(!TextUtils.isEmpty(videoDetailItem.getCategory_id())){
				if (videoDetailItem.getCategory_id().equals(ConstantUtils.CARTOON_CATEGORYID)) {
					historyRecord.setCategory_name(this.getString(R.string.CARTOON));
				} else if (videoDetailItem.getCategory_id().equals(ConstantUtils.TV_SERISE_CATEGORYID)) {
					historyRecord.setCategory_name(this.getString(R.string.TV_SERIES));
				} else if (videoDetailItem.getCategory_id().equals(ConstantUtils.MOVIES_CATEGORYID)) {
					historyRecord.setCategory_name(this.getString(R.string.MOVIES));
				} else if (videoDetailItem.getCategory_id().equals(ConstantUtils.DOCUMENTARY_CATEGORYID)) {
					historyRecord.setCategory_name(this.getString(R.string.DOCUMENTARY));
				} else if (videoDetailItem.getCategory_id().equals(ConstantUtils.VARIETY_CATEGORYID)) {
					historyRecord.setCategory_name(this.getString(R.string.VARIETY));
				} else {
					historyRecord.setCategory_name(this.getString(R.string.OTHER));
				}
//				historyRecord.setPlay_time("0");
//				String title = videoDetailItem.getTitle();
			}
//			historyRecord.setTitle(stitle);
			historyRecord.setContent_type(videoDetailItem.getContent_type());
			historyRecord.setId(videoDetailItem.getId());
			historyRecord.setGvid(videoDetailItem.getVideoItems().get(0).getGvid());
			historyRecord.setUrl(url);
			historyRecord.setTitle(videoDetailItem.getVideoItems().get(0).getTitle());
			new HistoryRecordDao(this).save(historyRecord);
			return historyRecord;
		}
		return null;
	}

	/**
	 * 上报历史记录
	 *
	 * @paramcid
	 */
	private void uploadHistoryRecordOneRecord(String token, UploadRecord historyRecord) {
		//请求频道页数据
		HttpManager.getInstance().cancelByTag(ConstantUtils.UPLOAD_HISTORY_RECORD_ONE_RECORD);
		HttpApi.
				uploadHistoryRecordoneRecord(token, historyRecord)
				.start(new UploadHistoryRecordListener(), ConstantUtils.UPLOAD_HISTORY_RECORD_ONE_RECORD);
	}

	private class UploadHistoryRecordListener implements RequestListener<HistoryRecordResponseData> {

		@Override
		public void onResponse(HistoryRecordResponseData result, boolean isCachedData) {
		}

		@Override
		public void netErr(int errorCode) {
			System.out.print(errorCode);
		}

		@Override
		public void dataErr(int errorCode) {
			System.out.print(errorCode);
		}
	}
}
