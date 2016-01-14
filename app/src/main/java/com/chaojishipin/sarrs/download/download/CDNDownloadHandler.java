package com.chaojishipin.sarrs.download.download;

import android.util.Log;

import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

public class CDNDownloadHandler implements DownloadHandler {

	@Override
	public int downloadFile(DownloadJob job) throws IOException {
		
		DownloadEntity downloadEntity = job.getEntity();
		String path = job.getDestination();
        String fileName = downloadEntity.getSaveName()+".mp4";
        new DownloadManager().deleteEmptyFile(path+"/"+downloadEntity.getSaveName());
		//构建下载目录
		try{
			File file = new File(path);
			if (!file.exists())
				file.mkdirs();   
		}catch (Exception e){
			e.printStackTrace();
			job.setExceptionType(DownloadJob.FILE_NOT_FOUND);
			return DOWNLOAD_FILE_ERROR;
		}
		
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params,
				50 * 1000);
		HttpConnectionParams.setSoTimeout(params, 50 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192 * 5);
		HttpGet httpGet = new HttpGet(downloadEntity.getDownloadUrl());

		File file = new File(path, fileName);
		LogUtil.e("wulianshu","下载的文件保存到本地为："+file.getAbsolutePath());
		RandomAccessFile randomFile = null;
		try{
			randomFile = new RandomAccessFile(file, "rw");
			job.setDownloadedSize(randomFile.length());
		}catch (Exception e) {
			e.printStackTrace();
			if(randomFile != null)
				randomFile.close();
			job.setExceptionType(DownloadJob.FILE_NOT_FOUND);
			return DOWNLOAD_FILE_ERROR;
		}


		job.setProgress(job.initProgress());
		if(job.getProgress()==100){
			if(randomFile != null)
				randomFile.close();
			return DOWNLOAD_SUCCESS;
		}

		httpGet.addHeader("Range",
				"bytes=" + randomFile.length() + "-");
		String userAgent = PlayerUtils.getUserAgent(job.getEntity().getSite(), MoviesHttpApi.LeTvBitStreamParam.KEY_DOWNLOAD, DownloadInfo.MP4);

		if (!"letv".equals( job.getEntity().getSite()) && !"nets".equals(job.getEntity().getSite())) {
			int downloadposition = 0;
			if(job.getCurrentdownloadpositon() < job.getOutSiteDataInfo().getOutSiteDatas().size()){
				downloadposition = job.getCurrentdownloadpositon();
			}else if (job.getCurrent_streamlistposition() < job.getOutSiteDataInfo().getOutSiteDatas().size()){
				downloadposition = job.getCurrent_streamlistposition();
			} else{
				downloadposition = 0;
			}
			String hearder = job.getOutSiteDataInfo().getOutSiteDatas().get(downloadposition).getHeader();

			if (!StringUtil.isEmpty(hearder)) {
				httpGet.addHeader("User-Agent", hearder);
			}
		} else {
			if (!StringUtil.isEmpty(userAgent)) {
				httpGet.addHeader("User-Agent", userAgent);
			}
		}

		HttpResponse response=new DefaultHttpClient(params).execute(httpGet);	
		HttpEntity entity = response.getEntity();	
		
		
		
		StatusLine statusLine = response.getStatusLine();
		int respCode = statusLine.getStatusCode();
		if(HttpURLConnection.HTTP_PARTIAL != respCode){
			if(randomFile != null)
				randomFile.close();
			return DOWNLOAD_URL_INVALID;
		}
		//上报
//		if(job.getAutoSnifferRetry()){
//			job.addReportState(PlayerUtils.M401);
//		}else{
//			job.addReportState(PlayerUtils.M411);
//		}
//		if(PlayerUtils.isOutSite(job.getEntity().getSite())){
//			job.getmSnifferReport().startReportOnBackgroundThread();
//		}
		//
		long length = entity.getContentLength();			
		long totalSize = randomFile.length()+length;
		job.setTotalSize(totalSize);
		
		InputStream in = entity.getContent();
		if(in == null){
			if(randomFile != null)
				randomFile.close();
			return DOWNLOAD_FILE_ERROR;
		}
		
		randomFile.seek(randomFile.length());

		byte[] buffer = new byte[1024];
		int lenght = 0;
		
		try {
			while ( (lenght = in.read(buffer)) > 0 && !job.isCancelled()) {	
				randomFile.write(buffer,0, lenght);
				job.setDownloadedSize((job.getDownloadedSize()+lenght));
				job.setRate();
			}
		} catch (Exception e) {
			randomFile.close();
			httpGet.abort();
			if (e.toString().contains("No space left on device")) {
				LogUtil.e("wulianshu","sibiantai");
				job.setExceptionType(DownloadJob.SD_SPACE_FULL);
			} else if (e.toString().contains("java.io.FileNotFoundException")
					|| e.toString().contains("java.io.IOException: write failed: EIO (I/O error)")) {
				job.setExceptionType(DownloadJob.NO_SD);// 没有sdcard，或者sdcard拔出或者存储器模式
			}
			e.printStackTrace();
			return DOWNLOAD_FILE_ERROR;
		}
		
		randomFile.close();
		httpGet.abort();
		//in.close();

		if( job.getDownloadedSize() < job.getTotalSize()){ //解决有时没下载完就返回true，显示下载完成
			return DOWNLOAD_FILE_ERROR ;
		}
		
		return DOWNLOAD_SUCCESS;
	}

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause(DownloadJob job) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

}
