package com.mylib.download;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.mylib.download.DownloadQueue.DownloadCallback;

public class DownloadManager implements IDownloadManager {

	public final static int DefaultPoolSize = 1;
	// private static DownloadManager manager;
	protected DownloadManagerFactory.DownloadModule mModule;
	private DownloadQueue mDownQueue;
	/**
	 * key: url value: download task
	 */
	private Map<String, IDownload> mDownloadMaps;
	private Map<Class<?>, IDownloadManager.IDownloadListener> mListeners;

	@SuppressWarnings("unused")
	private DownloadManager() {
		init(DefaultPoolSize);
	}

	public DownloadManager(DownloadManagerFactory.DownloadModule module) {
		initModule(module);
		init(module.getTaskingSize());
	}

	protected void initModule(DownloadManagerFactory.DownloadModule module) {
		this.mModule = module;
		printLog("[module=" + module + "]");
	}

	private void init(final int taskingSize) {
		mDownloadMaps = new ConcurrentHashMap<>();
		mListeners = new ConcurrentHashMap<Class<?>, IDownloadManager.IDownloadListener>();
		mDownQueue = initQueue(taskingSize);
	}

	public DownloadQueue initQueue(int taskingSize) {
		return new DownloadQueue(mModule, taskingSize);
	}

	@Override
	public void startDownload(IDownload download) {

		final String url = download.getDownloadUrl();
		if (!NetWorkUtils.isNetAvailable()) {// check net ?
			IDownloadManager.DownloadExp exp = new IDownloadManager.DownloadExp(
					IDownloadManager.DownloadExp.CODE_NET);
			IDownloadManager.DownloadInfo info = new IDownloadManager.DownloadInfo(download.getDownloadModule());
			info.status = DownloadConstant.Status.FAILED;
			// info.param = download.getParams();
			info.download = download;

			Collection<IDownloadManager.IDownloadListener> gls = mListeners
					.values();
			for (IDownloadManager.IDownloadListener l : gls) {
				l.onDownloadFailed(info, exp);
			}
		}

		if (mDownloadMaps.containsKey(url)) {
			printLog(" [downloadMaps.containsValue(download) == true]");
		}else{
			putDownloadAndRequest(download);
			startDownloadInner(download);// add request to DownloadQueue
		}
	}

	public IDownload pauseDownload(IDownload d) {

		IDownload download = d;
		IDownload oldDownload = mDownloadMaps.get(download.getUrl());

		if (oldDownload != null) {
			if(!mDownQueue.pauseDownload(oldDownload)){
				IDownloadManager.DownloadInfo info = new IDownloadManager.DownloadInfo(d.getDownloadModule());
				info.download = download;
				info.url = download.getUrl();
				info.tag = download.getTag();
				info.status = DownloadConstant.Status.PAUSE;
				mDownloadMaps.remove(download.getUrl());
				Collection<IDownloadManager.IDownloadListener> gls = mListeners
						.values();
				for (IDownloadManager.IDownloadListener l : gls) {
					l.onPauseDownload(info);
				}
			}
		} else {
			IDownloadManager.DownloadInfo info = new IDownloadManager.DownloadInfo(d.getDownloadModule());
			// info.param = download.getParams();
			info.download = download;
			info.url = download.getUrl();
			info.tag = download.getTag();
			info.status = DownloadConstant.Status.FAILED;
			IDownloadManager.DownloadExp exp = new IDownloadManager.DownloadExp();

			Collection<IDownloadManager.IDownloadListener> gls = mListeners
					.values();
			for (IDownloadManager.IDownloadListener l : gls) {
				l.onDownloadFailed(info, exp);
			}
			printLog("[pauseDownload: request=" + download + "]");
		}
		return download;
	}

	public void resumeDownload(IDownload download) {

		startDownload(download);

	}

	@SuppressWarnings("unused")
	private void reStartDownload(IDownload download) {
		startDownload(download);
	}

	/*
	 * public Download queryDownload(Download download) {
	 * 
	 * 
	 * return null; }
	 */

