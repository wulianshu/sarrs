package com.mylib.download;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.conn.ConnectTimeoutException;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.VStreamInfoList;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.download.download.DownloadHandler;
import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadRequestManager;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.CDEManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.letv.pp.func.CdeHelper;
import com.mylib.download.DownloadConstant.DownloadType;
import com.mylib.download.DownloadQueue.DownloadCallback;
import com.mylib.download.RequestConstant.HttpMode;

public class MyDownloadTask implements Runnable {

	protected DownloadJob job;
	private final int MAX_RETRY_COUNT = 2;
	private ArrayList<HashMap<String, String>> downloadUrls = new ArrayList<>();

	private IDown mDown;
	private IDownload mRequest;
	private DownloadCallback mCallback;
	
	public MyDownloadTask(IDownload request, DownloadCallback callback) {
		super();
		this.mRequest = request;
		this.mCallback = callback;
		this.job = request.getJob();
	}

	public IDownload getRequest() {
		return mRequest;
	}

	public boolean pauseTask(){
		if(mDown != null){
			return mDown.pauseTask();
		}
		return true;
	}

	@Override
	public void run() {
		if(!initDownload()){
			mCallback.onDownloadFailed(0, IDownloadManager.DownloadExp.CODE_NET_SERVER, "", mRequest);
			return;
		}
		try {
			startDownload();
		}catch(Throwable e){
			e.printStackTrace();
			mCallback.onDownloadFailed(0, IDownloadManager.DownloadExp.CODE_NET_SERVER, e.toString(), mRequest);
			return;
		}
	}

	private boolean initDownload() {
		if (null == job) {
			return false;
		}
		DownloadEntity entity = job.getEntity();
		if (null == entity) {
			return false;
		}

		if (!"letv".equals(entity.getSite()) && !"nets".equals(entity.getSite())) {
//                用唯一id请求FilePath，每次下载只请求一次，完全失败后，才会重新请求
			if(TextUtils.isEmpty(job.getmOutSiteDownloadPath())){
				job.getOutSiteStream(this);
				if(TextUtils.isEmpty(job.getmOutSiteDownloadPath()))
					return false;
			}
			int retry_count = 0;
			while(retry_count < MAX_RETRY_COUNT) {
				try {
					URL url = new URL(job.getmOutSiteDownloadPath());
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(10000);
					InputStream input = conn.getInputStream();
					byte[] bytes = new byte[45];
					input.read(bytes, 0, bytes.length);
					String str = new String(bytes, "UTF-8");
					LogUtil.e("wulianshu", "外站源下载时判断文件类型的判断：" + str);
					if (str.contains(ConstantUtils.M3U8FILETAG)) {
						setUrl(DownloadInfo.M3U8, job.getmOutSiteDownloadPath());
						LogUtil.e("wulianshu","下载地址为："+job.getmOutSiteDownloadPath());
						return true;
					} else {
						setUrl(DownloadInfo.MP4, job.getmOutSiteDownloadPath());
						LogUtil.e("wulianshu", "下载地址为：" + job.getmOutSiteDownloadPath());
						return true;
					}
				} catch (Exception e) {
					LogUtil.e("wulianshu", "判断文件类型请求超时：" + e.toString());
					retry_count++;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
						return false;
					}
				}
			}
			return false;
		} else if ("nets".equals(entity.getSite())) {
			DownloadRequestManager request = new DownloadRequestManager();
			String resultUrl = request.getCloudDiskDownloadUrl(job.getEntity());
			if (StringUtil.isEmpty(resultUrl)) {
				return false;
			}
			resultUrl = getLinkShellUrl(resultUrl);
			setUrl(DownloadInfo.MP4, resultUrl);
		} else if ("letv".equals(entity.getSite())) {
			DownloadRequestManager request = new DownloadRequestManager();
			VStreamInfoList data = request.getDownloadData(job.getEntity());
			downloadWithVStreamInfoList(data);
		}
		job.updateDownloadEntity();
		return true;
	}

	private boolean downloadWithVStreamInfoList(VStreamInfoList data) {
		if (null == data) {
			return false;
		}
		String videoCode = PlayerUtils.VIDEO_MP4_720_db;
		DownloadEntity localEntity =DataUtils.getInstance().getDownloadEntity(job.getEntity());
		if (localEntity != null && localEntity.getCurrClarity() != null) {
			videoCode = localEntity.getCurrClarity();
		} else {
			if (data.get(PlayerUtils.VIDEO_MP4_720_db) != null) {
				videoCode = PlayerUtils.VIDEO_MP4_720_db;
			} else if (data.get(PlayerUtils.VIDEO_MP4) != null) {
				videoCode = PlayerUtils.VIDEO_MP4;
			} else if (data.get(PlayerUtils.VIDEO_MP4_350) != null) {
				videoCode = PlayerUtils.VIDEO_MP4_350;
			}
		}
		job.getEntity().setCurrClarity(videoCode);
		String resultUrl = "";
		if (null != data.get(videoCode)) {
			resultUrl = data.get(videoCode).getMainUrl();
			if (TextUtils.isEmpty(resultUrl)) {
				resultUrl = data.get(videoCode).getBackUrl0();
				if (TextUtils.isEmpty(resultUrl)) {
					resultUrl = data.get(videoCode).getBackUrl1();
					if (TextUtils.isEmpty(resultUrl)) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		resultUrl = getLinkShellUrl(resultUrl);
		setUrl(DownloadInfo.MP4, resultUrl);

		return true;
	}

	private String getLinkShellUrl(String url) {
		CDEManager mCDEManager = CDEManager.getInstance(ChaoJiShiPinApplication.getInstatnce());
		CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
		return cdeHelper.getLinkshellUrl(url);
	}

	private void setUrl(String key, String url) {
		if(key.equals(DownloadInfo.M3U8)){
			job.getEntity().setDownloadType(DownloadInfo.M3U8);
		}else{
			job.getEntity().setDownloadType(DownloadInfo.MP4);
		}
		HashMap<String, String> urlMap = new HashMap<String, String>();
		urlMap.put(key, url);
		downloadUrls.add(urlMap);
	}

	private boolean startDownload() throws Exception{
		for (int i = 0; i < downloadUrls.size(); i++) {
			HashMap map = downloadUrls.get(i);
			Iterator iter = map.entrySet().iterator();
			String downLoadType = "";
			String url = "";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				downLoadType = (String) entry.getKey();
				url = (String) entry.getValue();
			}
			job.getEntity().setDownloadUrl(url);
			if (downLoadType.equals(DownloadInfo.MP4)) {
				mDown = new CDNDownload(mRequest, mCallback, job, this);
			} else {
				DataUtils.getInstance().updateDownloadType(job, DownloadInfo.M3U8);
				mDown = new M3u8Download(mRequest, mCallback, job, this);
			}
			job.getEntity().setDownloadType(downLoadType);
			job.getEntity().setDownloadUrl(url);

			int count = 0;
			while (true){
				boolean result = mDown.download();
				if(result)
					return true;
				else{
					++count;
					if(count > MAX_RETRY_COUNT)
						return false;
					try{
						Thread.sleep(1000);
					}catch(Throwable e){
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
}
