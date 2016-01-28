package com.chaojishipin.sarrs.config;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;
import android.widget.ToggleButton;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.utils.DataUtils;


public class SettingManage {

	private Context mContext;
	
	//设置里面用到的相关sharepreference
	public static final String SETTING_RELATIVE_SHAREPREFERENCE = "setting_relative_sharepreference";
	//是否只允许wifi状态下下载
	public static final String IS_DOWNLOAD_CAN_3G= "is_downloadcan_3g";
	//是否接收push通知
	public static final String IS_RECEIVE_PUSH_NOTIFICATION = "is_receive_push_notification";
	//push通知相关数据保存
	private static final String PUSH = "push";
	//同时下载任务数
	public static final String DOWNLOAD_NUM_SAME_TIME = "download_num_same_time";
	//是否显示桌面通知
	public static final String DISPLAY_DESKTOP_NOTIFY_ON_OFF = "display_desktop_notify_on_off";
	//码率选择sharepreference
	public static final String CODE_RATE_CONFIG = "code_rate_config";
	//下载时优先选择的码率类型
	public static final String CODE_RATE = "code_rate";
	
	//反馈弹窗数据保存
	public static final String IS_SHOW_SCOREPOP = "is_show_score_pop";
	
	
	public SettingManage(Context context){
		this.mContext = context;
	}
	
	private static SettingManage instance = new SettingManage(ChaoJiShiPinApplication.getInstatnce());

	public static SettingManage getInstance() {
		return instance;
	}
	
	/**
	 * 推送是否开启
	 * */
	public boolean isPush() {
		SharedPreferences sp = mContext.getSharedPreferences(IS_RECEIVE_PUSH_NOTIFICATION, Context.MODE_PRIVATE);
		return sp.getBoolean("isPush", true);
	}

	/**
	 * 设置是否开启推送
	 * */
	public void setIsPush(boolean isPush) {
//		SharedPreferences sp = mContext.getSharedPreferences(IS_RECEIVE_PUSH_NOTIFICATION, Context.MODE_PRIVATE);
//		sp.edit().putBoolean("isPush", isPush).commit();
//		if(isPush){
//			PushService.schedule(mContext);
//		}else{
//			PushService.unschedule(mContext);
//		}
	}
	
	public long getPushTime() {
		SharedPreferences sp = mContext.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		long time = sp.getLong("time", 0);

		return time;
	}

	public void savePushDistance(int time) {
		SharedPreferences sp = mContext.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		sp.edit().putInt("distance", time).commit();
	}

	public int getPushDistance() {
		SharedPreferences sp = mContext.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		int time = sp.getInt("distance", 30*60);
//		return 10;
		return time;
	}

	public void savePushTime(long time) {
		SharedPreferences sp = mContext.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		sp.edit().putLong("time", time).commit();
	}

	public void setPushToggleButtonState(ToggleButton toggleButton){
		if(null == toggleButton){
			return ;
}
		if(toggleButton.isChecked()){
			toggleButton.setChecked(false);
			setIsPush(false);
		}else{
			toggleButton.setChecked(true);
			setIsPush(true);
		}
	}
	public void setPushToggleButtonPreference(boolean isChecked){
		setIsPush(isChecked);
	}
	
	/**
	 * 启动程序的次数
	 * */
	public int isshowTimes() {
		try {
			SharedPreferences sp = mContext.getSharedPreferences(IS_SHOW_SCOREPOP, Context.MODE_PRIVATE);
			sp.edit().putInt("is_show_score_pop", sp.getInt("is_show_score_pop", 0)+1).commit();
			return sp.getInt("is_show_score_pop", 0);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void popIfContinueDownloadDialog(boolean isChecked) {
		if(null == mContext){
			return ;
		}
	if(NetworkUtil.reportNetType(mContext) == NetworkUtil.TYPE_MOBILE && !isChecked &&
				DataUtils.getInstance().getDownloadJobNum() > 0){
			checkIfContinueDownloadDialog(isChecked);		
		}else{
			sendBroadCast(isChecked);
		}
	}
	
	private void checkIfContinueDownloadDialog(final boolean isChecked) {
		Builder customBuilder = new Builder(mContext);
		AlertDialog dialog = null;
		customBuilder
		        .setTitle(R.string.tip)
		        .setMessage(R.string.wireless_tip)
		        .setPositiveButton(R.string.continue_download,
		                new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	sendBroadCast(isChecked);
		                    	dialog.dismiss();
		                    }
		                })
		        .setNegativeButton(R.string.pause_download,
		                new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	dialog.dismiss();
		                    }
		                })
		        .setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
	                        dialog.dismiss();
	                    }
						return false;
					}
				});
		if(dialog==null)
			dialog = customBuilder.create();
		dialog.show();
	}
	
	public void sendBroadCast(boolean isDownloadOnlyWifi){
		if(null == mContext){
			return ;
		}
		Intent intent = new Intent();
		intent.setAction("com.funshion.video.DOWNLOADCAN3G");
		intent.putExtra("isDownloadcan3g", isDownloadOnlyWifi);
		mContext.sendBroadcast(intent);
    }
	
	public void setToggleButtonState(ToggleButton toggleButton,
			Editor editor, String editorKey){
		if(null == toggleButton || null == editor){
			return ;
		}
		if(toggleButton.isChecked()){
			toggleButton.setChecked(false);
			editor.putBoolean(editorKey, false);
		}else{
			toggleButton.setChecked(true);
			editor.putBoolean(editorKey, true);
		}
		editor.commit();
	}
	
	public void setToggleButtonPreference(boolean isChecked, Editor editor, String editorKey){
		if(null == editor){
			return ;
		}
		editor.putBoolean(editorKey, isChecked);
		editor.commit();
	}

}
