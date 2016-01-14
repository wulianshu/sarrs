package com.chaojishipin.sarrs.async;

import com.letv.component.core.async.ThreadPoolOptions;

public abstract class MoviesBaseTaskImpl implements MoviesBaseTask {

	protected boolean isCancel = false;
	/**
	 * 线程池
	 * */
	protected static final MoviesThreadPool mThreadPool;

	static {// 初始化线程池
		ThreadPoolOptions options = new ThreadPoolOptions();
		options.priority = Thread.NORM_PRIORITY + 1;
		options.size = 5;
		options.waitPeriod = 1000;
		options.isReplayFailTask = false;
		mThreadPool = MoviesThreadPoolFactory.create(options);
	}

	@Override
	public void cancel() {
		this.isCancel = true;
		if (mThreadPool != null) {
			mThreadPool.removeTask(this);
		}
	}

	@Override
	public boolean isCancelled() {
		return this.isCancel;
	}
}
