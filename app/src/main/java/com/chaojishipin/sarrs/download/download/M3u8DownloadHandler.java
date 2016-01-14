package com.chaojishipin.sarrs.download.download;

import android.util.Log;

import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.thirdparty.LoginHelper;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.ConstantUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class M3u8DownloadHandler implements DownloadHandler {
	
	public static final String TAG = "m3u8download";
	
	private DownloadJob job;
	private DownloadEntity downloadEntity;
	private File dir;
	private double currentLoadedSize =0;
	private String url;
	private String redirectHost;
	private int redirectPort;
	private String redirectScheme;

	@Override
	public int downloadFile(DownloadJob job) {
		this.job = job;
		downloadEntity = job.getEntity();
		
		String path = job.getDestination()+"/"+downloadEntity.getSaveName();
        String fileName = downloadEntity.getSaveName();
        url = downloadEntity.getDownloadUrl();
        new DownloadManager().deleteEmptyFile(path+".mp4");
        
		//构建下载目录
        List<String> segments = null;
		try{
			dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();   
		}catch (Exception e){
			e.printStackTrace();
			job.setExceptionType(DownloadJob.FILE_NOT_FOUND);
			return DOWNLOAD_FILE_ERROR;
		}
		int downloadM3u8Status = 0;
		downloadM3u8Status = downLoadTopM3u8();
		if (downloadM3u8Status == DOWNLOAD_SUCCESS){
			File temp_m3u8 = new File(dir, ".temp_m3u8");
			File target_m3u8_file = new File(dir,fileName+".m3u8");
			LogUtil.e("wulianshu","下载的m3u8的地址为："+target_m3u8_file.getAbsolutePath());
			File temp_segments = new File(dir, "temp");
			if(currentLoadedSize == 0L){
				currentLoadedSize = initSize(dir);
			}
			try {
				temp_m3u8.createNewFile();
				target_m3u8_file.createNewFile();
				int currentIndex = 0;
				downloadM3u8Status = downloadFile(temp_m3u8,url,false);
				if(downloadM3u8Status == DOWNLOAD_SUCCESS){
					if(temp_segments.exists()){
						try {
							FileReader reader = new FileReader(temp_segments);
							BufferedReader br = new BufferedReader(reader);
							// 读取文件
							String line;
							while((line = br.readLine())!= null){
								if(line.startsWith("current_index=")){
									currentIndex = Integer.valueOf(line.split("=")[1]);
									break;
								}
							}
							br.close();
						}	catch (Exception e){
							temp_segments.delete();
							currentIndex = 0;
						}
					}
					segments = createM3u8LocalCopy(temp_m3u8,target_m3u8_file);
					return downloadSegments(segments,currentIndex);
				}
			} catch (Exception e) {
				if (e.toString().contains("No space left on device")) {
					LogUtil.e("wulianshu","手机内存不足 M3U8");
					job.setExceptionType(DownloadJob.SD_SPACE_FULL);
				} else if (e.toString().contains("java.io.FileNotFoundException")
						|| e.toString().contains("java.io.IOException: write failed: EIO (I/O error)")) {
					job.setExceptionType(DownloadJob.NO_SD);// 没有sdcard，或者sdcard拔出或者存储器模式
				}
				e.printStackTrace();
			}
		}

		return downloadM3u8Status;
	}
	
	/**
	 * 下载顶级m3u8文件。
	 * 现在只发现cntv是二级m3u8格式
	 */
	public int downLoadTopM3u8(){
		int result = DOWNLOAD_SUCCESS;
		if(downloadEntity.getSite().equals("cntv")){
			File top_m3u8 = new File(dir, ".top_m3u8");
			try {
				result = downloadFile(top_m3u8,url,false);
				if(result == DOWNLOAD_SUCCESS){
//					LogUtils.d(TAG,"topm3u8 file downloaded" );
					
					ArrayList<String> ret = new ArrayList<String>();
					FileReader fr = new FileReader(top_m3u8);
					BufferedReader br = new BufferedReader(fr);
					
					String tempString;
					String hostUrl;
					try {
						URI m3u8Uri = new URI(url);
						hostUrl = URIUtils.createURI(m3u8Uri.getScheme()
								, m3u8Uri.getHost()
								, m3u8Uri.getPort()
								,null,null,null).toString();
//						LogUtils.d(TAG,"hostUrl:"+hostUrl );
					} catch (URISyntaxException e) {
						e.printStackTrace();
						fr.close();
						br.close();
						throw new IOException("unavailable m3u8 download url");
					}
					while((tempString = br.readLine()) != null){
						if(!tempString.startsWith("#")){
							if(tempString.startsWith("/")){
								tempString = tempString.substring(1);
								tempString = new StringBuilder(hostUrl).append(tempString).toString();
							}
							ret.add(tempString);
						}
						else{
						}
					}
					fr.close();
					br.close();
					if(null!=ret && ret.size() > 0){
						String m3u8url = ret.get(ret.size()-1);//取出最后一个，清晰度最高的。
						url =m3u8url;
					}
				}else{
					
				}
			} catch (Exception e) {
			if (e.toString().contains("No space left on device")) {
				LogUtil.e("wulianshu","手机内存不足 M3U2");
				job.setExceptionType(DownloadJob.SD_SPACE_FULL);
			} else if (e.toString().contains("java.io.FileNotFoundException")
					|| e.toString().contains("java.io.IOException: write failed: EIO (I/O error)")) {
				job.setExceptionType(DownloadJob.NO_SD);// 没有sdcard，或者sdcard拔出或者存储器模式
			}
			e.printStackTrace();
		}
	  }
		return result;
	}
	@Override
	public void onCompleted() {

	}

	@Override
	public void onFailure() {

	}

	@Override
	public void onPause(DownloadJob job) {

	}

	@Override
	public void onStart() {
		
	}

	private int downloadSegments(List<String> segments, int lastCompleteIndex) throws IOException {
		job.setProgress(job.initProgress());
		if(job.getProgress()==100 || lastCompleteIndex > segments.size()-1){
			return DOWNLOAD_SUCCESS;
		}
		job.setTotalSize(segments.size());
		int downloadSegmentStatus = 0;
//		LogUtils.d(TAG,"start download segments, total size is "+ segments.size() );
		for(int i = lastCompleteIndex; i < segments.size(); i++){
			String segment = segments.get(i);
			File segFile = new File(dir, String.valueOf(i));
//			LogUtils.d(TAG,"segment download start, the index is "+i );
			try {
				downloadSegmentStatus = downloadFile(segFile, segment,true);
				if(downloadSegmentStatus != DOWNLOAD_SUCCESS || job.isCancelled()){
					FileWriter temp_fw = new FileWriter(new File(dir,"temp"),false);
					temp_fw.write(String.format("current_index=%d", i));
					temp_fw.close();
					return downloadSegmentStatus;
				}
//				else if(i==lastCompleteIndex){
//					if(job.getAutoSnifferRetry()){
//						job.addReportState(PlayerUtils.M301);
//					}else{
//						job.addReportState(PlayerUtils.M311);
//					}
////					if(PlayerUtils.isOutSite(job.getEntity().getSite())){
////						job.getmSnifferReport().startReportOnBackgroundThread();
////					}
//				}
			} catch (IOException e) {
				FileWriter temp_fw = new FileWriter(new File(dir,"temp"),false);
				temp_fw.write(String.format("current_index=%d", i));
				temp_fw.close();
				e.printStackTrace();
				throw e;
			}
			job.setDownloadedSize(i + 1);
//			LogUtils.d(TAG,"segment download done, the index is "+i );
		}
//		LogUtils.d(TAG,"all segments downloaded!" );
		FileWriter temp_fw = new FileWriter(new File(dir,"temp"),false);
		temp_fw.write(String.format("current_index=%d", segments.size()));
		temp_fw.close();
		return downloadSegmentStatus;
	}
	
	private List<String> createM3u8LocalCopy(File input, File output) throws IOException {
		ArrayList<String> ret = new ArrayList<String>();
		FileReader fr = new FileReader(input);
		BufferedReader br = new BufferedReader(fr);
		FileWriter fw = new FileWriter(output);
		String tempString;
		StringBuilder targetContent = new StringBuilder();
		StringBuilder downloadSegments = new StringBuilder();
		int count = 0;
		String parentUrl;
		String hostUrl;
		String fid = "";
		String pno = "";
//		LogUtils.d(TAG,"parse m3u8" );
		try {
			URI m3u8Uri = new URI(url);
			parentUrl = m3u8Uri.resolve(".").toString();
			String host = m3u8Uri.getHost();


//			LogUtils.d(TAG,"parentUrl:"+parentUrl );



//			LogUtils.d(TAG,"hostUrl:"+hostUrl );

			if (redirectHost != null && redirectHost.length() > 0)
			{
				hostUrl = URIUtils.createURI(redirectScheme
						, redirectHost
						, redirectPort
						,null,null,null).toString();
				int index = parentUrl.indexOf(host) + host.length();
				String subString = parentUrl.substring(index);
				if (subString.startsWith("/") && hostUrl.endsWith("/"))
				{
					parentUrl = hostUrl + subString.substring(1);
				}else {
					parentUrl = hostUrl + subString;
				}

			}else {
				hostUrl = URIUtils.createURI(m3u8Uri.getScheme()
						, m3u8Uri.getHost()
						, m3u8Uri.getPort()
						,null,null,null).toString();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fw.close();
			fr.close();
			br.close();
			throw new IOException("unavailable m3u8 download url");
		}
		while((tempString = br.readLine()) != null){
			if(!tempString.startsWith("#") && !StringUtil.isEmpty(tempString)){
				if(tempString.startsWith("/")){
					tempString = tempString.substring(1);
					tempString = new StringBuilder(hostUrl).append(tempString).toString();
				}else if(!tempString.startsWith("http://")){
					tempString = new StringBuilder(parentUrl).append(tempString).toString();
				}
//				if (downloadEntity.getSrc().equals(PlayerUtils.SITE_MANGGUO))
//				{
//					tempString = tempString + "&" + fid + "&" + pno;
//				}
				ret.add(tempString);
				downloadSegments.append(tempString).append("\n");
				targetContent.append(String.format("file:/%s/%s", dir.getAbsolutePath(), String.valueOf(count))).append("\n");
				count ++;
			}
			else{
				targetContent.append(tempString).append("\n");
			}
		}
		fw.write(targetContent.toString());
		fw.close();
		fr.close();
		br.close();
//		LogUtils.d(TAG,"parse complete" );
		return ret;
	}
	private long initSize(File dir){
		return 0L;
	}
	private int downloadFile(File output, String url, boolean m3u8OrSegment) throws IOException {
		int downloadStatus = 0;
//		LogUtils.d(TAG, "download :"+url);
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
		HttpConnectionParams.setSoTimeout(params, 15 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192 * 5);
		HttpGet httpGet = new HttpGet(url);
		RandomAccessFile randomFile = new RandomAccessFile(output, "rw");
		
//		job.setRetryNum(0);
		httpGet.addHeader("Range","bytes=" + randomFile.length() + "-");
		String userAgent = PlayerUtils.getUserAgent(job.getEntity().getSite(), ConstantUtils.LeTvBitStreamParam.KEY_DOWNLOAD, DownloadInfo.M3U8);
//		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36";



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
		}else{
			if(!StringUtil.isEmpty(userAgent)){
				httpGet.addHeader("User-Agent", userAgent);
			}
		}



		HttpClient client = new DefaultHttpClient(params);
		HttpContext httpContext = new BasicHttpContext();
		HttpResponse response=client.execute(httpGet, httpContext);
		if (!m3u8OrSegment)
		{
			HttpHost targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			redirectHost = targetHost.getHostName();
			redirectPort = targetHost.getPort();
			redirectScheme = targetHost.getSchemeName();
		}

		HttpEntity entity = response.getEntity();	
		
		StatusLine statusLine = response.getStatusLine();
		int respCode = statusLine.getStatusCode();
		if(HttpURLConnection.HTTP_PARTIAL == respCode){
			randomFile.seek(randomFile.length());
		}
		else if(HttpURLConnection.HTTP_OK == respCode){
		}
		else if(416 == respCode){//请求range超出范围
			if(!m3u8OrSegment){//如果是m3u8文件，删除，重新下载
				output.delete();
				randomFile.close();
				return DOWNLOAD_FILE_ERROR;
			}
		}
		else{
			randomFile.close();
			return DOWNLOAD_URL_INVALID;
		}
		long length = entity.getContentLength();

		InputStream in = entity.getContent();
		if(in == null){
			if(randomFile != null)
				randomFile.close();
			return DOWNLOAD_URL_INVALID;
		}
		/**
		 * 返回数据获取的contentLength可能不大于0但是仍然有数据，http header中无contentLength字段
		 */
//		if (length > 0 || !m3u8OrSegment)
		{
			int blockSize = 2024;
			byte[] buffer = new byte[blockSize];


			int readCount = 0; // 已经成功读取的字节的个数
			int readLen = 0;
			while (readLen >= 0 && !job.isCancelled()) {
				readLen = in.read(buffer, 0, blockSize);
				if (readLen > 0) {
					randomFile.write(buffer, 0, readLen);
					readCount += readLen;
				}
			}
			if (!job.isCancelled()) {
				job.setmM3u8DownloadedSize(readCount);
				job.setM3u8Rate();
			}
			downloadStatus = DOWNLOAD_SUCCESS;
		}

			if(job.isCancelled()){
//				LogUtils.d(TAG,"downloaded cancelled" );
				if(m3u8OrSegment){
					randomFile.close();
				}
					httpGet.abort();
				return DOWNLOAD_SUCCESS;
			}
		
		randomFile.close();
		httpGet.abort();
		return downloadStatus;
	}

}
