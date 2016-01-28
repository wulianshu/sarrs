package com.mylib.download;

/**
 * 
 * @author luxu
 *
 */
public class RequestConstant {

	public enum HttpMode {
		GET, POST
	}

	public enum Priority {
		LOW, NORMAL, HIGH
	}

	public enum HttpType {
		HTTP, HTTPS
	}

	public enum DataType {
		TEXT, BYTE, JSON, XML
	}
	
	/**
	 * 
	 * @author luxu
	 *
	 */
	public enum CacheStatus {
		YES, NO
	}
	
	
}
