package com.mylib.download;

import android.text.TextUtils;

import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by liuzhuo on 2016/1/21.
 */
public class CDNDownloadTask extends MyDownloadTask {

    public CDNDownloadTask(IDownload request, DownloadQueue.DownloadCallback callback) {
        super(request, callback);
    }


}
