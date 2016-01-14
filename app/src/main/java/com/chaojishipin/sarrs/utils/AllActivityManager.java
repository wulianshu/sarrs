package com.chaojishipin.sarrs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

import java.util.List;
import java.util.Stack;

/**
 * Created by zhangshuo on 2015/6/1.
 * Activity 管理类
 */
public class AllActivityManager {

    //Activity栈
    private static Stack<Activity> mActivityStack;

    private AllActivityManager() {
    }

    private static class ActivityManagerHolder {
        private static final AllActivityManager INSTANCE = new AllActivityManager();
    }

    /**
     * @return
     * 线程安全的单例模式
     */
    public static final AllActivityManager getInstance() {
        return ActivityManagerHolder.INSTANCE;
    };

    /**
     * 添加到Activity栈
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (null == mActivityStack) {
        mActivityStack = new Stack<Activity>();
    }
    mActivityStack.add(activity);
        LogUtil.e("xll"," jump from  add Activity "+activity.getClass().getSimpleName());
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     * @return
     */
    public Activity getCurrentActivity(){
        if (mActivityStack == null)
            return null;
        Activity activity = mActivityStack.lastElement();
        return activity;
    };

    /**
     * 结束当前Activity
     */
    public void finishActivity(){
        Activity actvity = mActivityStack.lastElement();
        finishActivity(actvity);
    }

    /**
     * 结束指定的Activity
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (null != activity) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    public void finishAllActivity() {
        int size = mActivityStack.size();
        LogUtil.e("xll","jump from share finish detailActivity before");
        LogUtil.e("xll","jump from share finish size "+mActivityStack.size());
        for (int i = 0; i < size; i++) {
            LogUtil.e("xll", "jump from share finish name " + mActivityStack.get(i).getCallingActivity());
            mActivityStack.get(i).finish();

        }
        mActivityStack.clear();
    }

    public void AppExit() {
        finishActivity();
        ChaoJiShiPinApplication application = ChaoJiShiPinApplication.getInstatnce();
        ActivityManager activityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(application.getPackageName());
    }

    public boolean isExistActivy(String activityName){
        if (activityName != null && mActivityStack != null)
        {
            for (Activity mActivity : mActivityStack
                 ) {
                if (mActivity.toString().equals(activityName))
                    return true;
            }
        }

        return false;
    }
}
