package com.chaojishipin.sarrs.download.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.chaojishipin.sarrs.async.MoviesHttpAsyncTask;
import com.chaojishipin.sarrs.download.bean.UpdateSnifferInfo;
import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.download.http.parser.UpdateSnifferParser;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.letv.http.bean.LetvDataHull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class UpdateSnifferManager {
	public static final int CHECK_ERROR = 0;
	public static final int FINISH = 1;
	private static boolean isUpdating = false;
	private boolean canWrite = true;//当开始播放时，如果还没更新完，就不允许本次再更改html内容
	private boolean isWritting = false;//为了防止当开始播放时，正在写文件
	private static Context mContext;
	private UpdateSnifferTask requestTask;
	private int reTryNum = 3;// 重试3次
	private String ver;
	private String fileName = "extractor.html";
	// 版本以及路径相关数据保存
	private String SnifferHtml = "snifferHtml";
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
		if (requestTask != null) {
			requestTask.cancel();
		}
		requestTask = new UpdateSnifferTask(mContext);
		requestTask.start();
	}

	private class UpdateSnifferTask extends MoviesHttpAsyncTask<UpdateSnifferInfo> {

		public UpdateSnifferTask(Context context) {
			super(context);
		}

		@Override
		public LetvDataHull<UpdateSnifferInfo> doInBackground() {
			return MoviesHttpApi.requestJsUpdate(new UpdateSnifferParser(), ver);
		}

		@Override
		public void onPostExecute(int updateId, UpdateSnifferInfo result) {
			isUpdating = false;
			if (!StringUtil.isEmpty(result.getCode()) && canWrite) {
				writeHtmlToData(result.getCode(), result.getVersion());
			}
		}

		@Override
		public void netNull() {
			isUpdating = false;
			Message msg = new Message();
			msg.what = CHECK_ERROR;
			mMainHandler.sendMessage(msg);
			super.netNull();
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			isUpdating = false;
			Message msg = new Message();
			msg.what = CHECK_ERROR;
			mMainHandler.sendMessage(msg);
			super.netErr(updateId, errMsg);
		}

	}

	/**
	 * 获取当前html版本
	 */
	private void getHtmlVersion() {
		if (LocaLHtmlExist()) {
			ver = getVersion();
		} else {
			ver = "1";
		}
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
		try {
			fileOutputStream = mContext.openFileOutput(fileName,mContext.MODE_PRIVATE);
			printStream = new PrintStream(fileOutputStream);
			printStream.println(code);
			saveVersion(ver);
		} catch (FileNotFoundException e) {
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

	/**
	 * 保存版本号
	 */
	private void saveVersion(String ver) {
		SharedPreferences sp = mContext.getSharedPreferences(SnifferHtml, Context.MODE_PRIVATE);
		sp.edit().putString("ver", ver).commit();
	}

	/**
	 * 获取版本号
	 */
	private String getVersion() {
		SharedPreferences sp = mContext.getSharedPreferences(SnifferHtml, Context.MODE_PRIVATE);
		return sp.getString("ver", "1");
	}

	/**
	 * 获取html文件路径
	 */
	public String getHtmlURL() {
		String url = null;
		if(isUpdating){
			canWrite = false;//播放器已经调用这个方法，则，不允许再更改Html内容
		}
		if (LocaLHtmlExist()) {
			url = "file:///" + "data" + File.separator + "data"+ File.separator + mContext.getPackageName()+ File.separator + "files" + File.separator + fileName;
		} else {
			url = "file:///android_asset/extractor.html";
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
