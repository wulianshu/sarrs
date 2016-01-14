package com.chaojishipin.sarrs.download.download;


public class Constants
{
	public static final String DIR = "Directory";
	
	public static final String HISTORY = "playhistory";
	
	public static final String FROM_BAIDU = "baidu";
	
	public static final String DESC = "0";
	
	public static final int MESSAGE_SCAN_BEGIN = 2001;
	public static final int MESSAGE_SCAN_OVER = 2002;
	
	public static final int MESSAGE_DELETE_FILE = 3001;
	public static final int MESSAGE_DELETE_ALLFILES = 3002;
	public static final int MESSAGE_DELETE_DOWNLOAD_FILE = 3003;
	
	public static final String V3 = "v3";
	
	public final static String DOWNLOAD_KEY = "DOWNLOAD_Play";
	
	public final static int Qihoo_LIGHT_ON = 1;
	
	public final static int Qihoo_LIGHT_OFF = 2;
	
	public static final String QIHOO_APK_DOWNLOAD_URL = "http://shouji.360.cn/360safe/104751/360MobileSafe.apk";
    
    public static final String GET_SERIAL_PLAY = V3+"/media/get_serial_play?cli="+
          "aphone"+"&ver="+"2.1.1"+"&ta="+"&sid="+"";
    
    public static final String GET_QIHOO_LIGHT_POINT_URL = V3 + "/app/get_setting?cli=" +
    		"aphone" + "&ver=" + "2.1.1" + "&ta=" + "&sid=" + "" + 
    		"&type=" + "light_point";
    
    public static final String NET_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
}
