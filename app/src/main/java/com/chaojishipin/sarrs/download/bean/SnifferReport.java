package com.chaojishipin.sarrs.download.bean;


import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.async.PlaySnifferReportTask;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;

public class SnifferReport implements LetvBaseBean {
    
    /**
     * zhangshuo 2014年10月11日 上午9:37:44
     */
    private static final long serialVersionUID = 1L;
    
    private boolean isReport;
    
    /**
     * 当前站点
     */
    private String site;

    /**
     * 专辑ID
     */
    private String aid;
    
    /**
     * 嗅探状态
     */
    private String state;
    
    /**
     * 播放链接
     */
    private String playUrl;
    
    /**
     * 嗅探后的文件地址
     */
    private String filepath;
    
    /**
     * 缓冲时长
     */
    private String waiting;
    
    private String definition;
    
    private String duration;

    /**
     * 不传或传0表示播放反馈，1表示下载反馈
     */
    private String download;
    
    private ArrayList<String> mStateList;

    public boolean isReport() {
        return isReport;
    }

    public void setReport(boolean isReport) {
        this.isReport = isReport;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getState() {
        try {
            String state = "";
            if (null != mStateList) {
                int size = mStateList.size();
                StringBuffer stateBuffer = new StringBuffer();
                for (int i = 0; i < size; i++) {
                    stateBuffer.append(mStateList.get(i));
                    if (i < size - 1) {
                        stateBuffer.append(",");
                    }
                }
                state = stateBuffer.toString();
            }
            return state;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getWaiting() {
        return waiting;
    }

    public void setWaiting(String waiting) {
        this.waiting = waiting;
    }
    
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
    
    public ArrayList<String> getmStateList() {
        return mStateList;
    }

    public void setmStateList(ArrayList<String> mStateList) {
        this.mStateList = mStateList;
    }
    
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
    
    
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public void resetData() {
        isReport = false;
        site = "";
        aid = "";
        state = "";
        playUrl = "";
        filepath = "";
        waiting = "";
        mStateList = null;
    }
    
    public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}
	/**
	 * 在主线程中上报
	 */
	public void startReport(){
		 if (null != this && PlayerUtils.isOutSite(site)){
			 PlaySnifferReportTask mSnifferReportTask = new PlaySnifferReportTask(ChaoJiShiPinApplication.getInstatnce());
			 mSnifferReportTask.setmSnifferReport(this);
		     mSnifferReportTask.start();
		 }
		
	}
	
	/**
	 * 在子线程中上报
	 */
	public void startReportOnBackgroundThread(){
		try {
            AllActivityManager.getInstance().getCurrentActivity().runOnUiThread(new Runnable() // 工作线程刷新UI
            { //这部分代码将在UI线程执行，实际上是runOnUiThread post Runnable到UI线程执行了
                @Override
                public void run() {
                    startReport();
			    }  
			});
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
