package com.letv.http.exception;

import com.letv.http.LetvHttpLog;
import com.letv.http.parse.LetvMainParser;

/**
 * json数据在canParse方法判断为false的异常
 * 在此方法抛出{@link LetvMainParser#initialParse(String)}
 * */

public class JsonCanNotParseException extends Exception{

	private static final long serialVersionUID = 1L;
	private String logmsg ;
	
	public JsonCanNotParseException(String logmsg) {
		this.logmsg = logmsg ;
	}
	
	@Override
	public void printStackTrace() {
		LetvHttpLog.Err(logmsg);
	}
}
