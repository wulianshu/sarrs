package com.chaojishipin.sarrs.dao;

import android.os.Handler;
import android.os.Looper;

public class DaoThread extends Thread {

	private Handler mHandler ;

	public DaoThread() {
		super();
	}
	@Override
	public void run() {
		Looper.prepare();
		mHandler = new Handler();
		Looper.loop();
		super.run();
	}

	public Handler getmHandler() {
		return mHandler;
	}
	public void post(Runnable r) {
		if(mHandler!=null){
			mHandler.post(r);
		}

	}

}
