package com.mylib.download;

public class HeaderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HeaderException() {
		super();
	}

	public HeaderException(String msg) {
		super(msg);
	}

	public HeaderException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public HeaderException(Throwable cause) {
		super(cause);
	}
}
