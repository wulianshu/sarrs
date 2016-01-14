package com.chaojishipin.sarrs.activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.Utils;

public class ChaojishipinHtmlActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener{


    private Button mBack;
    private WebView mWeb;
    private String mUrl="http://www.chaojishipin.com/agreement.html";

    void init(){
       // view 获取
        mBack=(Button)findViewById(R.id.register_activity_notify_back);
        mBack.setOnClickListener(this);
        mWeb=(WebView)findViewById(R.id.html_activity_web);
        mWeb.loadUrl(mUrl);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_activity_notify_back:
                 ChaojishipinHtmlActivity.this.finish();
                break;

        }
    }


    @Override
    protected void handleInfo(Message msg) {

    }


    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.htmlactivity,null);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(false);
        init();
    }



        @Override
        protected void onDestroy() {
            super.onDestroy();
            Utils.destroyWebView(mWeb);
        }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}