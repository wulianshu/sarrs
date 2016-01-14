package com.chaojishipin.sarrs.async;

import android.content.Context;

import com.chaojishipin.sarrs.download.bean.SnifferReport;
import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;

/**
 * 嗅探上报
 * @author zhangshuo
 *
 */
public class PlaySnifferReportTask extends MoviesHttpAsyncTask<LetvBaseBean>{

	private SnifferReport mSnifferReport;
	
	public SnifferReport getmSnifferReport() {
		return mSnifferReport;
	}

	public void setmSnifferReport(SnifferReport mSnifferReport) {
		this.mSnifferReport = mSnifferReport;
	}

	public PlaySnifferReportTask(Context context) {
		super(context);
	}

	@Override
	public LetvDataHull<LetvBaseBean> doInBackground() {
		return MoviesHttpApi.requestSnifferReport(mSnifferReport);
	}

	@Override
	public void onPostExecute(int updateId, LetvBaseBean result) {
		LogUtil.e("xll","parse letvBaseBean result "+result);
	}


	/*@Override
	public void onPostExecute(int updateId, String result) {
		LogUtil.e("xll","parse string result "+result);
	}*/
}
