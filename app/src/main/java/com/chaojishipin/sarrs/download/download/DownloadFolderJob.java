package com.chaojishipin.sarrs.download.download;

import android.util.SparseArray;

import java.text.DecimalFormat;

public class DownloadFolderJob {
	private String mTotal;
	private String mMediaName;
	private String mMediaId;
	private int index;//文件夹索引，key值
	private SparseArray<DownloadJob> mDownloadJobs;
	
	public DownloadFolderJob() {
	}
	
	public DownloadFolderJob(String mMediaName, String mMediaId, int index) {
		this.mMediaName = mMediaName;
		this.mMediaId = mMediaId;
		this.index = index;
	}
	
	public String getTotal() {
		if(mDownloadJobs!=null){
			double totalSize = 0;
			DecimalFormat df = new DecimalFormat();
			df.setMinimumFractionDigits(2);
			df.setMaximumFractionDigits(2);
			for(int i =0;i<mDownloadJobs.size();i++){
				totalSize += mDownloadJobs.valueAt(i).getTotalSize();
			}
			totalSize = totalSize/1024/1024/1024;
			if(totalSize >= 1) {
				mTotal = df.format(totalSize) + "G";
			} else {
				mTotal = (int)(totalSize * 1024) + "MB";
			}
		}
		return mTotal;
	}
	public String getMediaName() {
		return mMediaName;
	}
	public void setMediaName(String mMediaName) {
		this.mMediaName = mMediaName;
	}
	public SparseArray<DownloadJob> getDownloadJobs() {
		return mDownloadJobs;
	}
	public void setDownloadJobs(SparseArray<DownloadJob> mDownloadJobs) {
		this.mDownloadJobs = mDownloadJobs;
	}
	public String getMediaId() {
		return mMediaId;
	}
	public void setMediaId(String mMediaId) {
		this.mMediaId = mMediaId;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

}
