package com.chaojishipin.sarrs.download.download;

public interface DownloadHandler {
	int INIT = 5;
	int DOWNLOADING = 2;
	int PAUSE = 3; //指用户人为暂停
	int WAITING = 4;
	int COMPLETE = 1;
	int DELETE = 6;
	int PAUSEONSPEED = 7;
	int NO_USER_PAUSE = 0; //指网络等其他原因非用户任务暂停
	
	int NET_TIMEOUT = 1;
	int FILE_NOT_FOUND = 2;
	int SD_SPACE_FULL = 3;
	int OTHER_EXCEP = 9;

	/**
	 * 下载功能是成功，用户主动取消下载，认为该操作仍然是成功
	 */
	int DOWNLOAD_SUCCESS = 0;

	/**
	 * 下载地址有效，可以使用该地址，如果下载中断或者失败，可以继续使用该地址重试下载
	 */
	int DOWNLOAD_URL_VALID = 1;

	/**
	 * 下载地址无效，需要更换地址重新下载
	 */
	int DOWNLOAD_URL_INVALID = 2;

	/**
	 * 下载文件错误
	 */
	int DOWNLOAD_FILE_ERROR = 3;

	
	public void onStart();
	public int downloadFile(DownloadJob job) throws Exception;
	public void onCompleted();
	public void onFailure();
	public void onPause(DownloadJob job);
}
