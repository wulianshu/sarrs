package com.letv.component.player;

import java.util.Map;

import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import com.letv.component.player.Interface.OnMediaStateTimeListener;
import com.letv.component.player.Interface.OnVideoViewStateChangeListener;
import com.letv.component.player.core.PlayUrl;
import com.letv.component.player.core.PlayUrl.StreamType;
import com.media.ffmpeg.FFMpegPlayer.OnAdNumberListener;
import com.media.ffmpeg.FFMpegPlayer.OnBlockListener;
import com.media.ffmpeg.FFMpegPlayer.OnHardDecodeErrorListner;

/**
 * 播放器实现接口
 * @author chenyueguo
 */
public interface LetvMediaPlayerControl extends MediaPlayerControl {
	
	
	/**
	 * 方式一，传入PlayUrl
	 * @param url
	 */
	public void setVideoPlayUrl(PlayUrl url);

	/**
	 * 方式二，设置视频路径。
	 */
	public void setVideoPath(String videoPath);
	
	/**
	 * 方式三，设置视频路径,带头信息。TV端使用
	 */
	public void setVideoPath(String videoPath, Map<String, String> headers);
	
	/**
	 * 视频停止播放
	 */
	public void stopPlayback();
	
	/**
	 * 快进，modile和tv端快进单位不同,moblie每次快进15000ms，tv每次快进20000ms
	 */
	public void forward(); //快进
	
	/**
	 * 快退，modile和tv端快进单位不同,moblie每次快退15000ms，tv每次快退20000ms
	 */
	public void rewind(); //快退
	
	/**
     * 自适应屏幕 -1-初始化状态 0-自动 1-4:3 2-16:9
     */
    public void adjust(int type);
	
	/**
	 * 返回view对象，用于填充到布局。
	 */
	public View getView();
	
	/**
	 * 获取播放器销毁前时间点。
	 */
	public int getLastSeekWhenDestoryed();
	
	/**
	 * 判断播放器是否为暂停状态。
	 */
	public boolean isPaused();
	
	/**
	 * 判断是否在可控状态，
	 */
	public boolean isInPlaybackState();
	
	/**
	 * 设置媒体控制器。
	 */
	public void setMediaController(MediaController controller);
	
	/**
	 * 注册一个回调函数，在视频预处理完成后调用。此时视频的宽度、高度、宽高比信息已经获取到，此时可调用seekTo让视频从指定位置开始播放。
	 */
	public void setOnPreparedListener(OnPreparedListener l);
	
	/**
	 * 注册一个回调函数，视频播放完成后调用。
	 */
	public void setOnCompletionListener(OnCompletionListener l);
	
	/**
	 * 注册一个回调函数，在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化。
	 */
	public void setOnInfoListener(OnInfoListener l);
	
	/**
	 * 注册一个回调函数，在异步操作调用过程中发生错误时调用。例如视频打开失败。
	 */
	public void setOnErrorListener(OnErrorListener l);
	
	/**
	 * 注册一个回调函数，在seek操作完成后调用。
	 */
	public void setOnSeekCompleteListener(OnSeekCompleteListener l);
	
	/**
	 * 注册一个回调函数，在获取视频大小或视频大小改变时调用。
	 */
	public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener l);
	
	/**
	 * 注册一个回调函数，在网络视频流缓冲变化时调用。
	 */
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l);
	
	/**
	 * 注册一个回调函数，在视频状态信息改变时回调。该回调是兼容移动端原来接口。
	 */
	public void setVideoViewStateChangeListener(OnVideoViewStateChangeListener videoViewStateChangeListener);
	
	/**
	 * 注册一个回调函数，通知上层当前播放到哪一个广告
	 */
	public void setOnAdNumberListener(OnAdNumberListener l);
	
	/**
	 * 注册一个回调函数，通知上层当前卡顿了
	 */
	public void setOnBlockListener(OnBlockListener l);
	
	/**
	 * 注册回调函数，通知上层当前播放器状态时间点
	 */
	public void setOnMediaStateTimeListener(OnMediaStateTimeListener l);
	
	/**
	 * 硬解播放播放失败
	 */
	public void setOnHardDecodeErrorListener(OnHardDecodeErrorListner l);
	
	/**以下四个方法，在播广告时，强制停止正片播放**/
	public boolean isEnforcementWait();

	public void setEnforcementWait(boolean enforcementWait);

	public boolean isEnforcementPause();

	public void setEnforcementPause(boolean enforcementPause);
	
	public void setCacheSize(int video_size,int audio_size,int picutureSize, int startpic_size);

}
