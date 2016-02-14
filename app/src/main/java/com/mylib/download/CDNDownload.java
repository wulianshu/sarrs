package com.mylib.download;

import com.chaojishipin.sarrs.download.download.DownloadInfo;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liuzhuo on 2016/1/21.
 */
public class CDNDownload implements IDown {

    protected final int timeout = 20*1000;
    protected long mCurrent = 0;
    protected long mOffset = 0;
    protected DownloadJob job;

    private long mProgress;
    private long mTotal;
    protected URL mDownUrl;
    private HttpURLConnection mHttpClient;
    private int responseCode;

    protected IDownloadManager.Progress mProgrezz;
    protected IDownload mRequest;
    protected DownloadQueue.DownloadCallback mCallback;
    private FileOperator mFileOperators;

    private AtomicBoolean isPause = new AtomicBoolean(false); //stop task
    private AtomicBoolean downloadFinish = new AtomicBoolean(false);
    private AtomicBoolean isStarting = new AtomicBoolean(false);
    protected DownloadTask mTask;

    public CDNDownload(IDownload request, DownloadQueue.DownloadCallback callback, DownloadJob job, DownloadTask task) {
        super();
        this.job = job;
        this.mProgress = request.getStartPosition();
        this.mTotal = request.getTotalSize();

        this.mRequest = request;
        this.mCallback = callback;
        this.mTask = task;
        this.mProgrezz = new IDownloadManager.Progress();
    }

    @Override
    public boolean download() {
        try{
            mFileOperators = new FileOperator(mRequest.getLoaclFile());
            if(!mFileOperators.isInited()){
                mCallback.onDownloadFailed(responseCode, IDownloadManager.DownloadExp.CODE_WRITEFILE, mFileOperators.getErrorStack(), mRequest);
                return false;
            }

            executeDownload();
            return true;
        }catch(Throwable e){
            e.printStackTrace();
            if(!isPause())
                mCallback.onDownloadFailed(responseCode, IDownloadManager.DownloadExp.CODE_NET_SERVER, "", mRequest);
        }finally {
            mFileOperators.close();//TODO close resource
            closeConnection();
        }
        return false;
    }

    protected HttpURLConnection getHttpClient()
            throws IOException {
        HttpURLConnection conn = (HttpURLConnection) mDownUrl.openConnection();
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Referer", mDownUrl.toString());
        String str = getHeader();
        if(!StringUtil.isEmpty(str))
            conn.setRequestProperty("User-Agent", str);
        return conn;
    }

    protected String getHeader(){
        return getHeader(DownloadInfo.MP4);
    }

    protected String getHeader(String type){
        String userAgent = PlayerUtils.getUserAgent(job.getEntity().getSite(), MoviesHttpApi.LeTvBitStreamParam.KEY_DOWNLOAD, type);

        if (!"letv".equals( job.getEntity().getSite()) && !"nets".equals(job.getEntity().getSite())) {
            int downloadposition = 0;
            if(job.getCurrentdownloadpositon() < job.getOutSiteDataInfo().getOutSiteDatas().size()){
                downloadposition = job.getCurrentdownloadpositon();
            }else if (job.getCurrent_streamlistposition() < job.getOutSiteDataInfo().getOutSiteDatas().size()){
                downloadposition = job.getCurrent_streamlistposition();
            } else{
                downloadposition = 0;
            }
            String header = job.getOutSiteDataInfo().getOutSiteDatas().get(downloadposition).getHeader();
            return header;
        } else {
            return userAgent;
        }
    }

    private HttpURLConnection getResponse(String url) throws Exception {

        this.mDownUrl = new URL(url);
        this.mHttpClient = getHttpClient();

        if(mProgress > 0 && mTotal > mProgress){
            mHttpClient.setRequestProperty("Range", "bytes=" + mProgress + "-"+ mTotal);
        }

        final boolean isFailed = processHeader(mHttpClient);
        if(isFailed){
            throw new HeaderException("process header error");
        }

        mHttpClient.connect();
        final long contentLen = mHttpClient.getContentLength();
        if(mProgress == 0){
            if(contentLen <= 0)
                ;//throw new RuntimeException("get content length error, " + contentLen);
            else{
                mTotal = contentLen;

                mProgrezz.progress = mProgress;
                mProgrezz.total = mTotal;
                mCallback.onFileTotalSize(mProgrezz, responseCode, mRequest); //TODO nofity file totalsize
            }
        }
        setStarting();

        printLog("[contentLength=" + contentLen + "]");

        return mHttpClient;
    }

