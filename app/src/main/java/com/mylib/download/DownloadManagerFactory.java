package com.mylib.download;

import com.chaojishipin.sarrs.utils.LogUtil;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DownloadManagerFactory {

	private static DownloadManagerFactory mFactory;
	/**
	 * key: module DownloadModule value: DownloadManager
	 */
	private Map<DownloadManagerFactory.DownloadModule, IDownloadManager> mCache = new Hashtable<DownloadModule, IDownloadManager>();;
	private Object mLock = new Object();

	private DownloadManagerFactory() {
	}

	public DownloadManagerFactory(int value) {

	}

	public static synchronized DownloadManagerFactory getFactory() {
		if (mFactory == null) {
			mFactory = new DownloadManagerFactory();
		}
		LogUtil.l(" getFactory()=" + mFactory);
		return mFactory;
	}

	public IDownloadManager create(DownloadModule module) {
		if (module == null) {
			throw new NullPointerException(" module is null ");
		}
		synchronized (mLock) {
			IDownloadManager current = null;
			if (mCache.containsKey(module)) {
				current = mCache.get(module);
				if (current == null) {
					current = createDownloadManager(module);
				}
			} else {
				current = createDownloadManager(module);
			}
			printLog("[create() module=" + module + ", current=" + current
					+ "]");
			return current;
		}
	}

	public IDownloadManager getDownloadManager(DownloadModule module) {
		if (module == null) {
			throw new NullPointerException(" module is null ");
		}
		synchronized (mLock) {
			IDownloadManager dm = mCache.get(module);
			if (dm == null) {
				printLogE(" getDownloadManager dm == null " + module);
			}
			return dm;
		}
	}

	private IDownloadManager createDownloadManager(DownloadModule module) {
		DownloadManager current = newDownloadManager(module);
		mCache.put(module, current);
		return current;
	}

	protected DownloadManager newDownloadManager(DownloadModule module) {
		return new DownloadManager(module);
	}

	public void destroyModule(DownloadModule module) {
		synchronized (mLock) {
			try {
				if (!mCache.containsKey(module))
					return;
				mCache.get(module).destory();
				mCache.remove(module);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void destory() {
		try {
			loopDownloadManager(Operation.DESTORY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mCache != null)
			mCache.clear();
		mFactory = null;
	}

	public void pauseAll() {
		try {
			loopDownloadManager(Operation.PAUSE_ALL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loopDownloadManager(Operation opern) {
		Set<Entry<DownloadModule, IDownloadManager>> entrys = mCache.entrySet();
		if (entrys.isEmpty()) {
			return;
		}
		Iterator<Entry<DownloadModule, IDownloadManager>> iterts = entrys
				.iterator();
		Entry<DownloadModule, IDownloadManager> element = null;
		IDownloadManager manager;
		while (iterts.hasNext()) {
			element = iterts.next();
			manager = element.getValue();
			operationDManager(manager, opern);
		}
	}

	private void operationDManager(IDownloadManager manager, Operation opern) {
		if (manager != null) {
			if (opern == Operation.DESTORY) {
				manager.destory();
			} else if (opern == Operation.PAUSE_ALL) {
				manager.pauseAll();
			}
		}
	}

	private void printLog(String log) {
		LogUtil.l(log);
	}

	private void printLogE(String log) {
		LogUtil.l(log);
	}

	public enum Operation {
		DESTORY, PAUSE_ALL
	}

	public static class DownloadModule implements Serializable {

		private String mModule;
		/**
		 * 同时下载的任务数
		 */
		private int mTaskingSize = 1;

		public DownloadModule(String module) {
			this.mModule = module;
		}

		public String getModule() {
			return mModule;
		}

		public int getTaskingSize() {
			return mTaskingSize;
		}

		public void setTaskingSize(int taskingSize) {
			this.mTaskingSize = taskingSize;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			DownloadModule that = (DownloadModule) o;

			if (mModule != null ? !mModule.equals(that.mModule)
					: that.mModule != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return mModule != null ? mModule.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "[" + getClass().getSimpleName() + "(" + getModule() + ")-("
					+ getTaskingSize() + "]";
		}

	}
}
