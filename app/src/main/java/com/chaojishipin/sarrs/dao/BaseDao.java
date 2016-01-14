package com.chaojishipin.sarrs.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.chaojishipin.sarrs.bean.SearchKeyWords;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.letv.http.bean.LetvBaseBean;

import java.lang.Thread.UncaughtExceptionHandler;

public abstract class BaseDao<T extends LetvBaseBean> {
	protected DBHelper helper;
	
	protected static final DaoThread mIOThread;
	
	static {
		mIOThread = new DaoThread();
		mIOThread.setName("dao-thread");
		mIOThread.setPriority(Thread.NORM_PRIORITY-1);
		mIOThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
			}
		});
		mIOThread.start();
	}
	
	 public BaseDao(Context context) {  
	        helper = new DBHelper(context);
	    }  
	 public abstract void delete(String keyWords);
	 public abstract void delAll();
	 public abstract void save(T bean);
//	public abstract void getAll();
	 public void update(T prior, T now){
		 
	 }
	 public void doInBackground(Runnable r) {
		 mIOThread.post(r);
	};
	protected synchronized SQLiteDatabase safelyGetDataBase(){
		LogUtil.i("database", "open");
		return DatabaseManager.getInstance().openDatabase();
	}
	protected synchronized void safelyCloseDataBase(){
		LogUtil.e("database", "close");
		DatabaseManager.getInstance().closeDatabase();
	}
	
}
