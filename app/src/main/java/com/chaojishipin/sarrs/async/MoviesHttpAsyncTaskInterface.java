package com.chaojishipin.sarrs.async;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;

/**
 * 异步任务接口类（网络任务）
 * */
public interface MoviesHttpAsyncTaskInterface<T extends LetvBaseBean> {

	/**
	 * 异步任务开始前
	 * */
	public boolean onPreExecute();

	/**
	 * 异步任务执行
	 * */
	public LetvDataHull<T> doInBackground();

	/**
	 * 异步任务完成
	 * */
	public void onPostExecute(int updateId, T result);
	//public void onPostExecute(int updateId, String result);
}
