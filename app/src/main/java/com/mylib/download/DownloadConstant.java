package com.mylib.download;

import android.text.TextUtils;

public class DownloadConstant {

	/**
	 * 下载类型
	 * @author luxu
	 *
	 */
	public static enum DownloadType {
		/**
		 * 下载返回byte
		 */
		BYTE,
		/**
		 * 下载任务直接写文件
		 */
		FILE,
		/**
		 * 不支持断点续传的文件
		 */
		FILE_FROM_START;
	}

	public static enum Status {
		/**
		 * 还没请求下载
		 */
		UNSTART("UNSTART"),
		
		/**
		 *  等待网络回复  - 如下载前请求key
		 */
		WAIT("WAIT"), 
		/**
		 *  已请求开始下载
		 */
		PENDING("PENDING"), 
		/**
		 * 正在下载
		 */
		DOWNLOADING("DOWNLOADING"), 
		/**
		 * 暂停
		 */
		PAUSE("PAUSE"), 
		/**
		 * 继续
		 */
		RESUME("RESUME"),
		/**
		 * 下载完成
		 */
		FINISH("FINISH"), 
		/**
		 * 失败
		 */
		FAILED("FAILED");
		
		Status(String status){
			this.status = status;
		}
		
		public String getStatus(){
			return status;
		}
		public String status;
		
		public static Status convert(String status){
			if(!TextUtils.isEmpty(status)){
				return Enum.valueOf(Status.class, status);
			}
			return null;
		}
	}
	

}
