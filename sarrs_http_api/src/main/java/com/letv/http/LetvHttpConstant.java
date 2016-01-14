package com.letv.http;

public final class LetvHttpConstant {

	/**
	 * 读取超时时间
	 * */
	public static final int READ_TIMEOUT = 10 * 1000 ;
	
	/**
	 * 连接超时时间
	 * */
	public static final int CONNECT_TIMEOUT = 10 * 1000 ;
	
	/**
	 * LOG名
	 * */
	public static final String LOG = "LetvHttp" ;
	
	public static final String REFERER = "referer";
	
	public static final String USER_AGENT = "User-agent";
	
	/**
	 * 是否debug
	 * */
	public static boolean isDebug = false ;
	
	public static void setDebug(boolean isDebug){
		LetvHttpConstant.isDebug = isDebug ;
	}
}
