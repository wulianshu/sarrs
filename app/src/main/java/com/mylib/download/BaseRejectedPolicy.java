package com.mylib.download;

import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class BaseRejectedPolicy implements RejectedExecutionHandler {

	public BaseRejectedPolicy() {
		
	}
	
	public abstract void rejectedExecutionImpl(Runnable r, ThreadPoolExecutor executor);
	
	@Override
	final public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		
		rejectedExecutionImpl(r, executor);
		
	}
	
	protected void printLogE(String log){
		LogUtil.l(log);
	}
	
	protected void printLog(String log){
		LogUtil.l(log);
	}

}
