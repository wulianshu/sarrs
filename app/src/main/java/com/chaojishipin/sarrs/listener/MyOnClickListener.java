package com.chaojishipin.sarrs.listener;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class MyOnClickListener implements OnClickListener {
	private static long lastTime = 0;
	protected Bundle mBundle;
	/**
	 * 点击最短间隔时间 0.1s
	 */
	private final static int CLICK_TIME_ELAPSE = 100;

	public MyOnClickListener() {
	}

	public MyOnClickListener(Bundle bundle) {
		mBundle = bundle;
	}

	@Override
	public void onClick(View v) {
		if ((System.currentTimeMillis() - lastTime) >= CLICK_TIME_ELAPSE) {
			lastTime = System.currentTimeMillis();
			onClickListener(v);
		}
	}

	public abstract void onClickListener(View v);
}
