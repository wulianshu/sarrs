package com.mylib.download;

import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadQueue{

	private final static int MinPoolSize = 1;
	@SuppressWarnings("unused")
	private static DownloadQueue mQueue;
	private DownloadManagerFactory.DownloadModule mModule;
	private DownloadExecutor mExecutor;
	private Hashtable<IDownload, DownloadTask> mRequestTaskMaps;
	private int mPoolSize = 1;
	
	@SuppressWarnings("unused")
	private DownloadQueue(){
		init();
	}
	
	
	public DownloadQueue(DownloadManagerFactory.DownloadModule module, int taskingSize) {
		this.mModule = module;
		this.mPoolSize = taskingSize < MinPoolSize ? MinPoolSize : taskingSize;
		printLog("[module="+ module +"]");
		init();
	}

	private void init() {
		int corePoolSize = mPoolSize;
		int maxPoolSize = mPoolSize;
		mRequestTaskMaps = new Hashtable<IDownload, DownloadTask>();
		mExecutor = new DownloadExecutor(mModule.getModule(), corePoolSize, maxPoolSize);
		mExecutor.setRejectedExecutionHandler(new TaskCallerRunsPolicy());
		mExecutor.setqCallback(new QueueCallback(){
			@Override
			public void qCallback(IDownload request) {
				if(request != null){
					removeRequest(request);
				}
			}
		});
	}
	

	/*public static synchronized DownloadQueue getQueue(){
		if(queue == null){
			queue = new DownloadQueue();
		}
		return queue;
	}*/
	
	public DownloadTask getDownloadTask(IDownload request, DownloadCallback callback) {
		DownloadTask task = new DownloadTask(request, callback);
		return task;
	}
	
	public void startDownload(IDownload request, DownloadCallback callback){
		
		DownloadTask task = getDownloadTask(request, callback);
		try {
			mExecutor.execute(task);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRequestTaskMaps.put(request, task);
	}
	
	public boolean pauseDownload(IDownload request){
		if(request != null){
			DownloadTask task = mRequestTaskMaps.get(request);
			mExecutor.pauseTask(task);
			removeRequest(request);
			if(task == null)
				return false;
			return true;
		}
		return false;
	}

	private void removeRequest(IDownload request) {
		mRequestTaskMaps.remove(request);
	}
	
	public void resumeDownload(IDownload request){
		
		
	}
	
	public void queryDownload(IDownload request){
		
		
	}
	
	private void printLog(String log){
		LogUtil.l(log);
	}
	
	public Set<String> clearQueue(boolean isShutDown){
		printLog("[clearQueue()  module=" + mModule + "]");
		mRequestTaskMaps.clear();
		return mExecutor.clearAllTask(isShutDown);
	}
	
	public Set<Runnable> getDownloadingTasks(){
		if(mExecutor == null)
			return null;
		return mExecutor.getDownloadingTasks();
	}
	
	public interface DownloadCallback {
		
		boolean onDownloading(IDownloadManager.Progress progress, long rate, int respondeCode, IDownload request);
		void onPauseDownload(IDownloadManager.Progress progress, int responseCode, IDownload request);
		void onDownloadFinish(IDownloadManager.Progress progress, int respondeCode, IDownload request);
		void onDownloadFailed(int responseCode, int errorCode, String errorMsg, IDownload request);
		void onFileTotalSize(IDownloadManager.Progress progress, int respondeCode, IDownload request);	
	}

	public interface QueueCallback{
		
		void qCallback(IDownload request);
		
	}
	
	
	
	public static class DownloadExecutor extends ThreadPoolExecutor {

		private Set<Runnable> downloadingVector = Collections.newSetFromMap(new ConcurrentHashMap<Runnable, Boolean>());
		private QueueCallback qCallback;
		
		@Override
		public void execute(Runnable command) {
			super.execute(command);
			printLog(" [execute] [ActiveCount=" + getActiveCount() + " ,TaskCount=" + getTaskCount()  + ", queueSize=" + getQueue().size() + " ]");
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			printLog(" [beforeExecute] [ActiveCount=" + getActiveCount() + " ,TaskCount=" + getTaskCount() + ", queueSize=" + getQueue().size() + " ]");
			downloadingVector.add(r);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			printLog(" [afterExecute] [ActiveCount=" + getActiveCount() + " ,TaskCount=" + getTaskCount() + ", queueSize=" + getQueue().size() + " ]");
			downloadingVector.remove(r);
			if(qCallback != null){
				DownloadTask task = (DownloadTask) r;
				qCallback.qCallback(task.getRequest());
			}
		}
		
		public void pauseTask(DownloadTask task){
			if(task != null){
				BlockingQueue<Runnable> queues = getQueue();
				if(queues.contains(task)){
					queues.remove(task);
				} else if(downloadingVector.contains(task)){
					//TODO The task is being downloaded
					downloadingVector.remove(task);
				}
				task.pauseTask();
			} else {
				printLog("[DownloadExecutor.pauseTask() task="+ task +"]");
			}
			printLog(" [pauseTask] [ActiveCount=" + getActiveCount() + " ,TaskCount=" + getTaskCount() + ", queueSize=" + getQueue().size() + " ]");
		}
		
		public Set<String> clearAllTask(boolean isShutDown) {
			Set<String> list = new HashSet<>();
			try {
				printLog("[clearAllTask  module="+ mModule +"]");
				getQueue().clear();
				for(Runnable r : downloadingVector){
					DownloadTask task = (DownloadTask)r;
					list.add(task.getRequest().getUrl());
					task.pauseTask();
				}
				downloadingVector.clear();
				if(isShutDown){
					shutdownNow();//TODO is now
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
		
		public QueueCallback getqCallback() {
			return qCallback;
		}

		public void setqCallback(QueueCallback qCallback) {
			this.qCallback = qCallback;
		}

		private void printLog(String log) {
			//logger.e(true, log);
		}

		private final static int defaultCorePoolSize = 1;
		private final static int defaultMaximumPoolSize = 1;
		private final static long defaultKeepAliveTime = 60;
		private final static TimeUnit defaultUnit = TimeUnit.SECONDS;
		
		private String mModule;
		
		public DownloadExecutor(String module){
			super(defaultCorePoolSize, defaultMaximumPoolSize, defaultKeepAliveTime, defaultUnit, new LinkedBlockingQueue<Runnable>());
			init(module);
		}

		public DownloadExecutor(String module, int corePoolSize, int maximumPoolSize){
			super(corePoolSize, maximumPoolSize, defaultKeepAliveTime, defaultUnit, new LinkedBlockingQueue<Runnable>());
			init(module);
		}

		public DownloadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		}

		public DownloadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		}

		public DownloadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}

		public DownloadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		protected void init(String module) {
			this.mModule = module;
			printLog("[module="+ module +"]");
		}
		
		public Set<Runnable> getDownloadingTasks(){
			return downloadingVector;
		}
	}
}
