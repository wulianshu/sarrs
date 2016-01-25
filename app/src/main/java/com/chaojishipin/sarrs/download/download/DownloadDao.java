package com.chaojishipin.sarrs.download.download;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * 数据库增删改查
 * @author xiongjin
 *
 */
public interface DownloadDao {
	
	public boolean add(DownloadEntity entry);

	public void setStatus(DownloadEntity entity, int status); 	
	
	public void setIfWatch(DownloadEntity entity, String ifwatch);
	
	public SparseArray<DownloadFolderJob> getAllDownloadFloderJobs();
	
	public ArrayList<DownloadJob> getAllDownloadJobs();
	
	public void remove(DownloadJob job);
	
	public ArrayList<DownloadJob> getDownloadJobsByMid(String mid);

	public boolean selectDownloadJobByMid(String mid);

	public boolean updateValue(DownloadEntity entity, String key, String newValue);
	public boolean updateValue(DownloadEntity entity, String key, int newValue);

	public boolean isDownloaded(String id);
}
