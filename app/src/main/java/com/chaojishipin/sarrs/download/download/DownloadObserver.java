package com.chaojishipin.sarrs.download.download;


public interface DownloadObserver {
	
	void onDownloadChanged(DownloadManager manager);
	/**
	 * 如果下载完成的job，是删除界面中被勾选的，那么回调，通知被勾选数量自减。
	 */
	void onDownloadEnd(DownloadManager manager, DownloadJob job);

}
