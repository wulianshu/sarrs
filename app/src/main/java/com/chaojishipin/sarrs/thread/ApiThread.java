package com.chaojishipin.sarrs.thread;


import com.chaojishipin.sarrs.async.RequestResultListener;
import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.download.http.api.MoviesHttpApi;
import com.chaojishipin.sarrs.http.parser.HtmlParser;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvBaseParser;



/**
 *  工作线程
 * Created by xll on 2015/12/05
 */
public class ApiThread<T extends LetvBaseBean,D> implements Runnable {

    private String referUrl;// 网页地址
    private int mtaskId;
    private LetvBaseParser<T,D> mParser;
    private String mUrl;// 视频流地址
    private String mAgent;
    // 存放执行流地址结果list

    public ApiThread(int taskId, LetvBaseParser<T, D> parser, String url, String refererUrl,String userAgent){
        this.referUrl=refererUrl;
        this.mtaskId=taskId;
        this.mUrl=url;
        this.mAgent=userAgent;
        this.mParser=parser;
    }


    @Override
    public void run() {
        LogUtil.e("xll", "NEW thread : " + Thread.currentThread().getName() + " Start");
        // exe task
        LetvDataHull obj =  MoviesHttpApi.requestHtmlData(new HtmlParser(), mUrl, referUrl, mAgent);
        if (obj.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
            listener.onRequestSuccess(obj.getUpdataId(),(HtmlDataBean)obj.getDataEntity());
        } else{
            listener.onRequestFailed();
        }
        LogUtil.e("xll","NEW thread : "+Thread.currentThread().getName() + " End.");
    }
    RequestResultListener<HtmlDataBean> listener=new RequestResultListener<HtmlDataBean>() {
        @Override
        public boolean onRequestFailed() {
            return false;
        }

        @Override
        public void onRequestSuccess(int updateId, HtmlDataBean result) {
            String apiContent = result.getHtmlData();
            LogUtil.e("xll", "NEW thread work thread apicontent ok ！ "+"\n"+apiContent);
            ThreadPoolManager.getInstanse().addResultList(apiContent);
            LogUtil.e("xll", "NEW pool resultList size " +ThreadPoolManager.getInstanse().getResultList().size());
        }

        @Override
        public void onPreRequest() {

        }
    };
   /* @Override
    public boolean onRequestFailed() {
        LogUtil.e("xll","NEW thread work thread apicontent failed ");
        return false;
    }

    @Override
    public void onPreRequest() {

    }

    @Override
    public void onRequestSuccess(int updateId, HtmlDataBean result) {


    }*/


    @Override
    public String toString(){
        return this.referUrl;
    }
}




