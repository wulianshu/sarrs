package com.chaojishipin.sarrs.widget;

import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class VideoEnabledWebView extends WebView 
{ 
    public class JavascriptInterface
    {
        @android.webkit.JavascriptInterface
        public void notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
        {
            // This code is not executed in the UI thread, so we must force that to happen
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (videoEnabledWebChromeClient != null)
                    {
                        videoEnabledWebChromeClient.onHideCustomView();
                    }
                }
            });
        }
    }
    public interface ToggledFullscreenCallback 
    { 
        public void toggledFullscreen(boolean fullscreen); 
    }     
    private VideoEnabledWebChromeClient videoEnabledWebChromeClient; 
    private boolean addedJavascriptInterface;    
    public VideoEnabledWebView(Context context) 
    { 
        super(context); 
        addedJavascriptInterface = false; 
    }    
    public VideoEnabledWebView(Context context, AttributeSet attrs) 
    { 
        super(context, attrs); 
        addedJavascriptInterface = false; 
    }    
    public VideoEnabledWebView(Context context, AttributeSet attrs, int defStyle) 
    { 
        super(context, attrs, defStyle); 
        addedJavascriptInterface = false; 
    }     
    /** 
     * Pass only a VideoEnabledWebChromeClient instance. 
     */ 
    @Override 
    @SuppressLint ("SetJavaScriptEnabled") 
    public void setWebChromeClient(WebChromeClient client) 
    { 
        getSettings().setJavaScriptEnabled(true); 
 
        if (client instanceof VideoEnabledWebChromeClient) 
        { 
            this.videoEnabledWebChromeClient = (VideoEnabledWebChromeClient) client; 
        } 
 
        super.setWebChromeClient(client); 
    }     
    @Override 
    public void loadData(String data, String mimeType, String encoding) 
    { 
        addJavascriptInterface(); 
        super.loadData(data, mimeType, encoding); 
    }     
    @Override 
    public void loadDataWithBaseURL(String baseUrl, String data, 
                                    String mimeType, String encoding, 
                                    String historyUrl) 
    { 
        addJavascriptInterface(); 
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl); 
    }     
    @Override 
    public void loadUrl(String url) 
    { 
        addJavascriptInterface(); 
        super.loadUrl(url); 
    }     
    @Override 
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) 
    { 
        addJavascriptInterface(); 
        super.loadUrl(url, additionalHttpHeaders); 
    }     
    private void addJavascriptInterface()
    {
        if (!addedJavascriptInterface)
        {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(new JavascriptInterface(), "_VideoEnabledWebView"); // Must match Javascript interface name of VideoEnabledWebChromeClient

            addedJavascriptInterface = true;
        }
    }
}