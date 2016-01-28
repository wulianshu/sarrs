package com.mylib.download;

import com.mylib.download.DownloadManagerFactory.DownloadModule;

public interface IDownloadHandle {

	public void startDownload(DownloadModule downloadModule, Download download,
			Object... params);

	public void pauseDownload(DownloadModule downloadModule, Download download,
			Object... params);

	public void resumeDownload(DownloadModule downloadModule,
			Download download, Object... params);
	// public Download queryDownload(DangDang_Method action, Object... params);

}
