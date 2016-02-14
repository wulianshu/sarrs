package com.mylib.download;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chaojishipin.sarrs.download.download.Constants;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.mylib.download.DownloadConstant.Status;

/**
 * 书架全本下载管理类
 * @author xiaruri
 *
 */
public class VideoDownloadManager {

	private String TAG = "VideoDownloadManager";
	private Context mContext;
	private DownloadManagerFactory.DownloadModule module = new DownloadManagerFactory.DownloadModule("shelf");
	private IDownloadManager mDownloadManager;
	private Class<?> modleKey = VideoDownloadManager.class;
	private ConcurrentHashMap<String, VideoDownload> mMap = new ConcurrentHashMap<String, VideoDownload>();
	private Set<IShelfDownloadListener> mStatusListener = Collections.newSetFromMap(new ConcurrentHashMap<IShelfDownloadListener, Boolean>());

	public VideoDownloadManager(Context context){
		initDownload();
		context = context.getApplicationContext();
		mContext = context;
	}
	
	protected void initDownload() {
		module.setTaskingSize(3);
		mDownloadManager = DownloadManagerFactory.getFactory().create(module);
		mDownloadManager.registerDownloadListener(modleKey, downloadListener);
	}
	
	public DownloadManagerFactory.DownloadModule getModule(){
		return module;
	}
	
	public interface IShelfDownloadListener {

		public void onDownloading(VideoDownload download);

		public void onPauseDownload(VideoDownload download);

		public void onDownloadFinish(VideoDownload download);

		public void onFileTotalSize(VideoDownload download);

		public void onDownloadFailed(VideoDownload download, String msg);

		public void onDownloadPending(VideoDownload download);
	}
	
	final IDownloadManager.IDownloadListener downloadListener;

	{
		downloadListener = new IDownloadManager.IDownloadListener() {

			@Override
			public boolean onDownloading(IDownloadManager.DownloadInfo info) {
				LogUtil.l("onDownloading info = " + info.toString());
				VideoDownload download = mMap.get(info.url);
				LogUtil.l("onDownloading download = " + download);
				if (download != null) {
					download.getJob().setDownloadedSize(info.progress.progress);
					download.setProgress(info.progress.progress);
					download.setStatus(DownloadConstant.Status.DOWNLOADING);
					updateStatus(download, info.rate);
					Iterator<IShelfDownloadListener> ite = mStatusListener.iterator();
					while (ite.hasNext()) {
						IShelfDownloadListener l = ite.next();
						if (l != null)
							l.onDownloading(download);
					}
					return true;
				}else{
//					pauseDownload();
				}
				return false;
			}

			@Override
			public void onPauseDownload(IDownloadManager.DownloadInfo info) {
				LogUtil.l("onPauseDownload info = " + info.toString());
				VideoDownload download = changeMap(false, null, info, "pause");
				if (download != null) {
					download.setStatus(DownloadConstant.Status.PAUSE);
					updateStatus(download, 0);
					Iterator<IShelfDownloadListener> ite = mStatusListener.iterator();
					while (ite.hasNext()) {
						IShelfDownloadListener l = ite.next();
						if (l != null)
							l.onPauseDownload(download);
					}
				}
			}

			@Override
			public void onDownloadFinish(IDownloadManager.DownloadInfo info) {
				LogUtil.l("onDownloadFinish info = " + info.toString());
				VideoDownload download = changeMap(false, null, info, "finish");
				if (download != null) {
					DataUtils.getInstance().updateDownloadStatus(download.getJob(), DownloadJob.COMPLETE);
					download.setStatus(DownloadConstant.Status.FINISH);
					updateStatus(download, 0);
					Iterator<IShelfDownloadListener> ite = mStatusListener.iterator();
					while (ite.hasNext()) {
						IShelfDownloadListener l = ite.next();
						if (l != null)
							l.onDownloadFinish(download);
					}
					// 下载完成广播
					DownloadJob job = download.getJob();
					if (job == null)
						return;
					Intent intent = new Intent(Constants.MESSAGE_DOWNLOAD_FINISH);
					intent.putExtra("id", job.getEntity().getId());
					mContext.sendBroadcast(intent);
				}
			}

			@Override
			public void onFileTotalSize(IDownloadManager.DownloadInfo info) {
				LogUtil.l("onFileTotalSize size = " + info.progress.total);
				VideoDownload download = mMap.get(info.url);
				if (download != null) {
					download.getJob().getEntity().setFileSize(info.progress.total);
					download.getJob().setTotalSize(info.progress.total);
					download.setTotalSize(info.progress.total);
					updateStatus(download, 0);
					Iterator<IShelfDownloadListener> ite = mStatusListener.iterator();
					while (ite.hasNext()) {
						IShelfDownloadListener l = ite.next();
						if (l != null)
							l.onFileTotalSize(download);
					}
				}
			}

			@Override
			public void onDownloadFailed(IDownloadManager.DownloadInfo info, IDownloadManager.DownloadExp exp) {
				final VideoDownload download = changeMap(false, null, info, "fail");
				if (download != null) {
					initDownloadExpCode("", download);
					download.setStatus(Status.FAILED);
					updateStatus(download, 0);
					Iterator<IShelfDownloadListener> ite = mStatusListener.iterator();
					while (ite.hasNext()) {
						IShelfDownloadListener l = ite.next();
						if (l != null) {
							l.onDownloadFailed(download, "");
						}
					}
				}
			}
		};
	}

