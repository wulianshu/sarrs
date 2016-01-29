package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.uploadstat.UmengPagePath;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.Utils;

/**
 * Created by wulianshu on 2015/8/31.
 */
public class ChaojishipinAboutUsActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener {
    private String curVerName;
    private TextView tv_curvername;
    private RelativeLayout relativeLayout;
    private ImageView img_back;
    private TextView tv_title;
    public ChaojishipinAboutUsActivity() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chaojishipin_aboutus);
        setTitleBarVisibile(false);
        tv_curvername = (TextView) findViewById(R.id.about_us_tv_version);
        relativeLayout = (RelativeLayout) findViewById(R.id.mtitlebar);
        img_back = (ImageView) relativeLayout.findViewById(R.id.baseactivity_left_btn);
        tv_title = (TextView) relativeLayout.findViewById(R.id.baseactivity_title);
        img_back.setImageResource(R.drawable.selector_ranklistdetail_titlebar);
        img_back.setOnClickListener(this);
        curVerName = getIntent().getStringExtra("curVerName");
        tv_curvername.setText(curVerName+getResources().getString(R.string.setting_version));
        tv_title.setText(getResources().getString(R.string.about_us_title));

    }

    @Override
    protected void onResume() {
        UmengPagePath.beginpage(ConstantUtils.AND_ABOUT_US,this);

        super.onResume();
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.baseactivity_left_btn:
                this.finish();
                break;
        }
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    @Override
    protected void onPause() {
        UmengPagePath.endpage(ConstantUtils.AND_ABOUT_US,this);
        System.out.println("onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("onstop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