    private void executeDownload() throws Exception {
        if(mTotal > 0 && mProgress == mTotal){
            doDownloadFinish(responseCode);
            return;
        }
        String publicParams = "";
        if (mRequest.addPublicParams()) {
            //TODO
        }
        final String url = job.getEntity().getDownloadUrl() + publicParams;

        InputStream inStream = null;
        try {
            printLog("[downloadStart] "+url);

            if(mProgress > mTotal){
                mProgress = 0;
                mTotal = 0;
            }
            if((mProgress == 0 || mProgress < mTotal) && !isPause()){

                printLog("[range:bytes=" + mProgress + "-" + mTotal + "]");
                HttpURLConnection response = getResponse(url);
                responseCode = response.getResponseCode();
                inStream = new BufferedInputStream(response.getInputStream());

                boolean isFinish = true;

                if(!mFileOperators.seekStartPosition(mProgress)){
                    throw new RuntimeException(mFileOperators.getErrorStack());
                }

                final int buffSize = 8192*2;
                byte[] buffer = new byte[buffSize];
                int offset = 0;

                while ((offset = inStream.read(buffer, 0, buffSize)) != -1) {
                    if(!progressCallback(responseCode, buffer, offset)){
                        isFinish = false;
                        break;
                    }
                }
                if(isFinish){
                    doDownloadFinish(responseCode);
                    printLog("[downloadEnd] "+url);
                }
            } else {
                if(!isPause()){
                    throw new Exception("wrong progress " + mProgress + ", " + mTotal);
                }
            }
            printLog(" downloading ResponseCode: " + responseCode);

        } catch(HeaderException he) {
            responseCode = 408; //request timeout responseCode.
        } catch(Exception e) {
            e.printStackTrace();
            responseCode = 408; //request timeout responseCode.
            if(!isPause()){
                throw e;
            }else{
                mCallback.onPauseDownload(mProgrezz, 0, mRequest);
            }
        } finally{
            if(inStream != null){
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected boolean processHeader(HttpURLConnection response) {
        return false;
    }

    protected long getRate(long offset){
        long time = System.currentTimeMillis();
        if(mCurrent == 0){
            mCurrent = time;
            mOffset = offset;
        }else{
            if(time - mCurrent < 1000) {
                mOffset += offset;
                return -1;
            }
        }
        int tmp = (int)(time - mCurrent);
        long rate;
        if(tmp <= 0)
            rate = 0;
        else
            rate = mOffset * 1000 / (time - mCurrent);
        mCurrent = time;
        mOffset = 0;

        return rate;
    }

    private boolean progressCallback(int responseCode, byte[] data, int offset) throws IOException {
        //TODO if isPause ?
        if(!isPause()){
            if(responseSuccess(responseCode)){
                mProgress+=offset;

                if(!mFileOperators.writeFile(data, offset)){
                    mCallback.onDownloadFailed(responseCode, IDownloadManager.DownloadExp.CODE_WRITEFILE, mFileOperators.getErrorStack(), mRequest);
                    return false;
                }

                if(mTotal <= 0){
                    mProgrezz.progress = 50;
                    mProgrezz.total = 100;
                } else {
                    mProgrezz.progress = mProgress;
                    mProgrezz.total = mTotal;
                }
                long rate = getRate(offset);
                if(rate == -1)
                    return true;
                onDownloading(rate);
                return true;
            } else {
                mCallback.onDownloadFailed(responseCode, IDownloadManager.DownloadExp.CODE_NET_SERVER, "", mRequest);
            }
        } else {

            mProgrezz.progress = mProgress;
            mProgrezz.total = mTotal;
            mCallback.onPauseDownload(mProgrezz, 0, mRequest);
            closeConnection();

            printLog("onPause[offset="+ offset +", progress="+ mProgress
                    + ", total=" + mTotal + ",isPause=" + isPause() +"], url=" + mRequest.getUrl());
        }
        return false;
    }

    protected void onDownloading(long rate){
        boolean bo = mCallback.onDownloading(mProgrezz, rate, responseCode, mRequest);
        if(!bo)
            mTask.pauseTask();
    }

    protected void doDownloadFinish(int responseCode) {
        setDownloadFinish();
        mCallback.onDownloadFinish(mProgrezz, responseCode, mRequest);
        mFileOperators.close();//TODO close resource
    }

    protected boolean isPause() {
        return isPause.get();
    }

    @Override
    public boolean pauseTask(){
        printLog(" =========pauseTask.url[" + mRequest.getUrl() + "]");
        if(!downloadFinish.get()){

            pause();

            return true;
        }
        return false;
    }

    protected void pause(){
        setPause();

        if(!isStart()){
            mProgrezz.progress = mProgress;
            mProgrezz.total = mTotal;
            mCallback.onPauseDownload(mProgrezz, 0, mRequest);
            closeConnection();
        }
    }

    protected void setPause(){
        isPause.set(true);
    }

    private void setStarting(){
        isStarting.set(true);
    }

    private boolean isStart(){
        return isStarting.get();
    }

    private void closeConnection() {
        try {
            if(mHttpClient != null){
                mHttpClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean responseSuccess(int responseCode){
        return responseCode == HttpURLConnection.HTTP_OK
                || responseCode == HttpURLConnection.HTTP_PARTIAL;
    }

    private void setDownloadFinish() {
        downloadFinish.set(true);
    }

    public IDownload getRequest() {
        return mRequest;
    }

    private void printLog(String log){
        LogUtil.l(log);
    }

    public static class FileOperator{

        private boolean inited = false;
        private StringBuffer sb = new StringBuffer();

        public FileOperator(File destFile) {
            try {
                init(destFile);
                inited = true;
            } catch (Exception e1) {
                e1.printStackTrace();
                sb.append(e1.toString()).append(" ").append(destFile.getAbsolutePath()).append(", 1\n");
                inited = false;
            }
        }

        public String getErrorStack(){
            return sb.toString();
        }

        public boolean isInited(){
            return inited;
        }

        private File tmpFile;
        private String destExtName;
        private RandomAccessFile rAccessFile;

        private void init(File destFile) throws Exception{
            File parentFile = destFile.getParentFile();
            if(parentFile.exists()){
                if(!parentFile.isDirectory()){
                    File tmp = new File(parentFile.getAbsolutePath() + "a");
                    parentFile.renameTo(tmp);
                    tmp.delete();
                    parentFile.mkdirs();
                }
            }else
                parentFile.mkdirs();

            if(!destFile.exists()){
                destFile.createNewFile();
            }
            printLog("[FileOperator().dest="+ destFile.getAbsolutePath() +"]");

            this.tmpFile = destFile;
            this.rAccessFile = new RandomAccessFile(tmpFile, "rwd");
        }

        public boolean reloadFile(){
            try{
                if(tmpFile.length() == 0)
                    return true;
                close();
                File f = new File(tmpFile.getAbsolutePath() + ".a");
                tmpFile.renameTo(f);
                f.delete();
                init(tmpFile);
                return true;
            }catch(Exception e){
                LogUtil.l(e.toString());
                sb.append(e.toString()).append(" ").append(tmpFile.getAbsolutePath()).append(", 2\n");
                return false;
            }
        }

        public boolean seekStartPosition(long position) {
            try {
                rAccessFile.seek(position);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                sb.append(e.toString()).append(", 3\n");
                return false;
            }
        }

        public boolean writeFile(byte[] buffer, int count) {
            try {
                rAccessFile.write(buffer, 0, count);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                sb.append(e.toString()).append(", 4\n");
                return false;
            }
        }

        public void close(){
            try {
                if(rAccessFile != null){
                    rAccessFile.close();
                    rAccessFile = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                sb.append(e.toString()).append(", 5\n");
            }
        }

        private void printLog(String log){
            LogUtil.l(log);
        }
    }
}
