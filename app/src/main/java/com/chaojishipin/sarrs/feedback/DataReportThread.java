package com.chaojishipin.sarrs.feedback;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by wangyemin on 2015/10/10.
 */
public class DataReportThread extends Thread {
    private Handler mHandler;

    public DataReportThread() {
        super();
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler();
        Looper.loop();
        super.run();
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void post(Runnable r) {
        if (mHandler != null)
            mHandler.post(r);
    }
}
