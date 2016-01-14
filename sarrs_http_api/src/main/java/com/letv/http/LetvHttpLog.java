package com.letv.http;

public final class LetvHttpLog {

	/**
	 * 打印Debug级别信息
	 * */
	public static void Log(String msg){
		if(LetvHttpConstant.isDebug){
			if(msg == null){
				msg = "null";
			}
			android.util.Log.d(LetvHttpConstant.LOG, msg);
		}
	}
	
	/**
	 * 打印Error级别信息
	 * */
	public static void Err(String msg){
		if(LetvHttpConstant.isDebug){
			if(msg == null){
				msg = "null";
			}
			android.util.Log.e(LetvHttpConstant.LOG, msg);
		}
	}
}
