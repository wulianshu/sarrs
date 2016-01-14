package com.letv.http.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

/**
 * 解析器接口
 * */
public abstract class LetvBaseParser<T extends LetvBaseBean, D> {
	
	/**
	 * 错误code
	 * */
	private int errorMsg ;
	
	/**
	 * 服务信息
	 * */
	private String message ;
	
	/**
	 * 数据来源，区别解析,默认为0
	 * */
	private int from ;
	
	public LetvBaseParser(int from){
		this.from = from ;
	}
	
	public T initialParse(String data) throws JsonCanNotParseException, DataIsNullException, ParseException , DataIsErrException, DataNoUpdateException{
		if (TextUtils.isEmpty(data)) {
			throw new DataIsNullException("json string is null");
		}
		if (canParse(data)) {
			D d = null ;
			try{
				d = getData(data);
			}catch(Exception e){
				throw new DataIsErrException("Data is Err");
			}
			if(d != null){
				T t;
				try {
					t = parse(d);
					return t;
				} catch (Exception e) {
					throw new ParseException("Parse Exception");
				}
			}else{
				throw new ParseException("Data is Err");
			}
		} else {
			boolean hasUpdate = hasUpdate() ;
			if(!hasUpdate){
				throw new DataNoUpdateException("data has not update");
			}else{
				throw new JsonCanNotParseException("canParse is return false");
			}
		}
	}

	public abstract T parse(D data) throws Exception;
	
	/**
	 * 针对不同的接口类型（如：移动端接口，主站接口，支付接口等）进行不同实现，
	 * 如果独立接口，请实现为返回  true，否则不会进入解析方法，并抛出JsonCanNotParseException
	 * */
	protected abstract boolean canParse(String data);
	
	/**
	 * 针对不同的接口类型，给parse方法吐出不同的数据
	 * */
	protected abstract D getData(String data) throws Exception;
	
	/**
	 * 得到错误信息id
	 * */
	public int getErrorMsg(){
		return errorMsg ;
	}
	
	/**
	 * 设置错误信息的id
	 * */
	protected void setErrorMsg(int errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/**
	 * 设置服务器消息
	 * */
	protected void setMessage(String message){
		this.message = message ;
	}
	
	/**
	 * 得到服务器信息
	 * */
	public String getMessage(){
		return this.message ;
	}
	
	/**
	 * 得到解析器数据来源，默认为0，不进行区别解析
	 * */
	public int getFrom() {
		return from;
	}
	
	/**
	 * 判断接口是否有更新
	 * */
	public boolean hasUpdate(){
		return true ;
	}
	
		}
