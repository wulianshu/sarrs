package com.mylib.download;

import java.io.File;

import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.mylib.download.DownloadConstant.DownloadType;
import com.mylib.download.DownloadManagerFactory.DownloadModule;
import com.mylib.download.RequestConstant.DataType;
import com.mylib.download.RequestConstant.HttpMode;
import com.mylib.download.RequestConstant.HttpType;
import com.mylib.download.RequestConstant.Priority;

/**
 * 一个下载
 * @author luxu
 *
 */
public interface IDownload extends IRequest {

	/**
	 * 下载类型
	 * @return
	 */
	public DownloadType getType();

	/**
	 * 断点下载开始位置
	 * @return
	 */
	public long getStartPosition();
	
	/**
	 * 文件总大小
	 * @return
	 */
	public long getTotalSize();
	
	/**
	 * 本地保存的文件路径
	 * @return
	 */
	public File getLoaclFile();
	
	public String getDownloadUrl();

	public Object getTag();

	public boolean addPublicParams();
	
	public DownloadModule getDownloadModule();
	
	public abstract class BaseDownload implements IDownload {

		@Override
		public Priority getPriority() {
			return Priority.NORMAL;
		}

		@Override
		public HttpType getHttpType() {
			return HttpType.HTTP;
		}

		@Override
		public DownloadType getType() {
			return DownloadType.FILE;
		}

		@Override
		public DataType getDataType(){
			return DataType.BYTE;
		}
		
		@Override
		public String getDownloadUrl() {
			String tag = getUrl();
			if(getHttpMode() == HttpMode.POST){
				tag += getPost();
			}
			return tag;
		}

		@Override
		public Object getTag() {
			return null;
		}

		@Override
		public boolean addPublicParams() {
			return true;
		}
	}
	
	public abstract class GetDownload extends BaseDownload {

		@Override
		public String getPost() {
			return "";
		}

		@Override
		public HttpMode getHttpMode() {
			return HttpMode.GET;
		}
		
	}
	
	public abstract class PostDownload extends BaseDownload {

		@Override
		public HttpMode getHttpMode() {
			return HttpMode.POST;
		}
		
	}

	public DownloadJob getJob();

	public void setJob(DownloadJob job);
}
