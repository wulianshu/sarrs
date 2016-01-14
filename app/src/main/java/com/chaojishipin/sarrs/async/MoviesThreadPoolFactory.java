package com.chaojishipin.sarrs.async;

import com.letv.component.core.async.ThreadPoolOptions;

/**
 * 线程池工厂
 * */
public class MoviesThreadPoolFactory {

	/**
	 * 默认配置
	 * */
	private static final ThreadPoolOptions defaultOptions = new ThreadPoolOptions();

	static {
		defaultOptions.priority = Thread.NORM_PRIORITY;
		defaultOptions.size = 10;
		defaultOptions.waitPeriod = 100;
		defaultOptions.isReplayFailTask = false;
	}

	public static MoviesThreadPool create(ThreadPoolOptions options) {
		return initialize(options);
	}

	private static MoviesThreadPool initialize(ThreadPoolOptions options) {
		if (options == null) {
			options = defaultOptions;
		}
		MoviesThreadPool threadPool = new MoviesThreadPool(options);
		return threadPool;
	}
}
