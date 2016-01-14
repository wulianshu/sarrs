package com.chaojishipin.sarrs.fragment.videoplayer.httpd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;



public class M3u8Httpd extends NanoHTTPD {

	public M3u8Httpd(int port) {
		super(port);
	}
    @Override
    public Response serve(String uri, Method method, 
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(uri.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new NanoHTTPD.Response(Response.Status.OK, "text/plain", fis);
      }
}
