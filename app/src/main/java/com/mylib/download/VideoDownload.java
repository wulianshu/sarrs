package com.mylib.download;

import java.io.File;

import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.mylib.download.DownloadConstant.Status;

public class VideoDownload extends IDownload.GetDownload {
	
	private long mTotalSize;
	private long mProgress;
	private String mUrl;
	private File mLocalFile;
	private DownloadConstant.Status mStatus;
	private DownloadJob mJob;
	private boolean mStatusChange = false;
	private DownloadManagerFactory.DownloadModule mModule;
	
	public VideoDownload(DownloadManagerFactory.DownloadModule module, DownloadJob job){
		mModule = module;
		this.setParams(job);
	}

	@Override
	public void setJob(DownloadJob job){
		mJob = job;
	}

	@Override
	public DownloadJob getJob(){
		return mJob;
	}

	@Override
	public long getStartPosition() {
		long mStartPos;
		if(mLocalFile.exists() && mLocalFile.isFile())
			mStartPos = mLocalFile.length();
		else
			mStartPos = 0;
		
		if(mStartPos > mTotalSize)
			mStartPos = 0;
		return mStartPos;
	}

	@Override
	public long getTotalSize() {
		return mTotalSize;
	}

	@Override
	public File getLoaclFile() {
		return mLocalFile;
	}

	@Override
	public String getUrl() {
		return mUrl;
	}

	public Status getStatus(){
		return mStatus;
	}
	
	public void setStatus(Status status){
		if(mStatus != status)
			mStatusChange = true;
		else
			mStatusChange = false;
		
		mStatus = status;
	}
	
	public boolean isStatusChanged(){
		return this.mStatusChange;
	}
	
	public void setTotalSize(long size){
		if(mTotalSize != size)
			mStatusChange = true;
		mTotalSize = size;
	}

	public void setProgress(long p){
		mProgress = p;
	}
	
	public long getProgress(){
		return mProgress;
	}

	private void setParams(DownloadJob job){
		setJob(job);
		setTotalSize(job.getEntity().getFileSize());
		mUrl = job.getEntity().getId();
		mLocalFile = DataUtils.getLocalFile(job);
	}

	public String getJobId(){
		return mJob.getEntity().getId();
	}

//	public ResultExpCode getExpCode() {
//		return expCode;
//	}
//
//	public void setExpCode(ResultExpCode expCode) {
//		this.expCode = expCode;
//	}

	@Override
	public DownloadManagerFactory.DownloadModule getDownloadModule() {
		// TODO Auto-generated method stub
		return mModule;
	}
	
	@Override
	public boolean addPublicParams() {
		return true;
	}
}
