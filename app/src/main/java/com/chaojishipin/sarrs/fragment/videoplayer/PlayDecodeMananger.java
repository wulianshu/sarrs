package com.chaojishipin.sarrs.fragment.videoplayer;

import com.letv.component.player.LetvVideoViewBuilder.Type;

/**
 * 当前优先使用什么播放格式的管理类
 *
 */
public class PlayDecodeMananger {

	private static boolean mNeedSysDecoder = false;

	public final static String CLOUD_M3U8 = "1";

	public final static String CLOUD_MP4 = "0";
	
    private final static String M3U8 = "m3u8";
    
    private final static String Mp4 = "mp4";

	/**
	 * 获取当前云盘资源类型
	 *
	 */
	public static String getCloudSourceType() {
		String cloudPlayType = ismNeedSysDecoder() ? CLOUD_MP4 : CLOUD_M3U8;
		return cloudPlayType;
	}

	/**
	 * 获取当前播放器的编解码格式
	 *
	 */
    public static Type getCurrPlayerType() {
        Type playerType = ismNeedSysDecoder() ? Type.MOBILE_H264_MP4 : Type.MOBILE_H264_M3U8;
        return playerType;
    }

	public static boolean ismNeedSysDecoder() {
		return mNeedSysDecoder;
	}

	public static void setmNeedSysDecoder(boolean mNeedSysDecoder) {
		PlayDecodeMananger.mNeedSysDecoder = mNeedSysDecoder;
	}

}
