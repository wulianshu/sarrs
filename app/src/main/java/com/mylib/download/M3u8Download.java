package com.mylib.download;

import com.chaojishipin.sarrs.download.download.*;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhuo on 2016/1/21.
 */
public class M3u8Download extends CDNDownload {

    private DownloadEntity downloadEntity;
    private File dir;
    private double currentLoadedSize = 0;
    private String url;
    private String redirectHost;
    private int redirectPort;
    private String redirectScheme;

    public M3u8Download(IDownload request, DownloadQueue.DownloadCallback callback, DownloadJob job, DownloadTask task) {
        super(request, callback, job, task);
    }

    @Override
    public boolean download(){
        boolean bo = startDownload();
        if(!bo)
            mCallback.onDownloadFailed(0, 0, "download m3u8 fail", mRequest);

        return bo;
    }

    public boolean startDownload() {
        downloadEntity = job.getEntity();
        String path = job.getDestination() + "/" + downloadEntity.getSaveName();
        dir = new File(path);
        if(dir.exists() && dir.isDirectory())
            ;
        else
            dir.mkdirs();
        String fileName = downloadEntity.getSaveName();
        url = downloadEntity.getDownloadUrl();
        DataUtils.getInstance().deleteFolder(path + ".mp4");

        int downloadM3u8Status = downLoadTopM3u8();
        if (downloadM3u8Status == DOWNLOAD_SUCCESS){
            File temp_m3u8 = new File(dir, ".temp_m3u8");
            File target_m3u8_file = new File(dir,fileName+".m3u8");
            LogUtil.e("wulianshu", "下载的m3u8的地址为：" + target_m3u8_file.getAbsolutePath());
            File temp_segments = new File(dir, "temp");
            if(currentLoadedSize == 0L){
                currentLoadedSize = initSize(dir);
            }
            try {
                temp_m3u8.createNewFile();
                target_m3u8_file.createNewFile();
                int currentIndex = 0;
                downloadM3u8Status = downloadFile(temp_m3u8,url,false, 0);
                if(downloadM3u8Status == DOWNLOAD_SUCCESS){
                    if(temp_segments.exists()){
                        try {
                            FileReader reader = new FileReader(temp_segments);
                            BufferedReader br = new BufferedReader(reader);
                            // 读取文件
                            String line;
                            while((line = br.readLine())!= null){
                                if(line.startsWith("current_index=")){
                                    currentIndex = Integer.valueOf(line.split("=")[1]);
                                    break;
                                }
                            }
                            br.close();
                        }	catch (Exception e){
                            temp_segments.delete();
                            currentIndex = 0;
                        }
                    }
                    List<String> segments = createM3u8LocalCopy(temp_m3u8,target_m3u8_file);
                    if(segments.isEmpty()){
                        mCallback.onDownloadFailed(200, 0, "m3u8索引为空", mRequest);
                        return false;
                    }
                    if(DOWNLOAD_SUCCESS == downloadSegments(segments, currentIndex)) {
                        doDownloadFinish(200);
                        return true;
                    }
                    return false;
                }
            } catch (Exception e) {
                if (e.toString().contains("No space left on device")) {
                    LogUtil.e("wulianshu","手机内存不足 M3U8");
                    job.setExceptionType(DownloadJob.SD_SPACE_FULL);
                } else if (e.toString().contains("java.io.FileNotFoundException")
                        || e.toString().contains("java.io.IOException: write failed: EIO (I/O error)")) {
                    job.setExceptionType(DownloadJob.NO_SD);// 没有sdcard，或者sdcard拔出或者存储器模式
                }
                e.printStackTrace();
            }
        }

        if (downloadM3u8Status == DOWNLOAD_SUCCESS)
            return true;
        return false;
    }

