package com.chaojishipin.sarrs.async;


import com.letv.http.bean.LetvBaseBean;

/**
 * 异步任务接口类（网络任务）
 * */
public interface RequestResultListener<T extends LetvBaseBean> {

	/**
	 * 异步任务请求失败
	 * */
	public boolean onRequestFailed();

	/**
	 * 异步任务请求成功
	 * */
	public void onRequestSuccess(int updateId, T result);
	
	/**
	 * 开始请求
	 */
	public void onPreRequest();
	

}
