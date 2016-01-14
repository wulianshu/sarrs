package com.letv.http;

import java.io.IOException;

import com.letv.http.impl.LetvHttpBaseParameter;

public interface LetvHttpBaseHandler {
	
	public String doGet(LetvHttpBaseParameter<?, ?, ?> params) throws IOException;
	
	public String doPost(LetvHttpBaseParameter<?, ?, ?> params) throws IOException;
	
}
