package com.chaojishipin.sarrs.activity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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
import com.chaojishipin.sarrs.listener.MyOnClickListener;
import com.chaojishipin.sarrs.thirdparty.UIs;
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
		initView();
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			topbar.setVisibility(View.GONE);
		}
		playWeb.loadUrl(url);
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
			view.loadUrl(url);
			return true;
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
}
