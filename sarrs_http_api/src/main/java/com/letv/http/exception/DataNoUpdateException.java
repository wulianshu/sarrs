package com.letv.http.exception;

import com.letv.http.LetvHttpLog;
import com.letv.http.parse.LetvMainParser;

/**
 * 请求数据为空的异常
 * 在此方法抛出{@link LetvMainParser#initialParse(String)}
 * */

public class DataNoUpdateException extends Exception{

	private static final long serialVersionUID = 1L;
	private String logmsg ;
	
	public DataNoUpdateException(String logmsg) {
		this.logmsg = logmsg ;
	}
	
	@Override
	public void printStackTrace() {
		LetvHttpLog.Err(logmsg);
	}
}