	public void pauseAll() {
		printLog("[pauseAll()  module=" + mModule + "]");
		// mDownloadMaps.clear();
		// mRequestMaps.clear();

		Set<String> set = mDownQueue.clearQueue(false);

		Iterator<Map.Entry<String, IDownload>> it = mDownloadMaps.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, IDownload> entry = it.next();
			if(set.contains(entry.getKey()))
				continue;
			IDownload download = entry.getValue();
			IDownloadManager.DownloadInfo info = new IDownloadManager.DownloadInfo(download.getDownloadModule());
			info.download = download;
			info.url = download.getUrl();
			info.tag = download.getTag();
			info.status = DownloadConstant.Status.PAUSE;
			Collection<IDownloadManager.IDownloadListener> gls = mListeners
					.values();
			for (IDownloadManager.IDownloadListener l : gls) {
				l.onPauseDownload(info);
			}
			it.remove();
		}
	}

	public void destory() {
		printLog("[clear()  module=" + mModule + "]");
		pauseAll();
		mDownQueue.clearQueue(true);
	}

	private void putDownloadAndRequest(IDownload download) {

		final String url = download.getDownloadUrl();
		mDownloadMaps.put(url, download);
		// mRequestMaps.put(url, request);
	}

	private IDownload removeDownloadAndRequest(String url) {

		IDownload download = mDownloadMaps.remove(url);// downloadMaps.get(url);
		// mRequestMaps.remove(url);

		return download;
	}

	private void startDownloadInner(IDownload request) {

		// TODO
		final DownloadCallback callback = new DownloadCallback() {

			IDownloadManager.DownloadInfo mInfo;
			boolean first = true;

			@Override
			public boolean onDownloading(IDownloadManager.Progress progress, long rate,
					int respondeCode, IDownload request) {

				final String url = request.getUrl();
				final IDownload download = mDownloadMaps.get(url);
				if (download != null) {
					if (first) {
						printLog("onDownloading[progress=" + progress
								+ ", respondeCode=" + respondeCode + "]");
						first = false;
					}
					IDownloadManager.DownloadInfo info = null;
					info = setDownloadInfo(progress, download,
							DownloadConstant.Status.DOWNLOADING);

					Collection<IDownloadManager.IDownloadListener> gls = mListeners
							.values();
					for (IDownloadManager.IDownloadListener l : gls) {
						info.rate = rate;
						if(!l.onDownloading(info)){
							mDownQueue.pauseDownload(request);
							return false;
						}
					}
					return true;
				} else {
					printLog("[ onDownloading download==null ]url: " + url);
					mDownQueue.pauseDownload(request);
					return false;
				}
			}

			@Override
			public void onPauseDownload(IDownloadManager.Progress progress,
					int responseCode, IDownload request) {

				final String url = request.getUrl();
				final IDownload download = removeDownloadAndRequest(url);
				if (download != null) {

					printLog("onPauseDownload[progress=" + progress
							+ ", respondeCode=" + responseCode + "]");

					IDownloadManager.DownloadInfo info = null;
					info = setDownloadInfo(progress, download,
							DownloadConstant.Status.PAUSE);

					Collection<IDownloadManager.IDownloadListener> gls = mListeners
							.values();
					printLog("[listeners.size()=" + mListeners.size() + "]");
					for (IDownloadManager.IDownloadListener l : gls) {
						l.onPauseDownload(info);
					}
					/*
					 * for(IDownloadListener l : globalListeners){
					 * l.onPauseDownload(info); }
					 */
				} else {
					printLog("[ onPauseDownload download==null ]url: " + url);
				}
			}

			@Override
			public void onDownloadFinish(IDownloadManager.Progress progress,
					int responseCode, IDownload request) {

				final String url = request.getUrl();
				final IDownload download = removeDownloadAndRequest(request
						.getUrl());

				if (download != null) {
					printLog("onDownloadFinish[progress=" + progress
							+ ", respondeCode=" + responseCode + "]");

					IDownloadManager.DownloadInfo info = null;
					info = setDownloadInfo(progress, download,
							DownloadConstant.Status.FINISH);

					Collection<IDownloadManager.IDownloadListener> gls = mListeners
							.values();
					for (IDownloadManager.IDownloadListener l : gls) {
						l.onDownloadFinish(info);
					}
					/*
					 * for(IDownloadListener l : globalListeners){
					 * l.onDownloadFinish(info); }
					 */
				} else {
					printLog("[ onDownloadFinish download==null ]url: " + url);
				}
			}

			@Override
			public void onDownloadFailed(int responseCode, int errorCode, String errorMsg, 
					IDownload request) {

				final String url = request.getUrl();
				final IDownload download = removeDownloadAndRequest(url);
				if (download != null) {
					printLog("onDownloadFailed[respondeCode=" + responseCode
							+ ", request=" + request + "]");

					IDownloadManager.DownloadInfo info = null;
					info = setDownloadInfo(null, download,
							DownloadConstant.Status.FAILED);

					IDownloadManager.DownloadExp exp = new IDownloadManager.DownloadExp();
					exp.responseCode = responseCode;
					exp.statusCode = errorCode;
					exp.errMsg = errorMsg;

					Collection<IDownloadManager.IDownloadListener> gls = mListeners
							.values();
					for (IDownloadManager.IDownloadListener l : gls) {
						l.onDownloadFailed(info, exp);
					}

				} else {
					printLog("[ onDownloadFailed download==null ]url: " + url);
				}
			}

			private IDownloadManager.DownloadInfo setDownloadInfo(
					IDownloadManager.Progress progress,
					final IDownload download, DownloadConstant.Status status) {

				IDownloadManager.DownloadInfo info;
				if (mInfo == null) {
					mInfo = new IDownloadManager.DownloadInfo(download.getDownloadModule());
				}
				info = mInfo;
				// info.param = download.getParams();
				info.download = download;
				info.progress = progress;
				info.url = download.getUrl();
				info.tag = download.getTag();
				info.file = download.getLoaclFile();
				info.status = status;
				return info;
			}

			@Override
			public void onFileTotalSize(IDownloadManager.Progress progress,
					int respondeCode, IDownload request) {

				final String url = request.getUrl();
				final IDownload download = mDownloadMaps.get(url);
				if (download != null) {
					printLog("onFileTotalSize[respondeCode=" + respondeCode
							+ ", request=" + request + "]");

					IDownloadManager.DownloadInfo info = null;
					info = setDownloadInfo(progress, download,
							DownloadConstant.Status.DOWNLOADING);

					Collection<IDownloadManager.IDownloadListener> gls = mListeners
							.values();
					for (IDownloadManager.IDownloadListener l : gls) {
						l.onFileTotalSize(info);
					}
					/*
					 * for(IDownloadListener l : globalListeners){
					 * l.onFileTotalSize(info); }
					 */
				}
			}
		};

		mDownQueue.startDownload(request, callback);
	}

	/*
	 * public void registerDownloadListener(IDownloadListener l){ if(l == null){
	 * throw new NullPointerException("[IDownloadListener not null]"); }
	 * if(!globalListeners.contains(l)){ globalListeners.add(l); } }
	 * 
	 * public void unRegisterDownloadListener(IDownloadListener l){ if(l != null
	 * && globalListeners.contains(l)){ globalListeners.remove(l); } }
	 */

	/**
	 * moduleKey与IDownloadListener 一一对应
	 * 
	 * @param moduleKey
	 * @param l
	 */
	public void registerDownloadListener(Class<?> moduleKey,
			IDownloadManager.IDownloadListener l) {
		if (moduleKey == null || l == null) {
			throw new NullPointerException("[IDownloadListener not null]");
		}
		mListeners.put(moduleKey, l);
	}

	public void unRegisterDownloadListener(Class<?> moduleKey) {
		if (moduleKey != null) {
			mListeners.remove(moduleKey);
		}
	}

	public IDownloadManager.IDownloadListener getDownloadListener(
			Class<?> moduleKey) {
		return mListeners == null ? null : mListeners.get(moduleKey);
	}

	public int getListenerSize() {
		return mListeners.size();
	}

	private void printLog(String log) {
		LogUtil.l(log);
	}

	@Override
	public Set<Runnable> getDownloadingTasks() {
		return mDownQueue.getDownloadingTasks();
	}
}