    /**
     * 下载顶级m3u8文件。
     * 现在只发现cntv是二级m3u8格式
     */
    public int downLoadTopM3u8(){
        int result = DOWNLOAD_SUCCESS;
        if(downloadEntity.getSite().equals("cntv")){
            File top_m3u8 = new File(dir, ".top_m3u8");
            try {
                result = downloadFile(top_m3u8,url,false, 0);
                if(result == DOWNLOAD_SUCCESS){
//					LogUtils.d(TAG,"topm3u8 file downloaded" );

                    ArrayList<String> ret = new ArrayList<String>();
                    FileReader fr = new FileReader(top_m3u8);
                    BufferedReader br = new BufferedReader(fr);

                    String tempString;
                    String hostUrl;
                    try {
                        URI m3u8Uri = new URI(url);
                        hostUrl = URIUtils.createURI(m3u8Uri.getScheme()
                                , m3u8Uri.getHost()
                                , m3u8Uri.getPort()
                                , null, null, null).toString();
//						LogUtils.d(TAG,"hostUrl:"+hostUrl );
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fr.close();
                        br.close();
                        throw new IOException("unavailable m3u8 download url");
                    }
                    while((tempString = br.readLine()) != null){
                        if(!tempString.startsWith("#")){
                            if(tempString.startsWith("/")){
                                tempString = tempString.substring(1);
                                tempString = new StringBuilder(hostUrl).append(tempString).toString();
                            }
                            ret.add(tempString);
                        }
                        else{
                        }
                    }
                    fr.close();
                    br.close();
                    if(null!=ret && ret.size() > 0){
                        String m3u8url = ret.get(ret.size()-1);//取出最后一个，清晰度最高的。
                        url =m3u8url;
                    }
                }else{

                }
            } catch (Exception e) {
                if (e.toString().contains("No space left on device")) {
                    LogUtil.e("wulianshu","手机内存不足 M3U2");
                    job.setExceptionType(DownloadJob.SD_SPACE_FULL);
                } else if (e.toString().contains("java.io.FileNotFoundException")
                        || e.toString().contains("java.io.IOException: write failed: EIO (I/O error)")) {
                    job.setExceptionType(DownloadJob.NO_SD);// 没有sdcard，或者sdcard拔出或者存储器模式
                }
                e.printStackTrace();
            }
        }
        return result;
    }

    private int downloadSegments(List<String> segments, int lastCompleteIndex) throws IOException {
        mProgrezz.total = segments.size();
        job.setProgress(job.initProgress());
        if(job.getProgress()==100 || lastCompleteIndex > segments.size()-1){
            return DOWNLOAD_SUCCESS;
        }
        job.setTotalSize(segments.size());
        int downloadSegmentStatus = 0;
        for(int i = lastCompleteIndex; i < segments.size(); i++){
            if(isPause())
                break;
            String segment = segments.get(i);
            File segFile = new File(dir, String.valueOf(i));
            try {
                downloadSegmentStatus = downloadFile(segFile, segment,true, i);
                if(downloadSegmentStatus != DOWNLOAD_SUCCESS || isPause()){
                    FileWriter temp_fw = new FileWriter(new File(dir,"temp"),false);
                    temp_fw.write(String.format("current_index=%d", i));
                    temp_fw.close();
                    return downloadSegmentStatus;
                }
            } catch (IOException e) {
                FileWriter temp_fw = new FileWriter(new File(dir,"temp"),false);
                temp_fw.write(String.format("current_index=%d", i));
                temp_fw.close();
                e.printStackTrace();
                throw e;
            }
            job.setDownloadedSize(i + 1);
        }
        FileWriter temp_fw = new FileWriter(new File(dir,"temp"),false);
        temp_fw.write(String.format("current_index=%d", segments.size()));
        temp_fw.close();
        return downloadSegmentStatus;
    }

