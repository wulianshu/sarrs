package com.letv.http.impl;

import java.net.HttpURLConnection;

public interface LetvHttpParameterCallback {

	public void proRequest(HttpURLConnection connection);
	
	public void laterRequest(HttpURLConnection connection);
}
