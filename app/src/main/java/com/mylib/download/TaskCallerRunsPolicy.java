package com.mylib.download;

import java.util.concurrent.ThreadPoolExecutor;

public class TaskCallerRunsPolicy extends BaseRejectedPolicy {

	@Override
	public void rejectedExecutionImpl(Runnable r, ThreadPoolExecutor executor) {
		printLog(" CallerRunsPolicy rejectedExp ");
		if (!executor.isShutdown()) {
            r.run();
        }
	}

}
