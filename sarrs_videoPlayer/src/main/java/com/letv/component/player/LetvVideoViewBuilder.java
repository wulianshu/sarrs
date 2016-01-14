package com.letv.component.player;

import android.content.Context;

import com.letv.component.player.core.Configuration;
import com.letv.component.player.core.LetvMediaPlayerManager;
import com.letv.component.player.http.HttpServerConfig;
import com.letv.component.player.utils.LogTag;
import com.letv.component.player.videoview.VideoViewH264LeMobile;
import com.letv.component.player.videoview.VideoViewH264m3u8;
import com.letv.component.player.videoview.VideoViewH264m3u8Hw;
import com.letv.component.player.videoview.VideoViewH264m3u8HwLeMobile;
import com.letv.component.player.videoview.VideoViewH264mp4;
import com.letv.component.player.videoview.VideoViewTV;

/**
 * 创建VideView统一入口类
 * @author chenyueguo
 *
 */
public class LetvVideoViewBuilder {
	
	/**
	 * 创建VideoView类型
	 */
	public enum Type{
		TV, // for TV
		MOBILE_H264_MP4, //hardware
		MOBILE_H264_M3U8, //h264 software
		MOBILE_H264_M3U8_HW, //h264 custom hardware
		MOBILE_H264_LE_MOBILE, 
		MOBILE_H264_M3U8_HW_LE_MOBILE
	}
	
	private static LetvVideoViewBuilder mLetvVideoViewBuilder = null;
	
	/**
	 * 获取单例
	 * @modify wangshenhao 2015/1/15 17:47
	 * @return
	 */
	public static LetvVideoViewBuilder getInstants() {
		if (mLetvVideoViewBuilder == null) {
			createBuilder();
		}
		return mLetvVideoViewBuilder;
	}
	
	private synchronized static void createBuilder(){
		if(mLetvVideoViewBuilder==null){
			mLetvVideoViewBuilder = new LetvVideoViewBuilder();
		}
	}
	
	private LetvVideoViewBuilder() {
		
	}

	/**
	 * 根据Type类型创建播放器VideoView
	 * @param context
	 * @param type MOBILE_H264_MP4、MOBILE_H264_M3U8、MOBILE_H264_M3U8_HW或者TV
	 * @return LetvMediaPlayerControl
	 */
	public LetvMediaPlayerControl build(Context context, Type type) {
		LogTag.i("Create player, type="
				+ (type != null ? type.toString() : "null") + ", version="
				+ Configuration.SDK_VERSION_CODE);
		LetvMediaPlayerControl mLetvMediaPlayerControl = null;
		switch (type) {
		case MOBILE_H264_MP4:
			mLetvMediaPlayerControl = new VideoViewH264mp4(context);
			break;
		
		case MOBILE_H264_M3U8:
			mLetvMediaPlayerControl = new VideoViewH264m3u8(context);
			break;
			
		case MOBILE_H264_M3U8_HW:
			mLetvMediaPlayerControl = new VideoViewH264m3u8Hw(context);
			break;
			
		case TV:
			mLetvMediaPlayerControl = new VideoViewTV(context);
			break;
			
		case MOBILE_H264_LE_MOBILE:
			mLetvMediaPlayerControl = new VideoViewH264LeMobile(context);
			break;
			
		case MOBILE_H264_M3U8_HW_LE_MOBILE:
			mLetvMediaPlayerControl = new VideoViewH264m3u8HwLeMobile(context);
		}
		return mLetvMediaPlayerControl;
	}

	
	/**
	 * 获取类型
	 * @param control LetvMediaPlayerControl
	 * @return MOBILE_H264_MP4、MOBILE_H264_M3U8、MOBILE_H264_M3U8_HW或者TV
	 */
	public Type getType(LetvMediaPlayerControl control) {
		Type type = Type.MOBILE_H264_MP4;
		if(control instanceof VideoViewH264m3u8Hw) {
			type = Type.MOBILE_H264_M3U8_HW;
		} else if(control instanceof VideoViewH264m3u8) {
			type = Type.MOBILE_H264_M3U8;
		} else if(control instanceof VideoViewTV) {
			type = Type.TV;
		}else if(control instanceof VideoViewH264LeMobile){
			type = Type.MOBILE_H264_LE_MOBILE;
		}else if(control instanceof VideoViewH264m3u8HwLeMobile){
			type = Type.MOBILE_H264_M3U8_HW_LE_MOBILE;
		}
		return type;
	}
}