    private List<String> createM3u8LocalCopy(File input, File output) throws IOException {
        ArrayList<String> ret = new ArrayList<String>();
        FileReader fr = new FileReader(input);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(output);
        String tempString;
        StringBuilder targetContent = new StringBuilder();
        StringBuilder downloadSegments = new StringBuilder();
        int count = 0;
        String parentUrl;
        String hostUrl;
        try {
            URI m3u8Uri = new URI(url);
            parentUrl = m3u8Uri.resolve(".").toString();
            String host = m3u8Uri.getHost();
            if (redirectHost != null && redirectHost.length() > 0)
            {
                hostUrl = URIUtils.createURI(redirectScheme
                        , redirectHost
                        , redirectPort
                        ,null,null,null).toString();
                int index = parentUrl.indexOf(host) + host.length();
                String subString = parentUrl.substring(index);
                if (subString.startsWith("/") && hostUrl.endsWith("/"))
                {
                    parentUrl = hostUrl + subString.substring(1);
                }else {
                    parentUrl = hostUrl + subString;
                }

            }else {
                hostUrl = URIUtils.createURI(m3u8Uri.getScheme()
                        , m3u8Uri.getHost()
                        , m3u8Uri.getPort()
                        ,null,null,null).toString();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fw.close();
            fr.close();
            br.close();
            throw new IOException("unavailable m3u8 download url");
        }
        while((tempString = br.readLine()) != null){
            if(!tempString.startsWith("#") && !StringUtil.isEmpty(tempString)){
                if(tempString.startsWith("/")){
                    tempString = tempString.substring(1);
                    tempString = new StringBuilder(hostUrl).append(tempString).toString();
                }else if(!tempString.startsWith("http://")){
                    tempString = new StringBuilder(parentUrl).append(tempString).toString();
                }
                ret.add(tempString);
                downloadSegments.append(tempString).append("\n");
                targetContent.append(String.format("file:/%s/%s", dir.getAbsolutePath(), String.valueOf(count))).append("\n");
                count ++;
            }
            else{
                targetContent.append(tempString).append("\n");
            }
        }
        fw.write(targetContent.toString());
        fw.close();
        fr.close();
        br.close();
        return ret;
    }
    private long initSize(File dir){
        return 0L;
    }

    protected String getHeader(){
        return getHeader(DownloadInfo.M3U8);
    }

    private int downloadFile(File output, String url, boolean m3u8OrSegment, int index) throws IOException {
        int downloadStatus = 0;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
        HttpConnectionParams.setSoTimeout(params, 15 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192 * 5);
        HttpGet httpGet = new HttpGet(url);
        RandomAccessFile randomFile = new RandomAccessFile(output, "rw");
        httpGet.addHeader("Range", "bytes=" + randomFile.length() + "-");

        String header = getHeader();
        if (!StringUtil.isEmpty(header)) {
            httpGet.addHeader("User-Agent", header);
        }

        HttpClient client = new DefaultHttpClient(params);
        HttpContext httpContext = new BasicHttpContext();
        HttpResponse response=client.execute(httpGet, httpContext);
        if (!m3u8OrSegment)
        {
            HttpHost targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            redirectHost = targetHost.getHostName();
            redirectPort = targetHost.getPort();
            redirectScheme = targetHost.getSchemeName();
        }

        HttpEntity entity = response.getEntity();

        StatusLine statusLine = response.getStatusLine();
        int respCode = statusLine.getStatusCode();
        if(HttpURLConnection.HTTP_PARTIAL == respCode){
            randomFile.seek(randomFile.length());
        }
        else if(HttpURLConnection.HTTP_OK == respCode){
        }
        else if(416 == respCode){//请求range超出范围
            if(!m3u8OrSegment){//如果是m3u8文件，删除，重新下载
                output.delete();
                randomFile.close();
                return DOWNLOAD_FILE_ERROR;
            }
        }
        else{
            randomFile.close();
            return DOWNLOAD_URL_INVALID;
        }
        long length = entity.getContentLength();

        InputStream in = entity.getContent();
        if(in == null){
            if(randomFile != null)
                randomFile.close();
            return DOWNLOAD_URL_INVALID;
        }
        int blockSize = 2024;
        byte[] buffer = new byte[blockSize];

        int readCount = 0; // 已经成功读取的字节的个数
        int readLen = 0;
        while (readLen >= 0 && !isPause()) {
            readLen = in.read(buffer, 0, blockSize);
            if (readLen > 0) {
                randomFile.write(buffer, 0, readLen);
                readCount += readLen;
                if(m3u8OrSegment)
                    onDownloading(index, readLen);
            }
        }
        if (!isPause()) {
            job.setmM3u8DownloadedSize(readCount);
            job.setM3u8Rate();
        }
        downloadStatus = DOWNLOAD_SUCCESS;

        randomFile.close();
        httpGet.abort();

        if(isPause()){
            mCallback.onPauseDownload(mProgrezz, 200, mRequest);
        }
        return downloadStatus;
    }

    private void onDownloading(int index, int offset){
        mProgrezz.progress = index;
        long rate = getRate(offset);
        if(rate < 0)
            return;
        super.onDownloading(rate);
    }

    @Override
    protected void doDownloadFinish(int responseCode){
        if(isPause())
            return;
        super.doDownloadFinish(responseCode);
    }

    @Override
    protected void pause(){
        setPause();
    }
}
