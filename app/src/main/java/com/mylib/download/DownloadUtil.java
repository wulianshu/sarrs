package com.mylib.download;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.utils.DataUtils;

public class DownloadUtil implements VideoDownloadManager.IShelfDownloadListener {

	private Handler mHandler;
	private AbsListView mListView;
	private boolean mIdle = true;
	private boolean pause = false;
	private DataUtils mUtil;
	private Context mContext;

	public interface DownloadEndListener{
		public void onDownloadEnd(DownloadJob job);
	}

	private DownloadEndListener mListener;

	public DownloadUtil(Context context, DownloadEndListener listener, AbsListView listView){
		mContext = context;
		mListener = listener;
		init(listView);
	}
	
	private void init(AbsListView list){
		mUtil = DataUtils.getInstance();
		mUtil.addDownloadListener(this);
		mHandler = new MyHandler(this);

		if(list != null){
			mListView = list;
			mListView.setOnScrollListener(new OnScrollListener(){
				@Override
				public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				}
				@Override
				public void onScrollStateChanged(AbsListView arg0, int scrollState) {
					changeScrollState(scrollState);
				}
			});
		}
	}
	
	private void changeScrollState(int scrollState){
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			mIdle = true;
		}else{ 
			mIdle = false;
		}
	}

	private MapItem getView(VideoDownload download, boolean isFinish){
		return getView(download.getJobId(), isFinish);
	}
	
	private boolean isIdle(){
		return mIdle;
	}
	
	private MapItem getView(String id, boolean isFinish){
		if(mListView == null)
			return null;
		try{
			if(isIdle() || isFinish){
				int first = mListView.getFirstVisiblePosition();
				int last = mListView.getLastVisiblePosition();

				for(int i=0; i<=last-first; i++){
					DownloadJob job = (DownloadJob) mListView.getAdapter().getItem(first + i);
					if(job.getEntity().getId().equals(id)){
						View v = mListView.getChildAt(i);
						if(v == null)
							return null;
						MapItem item = new MapItem(v, job);
						return item;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private void updateView(MapItem item, VideoDownload download){
		final ProgressBar v = (ProgressBar) item.view.findViewById(R.id.ProgressBar);
		final TextView tv = (TextView)item.view.findViewById(R.id.tv_download_status);
		final TextView downloadLength = (TextView) item.view.findViewById(R.id.tv_download_length);
		final DownloadJob job = download.getJob();
		mHandler.post(new Runnable() {
			public void run() {
				DataUtils.getInstance().setView(job, v, tv, downloadLength);
			}
		});
	}

	@Override
	public void onDownloading(VideoDownload download) {
		if(pause)
			return;
		MapItem item = getView(download, false);
		if(item == null )
			return;
		updateView(item, download);
	}

	@Override
	public void onDownloadPending(VideoDownload download){
		onDownloading(download);
	}
	
	@Override
	public void onPauseDownload(VideoDownload download) {
		MapItem item = getView(download, false);
		if(item == null)
			return;
		updateView(item, download);
	}

	@Override
	public void onDownloadFinish(final VideoDownload download) {
		mHandler.post(new Runnable(){
			public void run(){
				if(mListener != null)
					mListener.onDownloadEnd(download.getJob());
			}
		});
		final MapItem item = getView(download, true);
		if(item == null)
			return;
		updateView(item, download);
	}

	@Override
	public void onFileTotalSize(VideoDownload download) {
	}

	@Override
	public void onDownloadFailed(final VideoDownload download, final String msg) {
		MapItem item = getView(download, true);
		if(item == null)
			return;
		updateView(item, download);
	}
	
	public void destroy(){
		try{
			mUtil.removeDownloadListener(this);
		}catch(Exception e){}
	}

	public void onPause(){
		pause = true;
	}
	
	public void onResume(){
		pause = false;
	}
	
	class MapItem{
		public View view;
		public DownloadJob job;
		
		public MapItem(View v, DownloadJob b){
			view = v;
			job = b;
		}
	}
	
//	class MyReceiver extends BroadcastReceiver {
//
//		public void init(Context context){
//			IntentFilter mRefreshFilter = new IntentFilter();
//			mRefreshFilter.addAction(Constants.MESSAGE_DOWNLOAD_FINISH);
//			context.registerReceiver(this, mRefreshFilter);
//		}
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			try{
//				if(Constants.MESSAGE_DOWNLOAD_FINISH.equals(intent.getAction())){
//					onDownloadFinish(intent.getStringExtra("id"));
//				}
//			}catch(Exception e){
//				LogUtil.l(e.toString());
//			}
//		}
//	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<DownloadUtil> mFragmentView;

		MyHandler(DownloadUtil view) {
			this.mFragmentView = new WeakReference<DownloadUtil>(
					view);
		}

		@Override
		public void handleMessage(Message msg) {
			DownloadUtil service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					switch(msg.what){
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
