package com.chaojishipin.sarrs.download.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;


import org.json.JSONObject;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.OutSiteData;
import com.chaojishipin.sarrs.bean.VStreamInfoList;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.thread.ThreadPoolManager;
import com.chaojishipin.sarrs.utils.CDEManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.FileUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.pp.func.CdeHelper;


public class DownloadTask extends AsyncTask<Void, Integer, Boolean> {

    private final static String TAG = "DownloadTask";
    private DownloadJob mJob;
    private String MP4 = "mp4";
    private String M3U8 = "m3u8";
    private int retry_count = 0;
    private static final int MAX_RETRY_COUNT = 2;
//    private WebView mWebView;
//    private TestJavaScriptInterface mTestInterface;
//    public class TestJavaScriptInterface {
//        @JavascriptInterface
//        public void startFunction(final String result) {
//            try {
//                JSONObject obj = new JSONObject(result);
//                String stream = obj.getString("stream");
//                LogUtil.e("wulianshu","NEW js call result !");
//                if(stream != null && !"".equals(stream)){
//                    LogUtil.e("wulianshuaddUrl", "addsniff");
//                    String fileName = "jscutresult4download.html";
//                    FileUtils.writeHtmlToData(ChaoJiShiPinApplication.getInstatnce(),fileName, stream);
////                  downloadurllist.add(stream);
//                }else{
//                    LogUtil.e("wulianshuaddUrl", "sniffnoresult");
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//    @SuppressLint("SetJavaScriptEnabled")
//    public void initWebView(Context activity) {
//        mTestInterface = new TestJavaScriptInterface();
//        setConfigCallback((WindowManager) activity.getApplicationContext().getSystemService(
//                Context.WINDOW_SERVICE));
//        mWebView = new WebView(ChaoJiShiPinApplication.getInstatnce());
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setAppCacheEnabled(false);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        mWebView.clearCache(true);
//        //请求jscode 接口
//        UpdateSnifferManager.getInstance(activity).startUpdate();
//        String htmlUrl = UpdateSnifferManager.getInstance(activity).getHtmlURL();
//        LogUtil.e("wulianshu", "NEW js file get from " + htmlUrl);
//        mWebView.loadUrl(htmlUrl);
//        mWebView.addJavascriptInterface(mTestInterface, "TestJavaScriptInterface");
//        mWebView.setWebViewClient(new WebViewClient() {
//
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d(TAG, " url:" + url);
//
//
//                view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
//                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                //
//                LogUtil.e("wulianshu", " NEW js webview loaded ok");
//            }
//        });
//    }

    //    /**
//     * WebView防止内存泄露
//     *
//     * @param windowManager zhangshuo 2014年12月25日 下午5:38:12
//     */
//    public void setConfigCallback(WindowManager windowManager) {
//        try {
//            Field field = WebView.class.getDeclaredField("mWebViewCore");
//            field = field.getType().getDeclaredField("mBrowserFrame");
//            field = field.getType().getDeclaredField("sConfigCallback");
//            field.setAccessible(true);
//            Object configCallback = field.get(null);
//            if (null == configCallback) {
//                return;
//            }
//            field = field.getType().getDeclaredField("mWindowManager");
//            field.setAccessible(true);
//            field.set(configCallback, windowManager);
//        } catch (Exception e) {
//
//        }
//    }
    public void setOutsidedownloadPath(String outsidedownloadPath) {
        this.outsidedownloadPath = outsidedownloadPath;
    }

    public String getOutsidedownloadPath() {
        return outsidedownloadPath;
    }

    private String outsidedownloadPath ;
    private ArrayList<HashMap<String, String>> downloadUrls;

    public DownloadTask(DownloadJob job) {
        mJob = job;
        downloadUrls = new ArrayList();
    }

    @Override
    protected void onPreExecute() {
        mJob.notifyDownloadStarted();
        super.onPreExecute();
    }

