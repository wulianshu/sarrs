package com.chaojishipin.sarrs.utils;

import android.net.TrafficStats;

/**
 * 流量统计工具类
 *
 */
public class TrafficStatsUtil {

	private static final String TAG = "TrafficStatsUtil";

	public static int UNSUPPORTED = -1;
	/**
	 * 首次接受到的字节数
	 */
	public static long mPreRxBytes = UNSUPPORTED;

	/**
	 * 计算当前网速
	 */
	public static String countCurRate() {
		String curRate = "0B/s";
		try {
			// 当前接受到的字节总数
			long curRxBytes = TrafficStats.getTotalRxBytes();
			// LogUtil.v(TAG, "接收到的总字节数"+curRxBytes);
			// 这次接受到的字节数
			if (mPreRxBytes == UNSUPPORTED) {
				mPreRxBytes = curRxBytes;
			}
			long curRateBytes = curRxBytes - mPreRxBytes;
			// 更新上一次接收到的字节总数
			mPreRxBytes = curRxBytes;
			if(curRateBytes < 0){
				curRate = "0B/s";
			}else if (curRateBytes>=0 && curRateBytes < 1024) {
				// LogUtil.v(TAG, "当前网速" + curRateBytes + "B/s");
				curRate = curRateBytes + "B/s";
			}else if (curRateBytes >= 1024 && curRateBytes < 1024 * 1024) {
				// LogUtil.v(TAG, "当前网速" + curRateBytes / 1024 + "K/s");
				curRate = curRateBytes / 1024 + "K/s";
			}else if (curRateBytes >= 1024 * 1024) {
				// LogUtil.v(TAG, "当前网速" + curRateBytes / (1024 * 1024) +
				// "M/s");
				curRate = curRateBytes / (1024 * 1024) + "M/s";
			}

		} catch (NoClassDefFoundError e) {
			mPreRxBytes = UNSUPPORTED;
			e.printStackTrace();
			return "0B/s";
		} catch (Exception e) {
			mPreRxBytes = UNSUPPORTED;
			e.printStackTrace();
			return "0B/s";
		}
		return curRate;
	}

	/**
	 * 计算当前的网速
	 * 
	 * @param startPos
	 * @param endtime
	 * @return
	 */
	public static long getAverageRateSpeed(long startPos, long endtime) {
		long averageRateSpeed;
		try {
			// 当前接受到的字节总数
			long curRateBytes = TrafficStats.getTotalRxBytes() - startPos;
			LogUtil.i(TAG, "接收到的总字节数" + curRateBytes);
			double time = ((double) System.currentTimeMillis() - (double) endtime);
			averageRateSpeed = (int) ((double) curRateBytes / time);
			LogUtil.i(TAG, "缓冲过程的平均网速：" + averageRateSpeed + "B/s");
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return averageRateSpeed;
	}

	/**
	 * 获取流量方法是否可以获得流量值
	 */
	public static void getPreRxByte() {
		try {
			mPreRxBytes = TrafficStats.getTotalRxBytes();
			if(mPreRxBytes <= 0){
				mPreRxBytes = UNSUPPORTED;
			}
		} catch (NoClassDefFoundError e) {
			mPreRxBytes = UNSUPPORTED;
			e.printStackTrace();
		} catch (Exception e) {
			mPreRxBytes = UNSUPPORTED;
			e.printStackTrace();
		}
	}
}
