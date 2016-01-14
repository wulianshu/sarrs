package com.chaojishipin.sarrs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.bean.UpdateSnifferInfo;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;


public class UpdateSnifferManager {
	public static final int CHECK_ERROR = 0;
	public static final int FINISH = 1;
	private static boolean isUpdating = false;
	private boolean canWrite = true;//当开始播放时，如果还没更新完，就不允许本次再更改html内容
	private boolean isWritting = false;//为了防止当开始播放时，正在写文件
	private static Context mContext;
	private int reTryNum = 3;// 重试3次
	// js 版本号
	private String ver;
	//NEW js 返回的是js文件 不是html文件
	private String fileName = "extractor.html";
	// 版本以及路径相关数据保存
	private String SnifferHtml = "snifferjs";
	private static UpdateSnifferManager updateSnifferManager;

	private Handler mMainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_ERROR:
				if (reTryNum > 0) {
				    startUpdate();
				}
				reTryNum--;
				break;
			case FINISH:
				break;
			}
		}
	};

	public static UpdateSnifferManager getInstance(Context context) {
		if (null == updateSnifferManager) {
			updateSnifferManager = new UpdateSnifferManager();
			mContext = context;

		}
		return updateSnifferManager;
	}

	public void startUpdate() {
		if (isUpdating) {
			return;
		}
		getHtmlVersion();
		isUpdating = true;
		updateJsCode();
	}

	/**
	 *  根据返回js规则请求js code
	 * */
	private void updateJsCode() {
		LogUtil.e("xll","NEW  jsCode request start !");
		HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_JSCODE);
		HttpApi.udpateJsCode(ver).start(new udpateSniff());
	}

	class udpateSniff implements RequestListener<UpdateSnifferInfo> {

		@Override
		public void onResponse(UpdateSnifferInfo result, boolean isCachedData) {
			isUpdating = false;
			if (!StringUtil.isEmpty(result.getCode())) {
				LogUtil.e("xll","NEW  jsCode response !"+result.getVersion());
				writeHtmlToData(result.getCode(), result.getVersion());
			}
		}

		@Override
		public void netErr(int errorCode) {
			//handlerOutSiteError();
			isUpdating = false;
			Message msg = new Message();
			msg.what = CHECK_ERROR;
			mMainHandler.sendMessage(msg);
		}

		@Override
		public void dataErr(int errorCode) {
			isUpdating = false;
			Message msg = new Message();
			msg.what = CHECK_ERROR;
			mMainHandler.sendMessage(msg);
		}
	}
	/**
	 * 获取当前html版本
	 * 默认版本号是 0，服务端返回数据
	 */
	private String getHtmlVersion() {
		if (LocaLHtmlExist()) {
			ver = getVersion();
		} else {
			ver = "0";
		}
		return ver;
	}

	/**
	 * 判断data里html文件是否存在
	 * 
	 * @return
	 */
	private Boolean LocaLHtmlExist() {
		FileInputStream fileOutputStream = null;
		try {
			fileOutputStream = mContext.openFileInput(fileName);
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			if (null != fileOutputStream) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return true;
	}

	/**
	 * 更新Html代码
	 */
	private void writeHtmlToData(String code, String ver) {
		isWritting = true;
		FileOutputStream fileOutputStream = null;
		PrintStream printStream = null;
		if(ver.equalsIgnoreCase(getHtmlVersion())){
               // 本地版本号和服务端不一致时才去写到本地
			LogUtil.e("xll","NEW jscode file do not save! ");
		}else{
			LogUtil.e("xll","NEW jscode file  save local ! ");
			try {
				fileOutputStream = mContext.openFileOutput(fileName,mContext.MODE_PRIVATE);
				printStream = new PrintStream(fileOutputStream);
				LogUtil.e("xll","Js file name "+fileName);
				String header="<!doctype html>\n" +
						"<html><head>\n" +
						"    <style type='text/css'>\n" +
						"        html { font-family:Helvetica; color:#222; }\n" +
						"        h1 { color:steelblue; font-size:24px; margin-top:24px; }\n" +
						"        button { margin:0 3px 10px; font-size:12px; }\n" +
						"        .logLine { border-bottom:1px solid #ccc; padding:4px 2px; font-family:courier; font-size:11px; }\n" +
						"        </style>\n" +
						"</head><body>\n" +
						"    <h1>WebViewJavascriptBridge Demo</h1>\n" +
						"    <script>";
				String footer="</script>\n" +
						"</body></html>\n";
				printStream.println(header);
				printStream.println(code);
				printStream.println(footer);
				saveVersion(ver);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				isWritting = false;
				try {
					printStream.close();
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


	}

	/**
	 * 保存版本号
	 */
	private void saveVersion(String ver) {
		SharedPreferences sp = mContext.getSharedPreferences(SnifferHtml,Context.MODE_PRIVATE);
		sp.edit().putString("ver", ver).commit();
	}

	/**
	 * 获取版本号
	 */
	private String getVersion() {
		SharedPreferences sp = mContext.getSharedPreferences(SnifferHtml,Context.MODE_PRIVATE);
		return sp.getString("ver", "0");
	}

	/**
	 * 获取html文件路径
	 */
	public String getHtmlURL() {
		String url = null;
		if(isUpdating){
			canWrite = false;//播放器已经调用这个方法，则，不允许再更改Html内容
		}
		if (!isWritting && LocaLHtmlExist()) {

			url = "file:///" + "data" + File.separator + "data"+ File.separator + mContext.getPackageName()+ File.separator + "files" + File.separator + fileName;

			LogUtil.e("xll","截流html 的位置为内存");
		} else {
			url = "file:///android_asset/extractor.html";
			LogUtil.e("xll","截流html 的位置为Asset");
		}
		return url;
	}
	/**
	 * 退出程序后，重置变量
	 */
	public void resetVariable(){
		canWrite = true;
		isWritting = false;
		reTryNum = 3;
	}
}
