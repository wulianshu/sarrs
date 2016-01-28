package com.mylib.download;

import java.io.File;
import java.io.Serializable;

import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.mylib.download.DownloadManagerFactory.DownloadModule;
import com.mylib.download.IDownload.GetDownload;

public class Download extends GetDownload implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Download(){
	}

	@Override
	public String getUrl() {
		return null;
	}

	@Override
	public long getStartPosition() {
		return 0;
	}

	@Override
	public long getTotalSize() {
		return 0;
	}

	@Override
	public File getLoaclFile() {
		return null;
	}

	@Override
	public DownloadModule getDownloadModule() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setJob(DownloadJob job){

	}

	public DownloadJob getJob(){
		return null;
	}
}
