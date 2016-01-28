package com.chaojishipin.sarrs.download.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.utils.DataUtils;


public class SdcardBroadcastReceiver extends BroadcastReceiver {
	
	public final String TAG = "SdcardBroadcastReceiver";
	public final static String SDCARD_CHANGE = "com.funshion.video.sdchange";

	@Override
	public void onReceive(final Context context, Intent intent) {
		if(intent != null) {
			if(context==null)
				return;
			String action = intent.getAction();
			if(Intent.ACTION_MEDIA_MOUNTED.equals(action)) {//插入sd卡
				DownloadHelper.NEW_ADDED_SDCARDPATH = intent.getData().getPath();
				Intent i = new Intent();
				i.setAction(SDCARD_CHANGE);
				context.sendBroadcast(i);
//				LogUtils.i(TAG, "插入sd卡 -- " + DownloadHelper.NEW_ADDED_SDCARDPATH);
				ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if(wifi.isConnected()){
					DataUtils.getInstance().startAllDownload();
				}
			} else if(Intent.ACTION_MEDIA_EJECT.equals(action)) {//拔出sd卡
				DownloadHelper.NEW_ADDED_SDCARDPATH = "";
				new Thread() {
					@Override
					public void run() {
						try {
							sleep(1000);
							DownloadHelper.RESTORE_FLAG = false;
							DownloadHelper.storageSdcardCount();
							DownloadHelper.getDefaultDownloadPath();
							DownloadHelper.saveReallyDownloadPath(DownloadHelper.DOWNLOAD_FILEPATH_NUMBER);
							DownloadHelper.storageAllExtSdcardPath();
							Intent i = new Intent();
							i.setAction(SDCARD_CHANGE);
							context.sendBroadcast(i);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		}
	}

}