    @Override
    public void onPostExecute(Boolean result) {
//		LogUtils.i("dyf", "下载成功：" + result);
        if (result) {
            mJob.onCompleted();
        } else {
            mJob.onFailure();
        }
        super.onPostExecute(result);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            //外站源和
            //for循环
            //  if downloadFile true 成功
            //失败   继续执行
            //乐视源
            return downloadFile();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            if (e.toString().contains("No space left on device")) {
                LogUtil.e("wulianshu","手机内存不足 DownloadTask");
                mJob.setExceptionType(DownloadJob.SD_SPACE_FULL);
            } else if (e.toString().contains("java.io.FileNotFoundException")
                    || e.toString().contains("java.io.IOException: write failed: EIO (I/O error)")) {
                mJob.setExceptionType(DownloadJob.NO_SD);// 没有sdcard，或者sdcard拔出或者存储器模式
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCancelled() {
        ChaoJiShiPinApplication.getInstatnce().getDownloadManager().notifyObservers();
        super.onCancelled();
    }

    private Boolean downloadFile() throws Exception {
        // 3g网络时默认不开启下载
        // 获取下载地址等信息，获取下载地址失败，返回false；
        retry_count = 0;
        if (!initDownload()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < downloadUrls.size(); i++) {
            HashMap map = downloadUrls.get(i);
            Iterator iter = map.entrySet().iterator();
            String downLoadType = "";
            String url = "";
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                downLoadType = (String) entry.getKey();
                url = (String) entry.getValue();
            }
            DownloadHandler downloadHandler = null;
            if (downLoadType.equals(DownloadInfo.MP4)) {
                downloadHandler = new CDNDownloadHandler();
            } else {
                downloadHandler = new M3u8DownloadHandler();
            }
            mJob.setDownloadHandler(downloadHandler);
            mJob.getEntity().setDownloadType(downLoadType);
            mJob.getEntity().setDownloadUrl(url);
//            if (!mJob.getEntity().getSrc().equals("letv") && !mJob.getEntity().getSrc().equals("nets")) {
//                mJob.getEntity().setUseUserAgent(true);
//            } else {
//                mJob.getEntity().setUseUserAgent(false);
//            }
            mJob.updateDownloadEntity();
            result = mJob.downloadFile();
            LogUtil.e("wulianshu","result:"+result);
            if (result == DownloadHandler.DOWNLOAD_SUCCESS)
                return true;
            else if (result == DownloadHandler.DOWNLOAD_URL_VALID || result == DownloadHandler.DOWNLOAD_FILE_ERROR) {
                /**
                 * 下载地址有效，重试3次，如果不成功，就下载失败
                 */
                for (int j = 0; j < 3; j++) {
                    result = mJob.downloadFile();
                    if (result == DownloadHandler.DOWNLOAD_SUCCESS)
                        return true;
                }

            }
            /**
             * 下载地址无效尝试下一个下载地址
             */
        }
        return false;
    }

    private boolean initDownload() {
        if (null == mJob) {
            return false;
        }
        DownloadEntity entity = mJob.getEntity();
        if (null == entity) {
            return false;
        }

        if (!"letv".equals(entity.getSite()) && !"nets".equals(entity.getSite())) {
//                用唯一id请求FilePath，每次下载只请求一次，完全失败后，才会重新请求
            if(TextUtils.isEmpty(outsidedownloadPath)){
                return false;
            }
            while(retry_count<MAX_RETRY_COUNT) {
                try {
                    URL url = new URL(outsidedownloadPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    InputStream input = conn.getInputStream();
                    byte[] bytes = new byte[45];
                    input.read(bytes, 0, bytes.length);
                    String str = new String(bytes, "UTF-8");
                    LogUtil.e("wulianshu", "外站源下载时判断文件类型的判断：" + str);
                    if (str.contains(ConstantUtils.M3U8FILETAG)) {
                        setUrl(DownloadInfo.M3U8, outsidedownloadPath);
                        LogUtil.e("wulianshu","下载地址为："+outsidedownloadPath);
                        return true;
                    } else {
                        setUrl(DownloadInfo.MP4, outsidedownloadPath);
                        LogUtil.e("wulianshu", "下载地址为：" + outsidedownloadPath);
                        return true;
                    }
                } catch (Exception e) {
                    LogUtil.e("wulianshu", "判断文件类型请求超时：");
                    retry_count++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                        return false;
                    }
                    e.printStackTrace();
                }
            }
            return false;
        } else if ("nets".equals(entity.getSite())) {
//			LogUtils.e("dyf", "!!!!!!!云盘下载!!!!!!");
            DownloadRequestManager request = new DownloadRequestManager();
            String resultUrl = request.getCloudDiskDownloadUrl(mJob.getEntity());
            if (StringUtil.isEmpty(resultUrl)) {
                return false;
            }
            resultUrl = getLinkShellUrl(resultUrl);
            setUrl(DownloadInfo.MP4, resultUrl);
        } else if ("letv".equals(entity.getSite())) {
            DownloadRequestManager request = new DownloadRequestManager();
            VStreamInfoList data = request.getDownloadData(mJob.getEntity());
            downloadWithVStreamInfoList(data);
        }

//        if ("letv".equals(entity.getSite())) {
//            DownloadRequestManager request = new DownloadRequestManager();
//            VStreamInfoList data = request.getDownloadData(mJob.getEntity());
//            downloadWithVStreamInfoList(data);
//        }else{
//            //外站源
//        }

        mJob.updateDownloadEntity();
        return true;
    }

    private boolean downloadWithVStreamInfoList(VStreamInfoList data) {
        DownloadHandler downloadHandler;
        if (null == data) {
            return false;
        }
        String videoCode = PlayerUtils.VIDEO_MP4_720_db;
        DownloadEntity localEntity = ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getDownloadEntity(mJob.getEntity());
        if (localEntity != null && localEntity.getCurrClarity() != null)
        {
            videoCode = localEntity.getCurrClarity();
        }else {
            if (data.get(PlayerUtils.VIDEO_MP4_720_db) != null) {
                videoCode = PlayerUtils.VIDEO_MP4_720_db;
            } else if (data.get(PlayerUtils.VIDEO_MP4) != null) {
                videoCode = PlayerUtils.VIDEO_MP4;
            } else if (data.get(PlayerUtils.VIDEO_MP4_350) != null) {
                videoCode = PlayerUtils.VIDEO_MP4_350;
            }
        }
        mJob.getEntity().setCurrClarity(videoCode);
        String resultUrl = "";
        if (null != data.get(videoCode)) {
            resultUrl = data.get(videoCode).getMainUrl();
            if (TextUtils.isEmpty(resultUrl)) {
//						resultUrl = PlayerP2PDecode.getInstance().getLePlayUrl(url);
                resultUrl = data.get(videoCode).getBackUrl0();
                if (TextUtils.isEmpty(resultUrl)) {
                    resultUrl = data.get(videoCode).getBackUrl1();
                    if (TextUtils.isEmpty(resultUrl)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
//				LogUtils.e("dyf", "!!!!!!!letv下载url!!!!!!" + resultUrl);
        resultUrl = getLinkShellUrl(resultUrl);
//		downloadHandler = new CDNDownloadHandler();
//		mJob.setDownloadHandler(downloadHandler);
//
//		mJob.getEntity().setDownloadType(DownloadInfo.MP4);
//		mJob.getEntity().setDownloadUrl(resultUrl);
//				mJob.getEntity().setFileSize(data.getFileSize());
//				mJob.setTotalSize(data.getFileSize());
//			}
        setUrl(DownloadInfo.MP4, resultUrl);
        // 下载信息上报
//		DownloadReport.downloadInfoReport(entity, getInfoHashId(downloadInfo, mJob.getEntity()));
//		mJob.updateDownloadEntity();

        return true;
    }

    private String getLinkShellUrl(String url) {
        CDEManager mCDEManager = CDEManager.getInstance(ChaoJiShiPinApplication.getInstatnce());
        CdeHelper cdeHelper = mCDEManager.getmCdeHelper();
        return cdeHelper.getLinkshellUrl(url);
    }

    private void setUrl(String key, String url) {
        if(key.equals(DownloadInfo.M3U8)){
            mJob.getEntity().setDownloadType(DownloadInfo.M3U8);
        }else{
            mJob.getEntity().setDownloadType(DownloadInfo.MP4);
        }
        HashMap<String, String> urlMap = new HashMap<String, String>();
        urlMap.put(key, url);
        downloadUrls.add(urlMap);
    }



//    public class OutSiteDataListener implements RequestListener<OutSiteDataInfo> {
//        @Override
//        public void onResponse(OutSiteDataInfo result, boolean isCachedData) {
//            LogUtil.e("xll ", "NEW  execute OutSite result ok !");
//            // 默认设置M3U8 有清晰度选项
//            //executeOutSitePlayCore(mStreamIndex);
//            //M3U8失败设置MP4
//            //executeOutSitePlayCore(ConstantUtils.OutSiteDateType.MP4);
//            //排列下载链接
//            for(int i=0;i<result.getOutSiteDatas().size();i++){
//                result.getOutSiteDatas().get(i).setPriority(Utils.getPriority4Download(result.getOutSiteDatas().get(i).getOs_type(),result.getOutSiteDatas().get(i).getRequest_format()));
//            }
//            Collections.sort(result.getOutSiteDatas());
//            List<String> downloadurllist = new ArrayList<String>();
//            for(int i=0;i<result.getOutSiteDatas().size();i++){
//                downloadurllist.addAll(result.getOutSiteDatas().get(i).getApi_list());
//                downloadurllist.addAll(result.getOutSiteDatas().get(i).getStream_list());
//            }
//
//        }
//
//        @Override
//        public void netErr(int errorCode) {
//            LogUtil.e("xll ", " execute OutSite net error");
//        }
//
//        @Override
//        public void dataErr(int errorCode) {
//            LogUtil.e("xll ", " execute OutSite data error");
//        }
//    }
//    void batchRuquestApi(OutSiteData outSiteData) {
//        if (outSiteData.getApi_list() != null && outSiteData.getApi_list().size() > 0) {
//            LogUtil.e("xll", "NEW js api size is " + outSiteData.getApi_list().size());
//            // 多线请求api
//            try {
//                ThreadPoolManager.getInstanse().createPool();
//                ThreadPoolManager.getInstanse().exeTaskTimOut(outSiteData.getApi_list(), outSiteData.getUrl(), outSiteData.getHeader(), 2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    void batchSendJsparam(OutSiteData  outSiteData){
//        ThreadPoolManager.getInstanse().shutdown();
//        while(!ThreadPoolManager.getInstanse().isTerminated()){
//            //等待执行完毕
//            LogUtil.e("xll","NEW wait for pool task finish ");
//        }
//        List<String> mApi_contentlist=ThreadPoolManager.getInstanse().getResultList();
//        LogUtil.e("wulianshu","mApi_contentlist:"+mApi_contentlist.toString());
//        ThreadPoolManager.getInstanse().shutdown();
//        if(mApi_contentlist!=null){
//            for (int j = 0; j < mApi_contentlist.size(); j++) {
//                for (int k = 0; k < outSiteData.getStream_list().size(); k++) {
//                    if (outSiteData.getUrl() != null && outSiteData.getTs() != null && outSiteData.getTe() != null)
//                        //api_list需要走js截流
//                        sendCutRequest(outSiteData.getUrl(), mApi_contentlist.get(j), outSiteData.getStream_list().get(k), outSiteData.getTs(), outSiteData.getTe());
//                }
//            }
//        }
//    }
//    /**
//     * wulianshu
//     *
//     *  发送js截流请求
//     *  @param apiContent  api-list 请求每一条api对应的api-content
//     *  @param requestUrl  防盗链接口返回播放地址
//     *  @param streamUrl  防盗链接口返回流数组对应的每一条url数据
//     *
//     * */
//    void sendCutRequest(String requestUrl,String apiContent,String streamUrl,String mTs,String mTe){
//        com.alibaba.fastjson.JSONObject obj = new  com.alibaba.fastjson.JSONObject();
//        LogUtil.e("wulianshu", "NEW send js requestUrl:" + requestUrl + " streamUrl " + streamUrl);
//        obj.put("requestUrl", Base64.encodeToString(requestUrl.getBytes(), Base64.DEFAULT));
//        obj.put("uStream", Base64.encodeToString(streamUrl.getBytes(), Base64.DEFAULT));
//        obj.put("apiContent", Base64.encodeToString(apiContent.getBytes(), Base64.DEFAULT));
//        //TODO rule 值获取
//        if(mTs!=null && mTe !=null){
//            LogUtil.e("wulianshu", "NEW js has rule");
//            com.alibaba.fastjson.JSONObject ruleObj = new  com.alibaba.fastjson.JSONObject();
//
//            if(!TextUtils.isEmpty(mTs)){
//                ruleObj.put("ts",mTs);
//            }
//            if(!TextUtils.isEmpty(mTe)){
//                ruleObj.put("te",mTe);
//            }
//            obj.put("rule", ruleObj);
//            LogUtil.e("wulianshu", "NEW js rule " + ruleObj.toString());
//            LogUtil.e("wulianshu", "NEW js ts te (" +mTs+")  ("+mTe+")");
//        }else{
//
//            LogUtil.e("wulianshu", "NEW js no rule");
//        }
//
//        String mSnifferParamter = obj.toString().replace("\\n","");
//        String fileName="request4download.html";
//        FileUtils.writeHtmlToData(ChaoJiShiPinApplication.getInstatnce(), fileName, mSnifferParamter);
//        LogUtil.e(TAG, "@@@@@@@" + mSnifferParamter);
//        // 发送消息调用JS代码
//        // 发送消息调用JS代码
//        //mHandler.sendEmptyMessage(Utils.GET_JS_RESULT);
//        LogUtil.e("wulianshu","NEW js call loadUrl !");
//        mWebView.clearCache(true);
//        LogUtil.e("wulianshu","NEW　js send params : "+mSnifferParamter);
//        mWebView.loadUrl("javascript:TestJavaScriptInterface.startFunction(dealWithRequest('" + mSnifferParamter + "'));");
//
//    }
}
