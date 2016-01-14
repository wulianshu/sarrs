package com.chaojishipin.sarrs.thread;


import com.chaojishipin.sarrs.async.RequestHtmlDataTask;
import com.chaojishipin.sarrs.download.http.parser.HtmlParser;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *  线程池管理类
 * Created by xll on 2015/12/05.
 */
public class ThreadPoolManager {

  public static ThreadPoolManager instanse;

  private long timeout=2000L;

  // 定义列表存放多线程执行结果集合
    public  List<String> resultList;

    ThreadPoolExecutor  executorpool;




  TimeUnit timeUnit = TimeUnit.MILLISECONDS;// 时间单位
   /**
    *  线程池初始化
    * */
   public static final ThreadPoolManager  getInstanse(){
       if(instanse==null){
           instanse=new ThreadPoolManager();
       }
       return instanse;

   }

 /**
  *  创建线程池
  * */
  public void createPool(){
      LogUtil.e("xll","NEW create pool ");


      resultList=new ArrayList<>();
      ThreadFactory threadFactory = Executors.defaultThreadFactory();
      RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
      executorpool = new ThreadPoolExecutor(2, 4, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);
  }

/**
 *   关闭线程池
 * */

 public void shutdown(){
     if(executorpool!=null&&!executorpool.isShutdown()){
         executorpool.shutdownNow();
         if(resultList!=null)
         LogUtil.e("xll","NEW thread pool shut down result size "+resultList.size());


     }
 }

    /**
     *  当前线程池任务是否都已经执行完毕
     * */

   public boolean isTerminated(){

       return executorpool==null?true:executorpool.isTerminated();

   }


    /**
     *   添加设置timeout
     *   @param tasksList 任务列表
     *   @param timeOut   单个任务超时
     * */
    public void exeTaskTimOut(List<String> tasksList,String refer,String userAgent,long timeOut) throws InterruptedException,ExecutionException
    {
        if(tasksList!=null&&tasksList.size()>0){
            for(int i=0; i<tasksList.size(); i++){
                if(executorpool!=null){
                    Future<?> future= executorpool.submit(new ApiThread(i,new HtmlParser(),tasksList.get(i),refer,userAgent));
                try{
                    future.get(timeOut, timeUnit);
                    LogUtil.e("xll", "NEW thread future " + i + " task normal next ");
                }catch (TimeoutException e){
                    //任务超时取消当前任务，执行下一个任务
                    LogUtil.e("xll","NEW thread future "+i+" task timeout next ");
                    future.cancel(true);
                    continue;
                 }
                }
            }
        }
    }

    /**
     *   添加设置timeout
     *   @param apicontent apicontent 内容
     *   @param stream   流地址
     *   @param timeOut 超时
     * */
    public void  exeTaskTimOut(android.os.Handler mHandler,String apicontent,String stream,String requestUrl,String te,String ts,boolean isHasrule,long timeOut) throws InterruptedException,ExecutionException
    {
                if(executorpool!=null){
                    Future<?> future= executorpool.submit(new JSThread(mHandler,requestUrl,apicontent,stream,isHasrule,ts,te));
                    try{
                        future.get(timeOut, timeUnit);
                        LogUtil.e("xll", "NEW thread future task normal next ");
                    }catch (TimeoutException e){
                        //任务超时取消当前任务，执行下一个任务
                        LogUtil.e("xll", "NEW thread future task timeout next ");
                        future.cancel(true);
                    }
        }
    }
  /**
   *  统计线程执行结果
   *  @param apiContent
   * */

    public synchronized void addResultList(String apiContent){

        if(resultList!=null){
            resultList.add(apiContent);
        }

    }


    public List<String> getResultList(){
        return resultList;
    }


}








class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        LogUtil.e("xll","reject thread "+r.toString() + " is rejected");
    }




}