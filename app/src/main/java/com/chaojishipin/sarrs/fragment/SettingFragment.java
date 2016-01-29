package com.chaojishipin.sarrs.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaojishipinAboutUsActivity;
import com.chaojishipin.sarrs.activity.SettingActivity;
import com.chaojishipin.sarrs.adapter.StorageAdapter;
import com.chaojishipin.sarrs.bean.StorageBean;
import com.chaojishipin.sarrs.bean.UpgradeInfo;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.GetFileSizeUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.StoragePathsManager;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.UpgradeHelper;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.SdcardSettingsPopWindow;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulianshu on 2015/8/30.
 */
public class SettingFragment extends ChaoJiShiPinBaseFragment implements View.OnClickListener {
    private View contentView;
    private RelativeLayout clear_catch;
    private RelativeLayout version_upgrad;
    private RelativeLayout abous_us;
    private TextView tv_version;
    private TextView tv_catch;
    private ImageView circle_Imageview;
    private TextView setting_sd_content;
    private UpgradeHelper mUpgradeHelper;

    //upgrade
    private String sVerName; //服务器端版本名
    private String loadUrl;// 下载url
    private String curVerName;// 本地版本名
    private Context mContext;
    StorageAdapter adapter;
    SdcardSettingsPopWindow popWindow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.fragment_setting_layout, null);
        initView();
        setListenner();
        initData();

        if (NetWorkUtils.isNetAvailable()) {
            //有网络才能获取到服务器版本信息
            UpgradeHelper.requestUpgradeData(new RequestUpgradeListener());
        }
        return contentView;
    }



    @Override
    public void onResume() {
        Log.i("SettingFragment", "SettingFragment");
        tv_catch.setText(getCatchSize());
        super.onResume();
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    /**
     * 获取缓存的大小
     *
     * @return
     */
    private String getCatchSize() {
        File file = StorageUtils.getCacheDirectory(mContext.getApplicationContext());
        String fileOrFilesSize = GetFileSizeUtil.getAutoFileOrFilesSize(file.getAbsolutePath().toString());
//      File apkFile = new File(Environment.getExternalStorageDirectory(), appName);
        return fileOrFilesSize;
    }

    private void initView() {
        mContext = getActivity();


        //--upgrade
        mUpgradeHelper = new UpgradeHelper(mContext);
        curVerName = Utils.getClientVersionName();
        //----
        clear_catch = (RelativeLayout) contentView.findViewById(R.id.setting_clear_catch);
        version_upgrad = (RelativeLayout) contentView.findViewById(R.id.setting_version_upgrade);
        abous_us = (RelativeLayout) contentView.findViewById(R.id.setting_about_us);
        tv_version = (TextView) contentView.findViewById(R.id.setting_tv_version);
        tv_catch = (TextView) contentView.findViewById(R.id.setting_tv_catch);
        circle_Imageview = (ImageView) contentView.findViewById(R.id.setting_circle_imageview);

        setting_sd_content=(TextView)contentView.findViewById(R.id.setting_sd_content);



    }

    private void setListenner() {

        clear_catch.setOnClickListener(this);
        version_upgrad.setOnClickListener(this);
        abous_us.setOnClickListener(this);
        setting_sd_content.setOnClickListener(this);
        // 为设置中sdcard选项注册contextMenu


    }
   void initData(){

       List<StorageBean> list=StoragePathsManager.getInstanse(getActivity()).getStoragePaths();
       String perfect= SPUtil.getInstance().getString("sdcard", "");

       String setByuser= SPUtil.getInstance().getString("sdcardbyuser","");
       LogUtil.e("v1.2.0","perfect path" +perfect);
       LogUtil.e("v1.2.0","setByuser path" +setByuser);
       if(TextUtils.isEmpty(setByuser)){
           for(StorageBean bean:list){
               if(bean.getPath().equalsIgnoreCase(perfect)){
                   setting_sd_content.setText(bean.getName());
                   bean.setIsClick(true);
               }else{
                   bean.setIsClick(false);

               }
           }
       }else{
           for(StorageBean bean:list){
               if(bean.getPath().equalsIgnoreCase(setByuser)){
                   setting_sd_content.setText(bean.getName());
                   bean.setIsClick(true);
               }else{
                   bean.setIsClick(false);
               }
           }
       }




       adapter=  new StorageAdapter(getActivity(),list);
       popWindow=new SdcardSettingsPopWindow(getActivity(),adapter);
   }


    public void onEventMainThread(StorageBean obj) {
        super.onEventMainThread(obj);
        if(obj!=null){
            setting_sd_content.setText(obj.getName());
            // 用户手动设置sdcard路径
            SPUtil.getInstance().putString("sdcardbyuser", obj.getPath());
            LogUtil.e("v1.2.0","user set path "+obj.getPath());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
             case R.id.setting_sd_content:

                 if(popWindow!=null){
                     popWindow.showPopupWindow(setting_sd_content);
                 }


                break;
            case R.id.setting_about_us:
                Intent intent = new Intent(mContext, ChaojishipinAboutUsActivity.class);
                intent.putExtra("curVerName", curVerName);
                startActivity(intent);
                break;
            case R.id.setting_clear_catch:
                GetFileSizeUtil.delete(StorageUtils.getCacheDirectory(mContext.getApplicationContext()));
                tv_catch.setText(0+"");
                break;
            case R.id.setting_version_upgrade:
                if (NetWorkUtils.isNetAvailable()) {
                    if (TextUtils.isEmpty(sVerName))
                        UpgradeHelper.requestUpgradeData(new RequestUpgradeListener());
                    else {
                        mUpgradeHelper.new CheckNewestVersionAsyncTask().execute();
                    }
                } else ToastUtil.showShortToast(mContext, R.string.neterror);
                break;
        }
    }

    class RequestUpgradeListener implements RequestListener<UpgradeInfo> {
        @Override
        public void onResponse(UpgradeInfo result, boolean isCachedData) {
            if (result != null) {
                sVerName = result.getVersion();
                if (!TextUtils.isEmpty(sVerName)) {
                    if (sVerName.compareTo(curVerName) > 0) {
//                        if(isAdded()){
//                            tv_version.setText(getResources().getString(R.string.setting_new) + sVerName + getResources().getString(R.string.setting_version));
//                        }
                        circle_Imageview.setVisibility(View.VISIBLE);
                    } else {
//                        if(isAdded()){
//                            tv_version.setText(getResources().getString(R.string.setting_already_new) + sVerName);
//                        }
                        circle_Imageview.setVisibility(View.GONE);
                    }
                }
                loadUrl = result.getUpgradelink();
                mUpgradeHelper.setmSerVerName(sVerName);
                mUpgradeHelper.setmDownUrl(loadUrl);

                mUpgradeHelper.setmUpgradeType(result.getUpgrade());
            }
        }

        @Override
        public void netErr(int errorCode) {
        }

        @Override
        public void dataErr(int errorCode) {
        }
    }
}
