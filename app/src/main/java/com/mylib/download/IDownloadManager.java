package com.mylib.download;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.mylib.download.DownloadManagerFactory.DownloadModule;

/**
 * 一个模块下载管理接口
 * 
 * @author luxu
 * 
 */
public interface IDownloadManager {

	/**
	 * 开启一个下载()
	 * 
	 * @param download
	 */
	public void startDownload(IDownload download);

	/**
	 * 暂停一个下载
	 * 
	 * @param download
	 * @return
	 */
	public IDownload pauseDownload(IDownload download);

	/**
	 * 暂停这个模块的所有下载，仍可接受下载请求
	 */
	public void pauseAll();

	/**
	 * 注册下载监听，一个DownloadManager可以对应多个监听
	 * 
	 * @param moduleKey
	 *            与 IDownloadListener 一一对应的模块key
	 * @param l
	 */
	public void registerDownloadListener(Class<?> moduleKey,
			IDownloadManager.IDownloadListener l);

	/**
	 * 取消注册下载监听
	 * 
	 * @param moduleKey
	 */
	public void unRegisterDownloadListener(Class<?> moduleKey);

	/**
	 * 暂停这个模块的所有下载, 并摧毁下载管理server。不能再请求下载任务
	 */
	public void destory();

	/**
	 * 正在下载的任务
	 * 
	 * @return
	 */
	public Set<Runnable> getDownloadingTasks();

	/**
	 * 下载监听
	 * 
	 * @author luxu
	 * 
	 */
	public interface IDownloadListener {

		public boolean onDownloading(IDownloadManager.DownloadInfo info);

		public void onPauseDownload(IDownloadManager.DownloadInfo info);

		public void onDownloadFinish(IDownloadManager.DownloadInfo info);

		public void onFileTotalSize(IDownloadManager.DownloadInfo info);

		public void onDownloadFailed(IDownloadManager.DownloadInfo info,
				IDownloadManager.DownloadExp exp);

	}

	public static class DownloadInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String url;//
		public IDownloadManager.Progress progress;

		// public Object[] param;
		public IDownload download;
		public long rate;
		public File file;// 下载时保存的文件

		public DownloadConstant.Status status;//
		public Object tag;
		public DownloadModule mModule;

		public DownloadInfo(DownloadModule module){
			mModule = module;
		}
		
		@Override
		public String toString() {
			StringBuffer buff = new StringBuffer("[url=");
			buff.append(url);
			buff.append("],");
			buff.append(progress);

			return buff.toString();
		}
	}

	public static class Progress implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public long progress;
		public long total;

		// public byte[] datas;

		@Override
		public String toString() {

			StringBuffer buff = new StringBuffer("[progress=");
			buff.append(progress);
			buff.append(", total=");
			buff.append(total);

			return buff.toString();
		}
	}

	public static class DownloadExp implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public final static int CODE_OK = 0;

		/**
		 * 网络异常-没有网络(wifi and 3g)
		 */
		public final static int CODE_NET = 11;

		/**
		 * 网络异常 - 服务器异常
		 */
		public final static int CODE_NET_SERVER = 12;

		/**
		 * 获取文件数据失败
		 */
		public final static int CODE_NET_FAILED = 13;

		/**
		 * sdcard没有存储空间
		 */
		public final static int CODE_NOSPACE = 20;

		/**
		 * 写文件异常
		 */
		public final static int CODE_WRITEFILE = 21;

		public int statusCode = CODE_OK;
		public int responseCode;
		public String errMsg;

		/**
		 * http头信息
		 */
		public Map<String, String> headers;

		public DownloadExp() {
			super();
		}

		public DownloadExp(int statusCode) {
			super();
			this.statusCode = statusCode;
		}

		public DownloadExp(int statusCode, int responseCode) {
			super();
			this.statusCode = statusCode;
			this.responseCode = responseCode;
		}

		@Override
		public String toString() {
			StringBuffer buff = new StringBuffer("[statusCode=");
			buff.append(statusCode);
			buff.append(", responseCode=");
			buff.append(responseCode);
			buff.append(", errMsg=");
			buff.append(errMsg);
			buff.append("]");
			return buff.toString();
		}

	}
}
