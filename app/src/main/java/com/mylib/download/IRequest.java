package com.mylib.download;

import com.mylib.download.RequestConstant.DataType;
import com.mylib.download.RequestConstant.HttpMode;
import com.mylib.download.RequestConstant.HttpType;
import com.mylib.download.RequestConstant.Priority;

public interface IRequest {

	public String getUrl();

	public String getPost();

	public HttpMode getHttpMode();

	public Priority getPriority();

	public DataType getDataType();
	
	/**
	 * https or http
	 * @return
	 */
	public HttpType getHttpType();
}
