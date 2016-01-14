package com.chaojishipin.sarrs.async;

public interface MoviesBaseTask {

	/**
	 * 任务执行
	 * */
	public boolean run();

	/**
	 * 取消任务
	 * */
	public void cancel();

	/**
	 * 是否取消
	 * */
	public boolean isCancelled();
}
