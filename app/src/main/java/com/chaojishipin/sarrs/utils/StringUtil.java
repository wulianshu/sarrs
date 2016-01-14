package com.chaojishipin.sarrs.utils;

import android.text.TextUtils;

/**
 * 字符串工具类
 * @author daipei
 */
public class StringUtil {
	private final static String TAG = "StringUtil";

	/**
	 * 判断是否为空
	 */
	public static boolean isEmpty(String str) {
		if (TextUtils.isEmpty(str) || (str != null && "".equals(str.trim())||(str != null && "null".equals(str)))) {
			return true;
		}
		return false;
	}

	/**
	 * 去掉标红
 	 */
	public static String deleteSignRed(String oldString) {
		String newString = null;

		newString = oldString.replace((char) 1,'$').replace((char) 2, '$').replace("$","");
		newString = newString.replace("\\u0001", "").replace("\\u0002", "").trim();

		return newString;

	}

}