	private void writeError(String error){
		try{
			if(TextUtils.isEmpty(error))
				return;
			
			String path = Environment.getExternalStorageDirectory() + "/ddLog";
			File f = new File(path);
			if(f.exists() && f.isFile()){
				File tmp = new File(path + "a");			
				f.renameTo(tmp);
				tmp.delete();
			}
			f.mkdirs();
			f = new File(f, System.currentTimeMillis() + "");
			FileOutputStream s = new FileOutputStream(f);
			s.write(error.getBytes());
			s.flush();
			s.close();
		}catch(Throwable e){
			e.printStackTrace();
		}		
	}

	private int convertStatus(Status s){
		switch(s){
			case DOWNLOADING:
			case PENDING:
			case RESUME:
			case WAIT:
				return DownloadJob.DOWNLOADING;

			case FAILED:
				return DownloadJob.FAIL;

			case PAUSE:
			case UNSTART:
				return DownloadJob.PAUSE;

			case FINISH:
				return DownloadJob.COMPLETE;
		}
		return DownloadJob.INIT;
	}

	private void updateStatus(VideoDownload download, long rate){
		download.getJob().setRate(rate);
		if(!download.isStatusChanged())
			return;
		download.getJob().setStatus(convertStatus(download.getStatus()));
	}
	
	private void notifyPending(VideoDownload download){
		Iterator<IShelfDownloadListener> ite = mStatusListener.iterator();				
        while (ite.hasNext()){
        	IShelfDownloadListener l = ite.next();
        	if(l != null)
        		l.onDownloadPending(download);
        }
	}

	public void startDownload(VideoDownload download){
		if(mMap.containsKey(download.getUrl())) {
			;
		}else
			download(download);
	}

	public void pauseAll(){
		mDownloadManager.pauseAll();
	}

	public void pauseDownload(VideoDownload download){
		if(mMap.containsKey(download.getUrl())){
			mDownloadManager.pauseDownload(download);
		}
	}

	public void download(VideoDownload download){
//		LogUtil.l("download bookId = " + download.getBookId());
		if(mMap.containsKey(download.getUrl())){
			download = mMap.get(download.getUrl());
			switch(download.getStatus()){
			case DOWNLOADING:
			case PENDING:
			case RESUME:	
			case WAIT:
				mDownloadManager.pauseDownload(download);
				break;
				
			case FAILED:
			case PAUSE:
			case UNSTART:
				notifyPending(download);
				mDownloadManager.startDownload(download);
				break;
			
			case FINISH:
				break;
				
			default:
				break;
			}
		} else {
			download.setStatus(Status.WAIT);
			download.getJob().setStatus(DownloadJob.WAITING);
			LogUtil.l("download download.getUrl() = " + download.getUrl());
			changeMap(true, download, null, "add");
			notifyPending(download);
			mDownloadManager.startDownload(download);
		}
	}

	private VideoDownload changeMap(boolean add, VideoDownload download, IDownloadManager.DownloadInfo info, String action){
		if(add) {
			mMap.put(download.getUrl(), download);
			Log.e("mapp", "new download " + download.getUrl());
			return download;
		}else {
			VideoDownload down = mMap.remove(info.url);
			if(down == null)
				Log.e("mapp", "remove download " + info.url + ", " + action + ", fail");
			else
				Log.e("mapp", "remove download " + info.url + ", " + action);
			return down;
		}
	}

	public void removeDownloadListener(IShelfDownloadListener l){
		if(l != null)
			mStatusListener.remove(l);
	}
	
	public void addDownloadListener(IShelfDownloadListener l){
		if(l != null)
			mStatusListener.add(l);
	}
	
	private void initDownloadExpCode(String json, VideoDownload download)
			throws JSONException {
		
		LogUtil.l("initDownloadExpCode");
		
		if(TextUtils.isEmpty(json) || download == null){
			return;
		}
		
		JSONObject jsonO = JSON.parseObject(json);
		JSONObject statusJson = jsonO.getJSONObject("status");
		if (statusJson == null) {
			return;
		}
		String statusCode = statusJson.getString("code");
		if ("0".equals(statusCode)) {
		} else {
//			ResultExpCode expCode = new ResultExpCode();
//			expCode.errorCode = statusJson.getString("code");
//			expCode.errorMessage = statusJson.getString("message");
//			download.setExpCode(expCode);
		}
	}

	public int getDownloadingJobNum(){
		return mMap.size();
	}

	public ArrayList<DownloadJob> getAllDownloadingJobs(){
		ArrayList<DownloadJob> list = new ArrayList<>();
		if(mMap == null)
			return list;
		Iterator<Map.Entry<String, VideoDownload>> it = mMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, VideoDownload> e = it.next();
			list.add(e.getValue().getJob());
		}
		return list;
	}


}
