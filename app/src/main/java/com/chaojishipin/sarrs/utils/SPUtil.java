package com.chaojishipin.sarrs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

/**
 * @CopyRight: letv.
 * @Description:文件读写类.
 * @Author: xulinlin.
 * @Create: 2014年6月12日.
 */

public class SPUtil {
     // 
    
	private SharedPreferences sp;
	private Editor edit;
	private static SPUtil spUtil;
	private SPUtil(Context context) {
		sp = context.getSharedPreferences("sarrsconfig", Context.MODE_PRIVATE);
	}

	public static final synchronized SPUtil getInstance() {
		if (spUtil == null) {
			spUtil = new SPUtil(ChaoJiShiPinApplication.getInstatnce());
		}
		return spUtil;

	}

	public void putString(String key, String value) {
		edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	   public void putInt(String key, int value) {
	        edit = sp.edit();
	        edit.putInt(key, value);
	        edit.commit();
	    }

	public String getString(String key, String defaultValue) {
		return sp.getString(key, defaultValue);
	}
    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

	public void putBoolean(String key, boolean value) {
		edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}

}
