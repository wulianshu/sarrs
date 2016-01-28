package com.mylib.download;

import java.util.concurrent.ThreadPoolExecutor;

public class TaskDiscardPolicy extends BaseRejectedPolicy {

	@Override
	public void rejectedExecutionImpl(Runnable r, ThreadPoolExecutor executor) {
		printLog(" DiscardPolicy rejectedExp ");
	}

}
