package com.chaojishipin.sarrs.download.download;

import android.util.Log;
import android.util.SparseArray;

/**
 * 支持mid转换成整形作为key和按某个媒体添加顺序作为key自增长
 * @author daipei
 *
 */
public class SparseArrayUtils {
	
	private final static boolean KEY = false; //1.true:表示用mid转成int 作为key 2.false：自增加作为key 
	
	public static void put(DownloadJob job,SparseArray<DownloadFolderJob> folderJobs){
		if(job == null || folderJobs == null)
			return;
		int index = indexOfMid(job.getEntity().getMid(), folderJobs);
		if(index == -1){
			int key = createKey(job.getEntity().getMid(), folderJobs);
			DownloadFolderJob folderJob = new DownloadFolderJob(job.getEntity().getFolderName(),job.getEntity().getMid(),key);
			folderJobs.append(key,folderJob);
			SparseArray<DownloadJob> jobs = new SparseArray<DownloadJob>();
			add(job,jobs);
			folderJob.setDownloadJobs(jobs);
		}else{
			DownloadFolderJob folderJob = folderJobs.valueAt(index);
			SparseArray<DownloadJob> jobs = folderJob.getDownloadJobs();
			add(job,jobs);
		}
		
	}

	private static void add(DownloadJob job,SparseArray<DownloadJob> jobs){
		if(job == null)
			return;
		if(jobs == null)
			jobs = new SparseArray<DownloadJob>();
		jobs.put(job.getIndex(), job);
	}

	public static void remove(DownloadJob job,SparseArray<DownloadFolderJob> folderJobs){
		if(job == null || folderJobs == null)
			return;
		int index = indexOfMid(job.getEntity().getMid(), folderJobs);
		if(index == -1){
		}else{
			DownloadFolderJob folderJob = folderJobs.valueAt(index);
			SparseArray<DownloadJob> jobs = folderJob.getDownloadJobs();
			jobs.remove(job.getIndex());
			if(jobs.size() == 0)
				folderJobs.valueAt(index);
		}
	}
	
	public static SparseArray<DownloadJob> find(int index,SparseArray<DownloadFolderJob> folderJobs){
		if(folderJobs == null)
			return null;
		DownloadFolderJob folderJob = folderJobs.valueAt(index);
		if(folderJob == null)
			return null;
		SparseArray<DownloadJob> jobs = folderJob.getDownloadJobs();
		return jobs;
		
	}
	
	public static void put(SparseArray<DownloadFolderJob> array,SparseArray<DownloadFolderJob> folderJobs){
		//TODO;
	}
	
	/**
	 * 查找对应mid的index值，若没有返回-1;有返回index
	 * @param mid
	 * @param folderJobs
	 * @return
	 */
	public static int indexOfMid(String mid,SparseArray<DownloadFolderJob> folderJobs){
		if(mid == null || folderJobs == null)
			throw new IllegalArgumentException("Argument can't be null");
		int size = folderJobs.size();
		for(int i =0;i<size;i++){
			//int key = folderJobs.keyAt(i);
			DownloadFolderJob folderJob = folderJobs.valueAt(i);
			if(folderJob!=null&&mid.equals(folderJob.getMediaId())){
				return i;
			}
				
		}
		return -1;
		
	}
	
	private static int keyOfMid(String mid,SparseArray<DownloadFolderJob> folderJobs){
		int key = -1;
		if(KEY){
			key = createKeyOfMid(mid);
		}else{
			key = keyByMid(mid,folderJobs);
		}
		return key;
	}

	private static int keyByMid(String mid,SparseArray<DownloadFolderJob> folderJobs){
		if(mid == null || folderJobs == null)
			throw new IllegalArgumentException("Argument can't be null");
		int size = folderJobs.size();
		for(int i =0;i<size;i++){
			//int key = folderJobs.keyAt(i);
			DownloadFolderJob folderJob = folderJobs.valueAt(i);
			if(folderJob!=null&&mid.equals(folderJob.getMediaId())){
				return folderJobs.keyAt(i);
			}
				
		}
		return -1;		
	}
	
	/**
	 * create the key mapping mid;
	 * @param mid
	 * @param folderJobs
	 * @return
	 */
	private static int createKey(String mid,SparseArray<DownloadFolderJob> folderJobs){
		//String midNum = DownloadHelper.changeHashidToNumStr(mid);
		int key = -1;
		if(KEY){
			key = createKeyOfMid(mid);
		}else{
			key = CreateKeyIncre(folderJobs);
		}
		return key;
	}
	
	private static int createKeyOfMid(String mid){
		//String midNum = DownloadHelper.changeHashidToNumStr(mid);
		int key = -1;
		try{
			key = Integer.valueOf(mid);
		}catch(NumberFormatException e){
			key = -1;
		}
		return key;
	}
	
	/**
	 * create key by size-1 对应的index 加1;因为indexs 是有序的
	 * @param folderJobs
	 * @return
	 */
	private static int CreateKeyIncre(SparseArray<DownloadFolderJob> folderJobs){
		if(folderJobs == null)
			return -1;
		int size = folderJobs.size();
		if(size == 0 )
			return size;
		int key = folderJobs.keyAt(size-1);
		return ++key;
	}
	
	public static void buildData(){
		SparseArray<String> strs = new SparseArray<String>();
		strs.put(0, "0");
		strs.put(1, "1");
		strs.put(5, "5");
		strs.put(4, "4");
		strs.append(8, "8");
		strs.put(9, "9");
		
		print(strs);
		
		strs.remove(5);
		
		print(strs);
		
		strs.append(7, "7");
		strs.append(6, "6");
		
		print(strs);
	}
	
	private static void print(SparseArray<String> strs){
		for(int i=0;i<strs.size();i++){
			Log.i("XJ", "key = " + strs.keyAt(i) + ",value = " + strs.valueAt(i));
		}
	}

}
