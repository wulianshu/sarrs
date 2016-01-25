package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.bean.UpgradeInfo;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.download.activity.DownloadActivity;
import com.chaojishipin.sarrs.download.fragment.DownloadFragment;
import com.chaojishipin.sarrs.fragment.MainChannelFragment;
import com.chaojishipin.sarrs.fragment.RankListFragment;
import com.chaojishipin.sarrs.fragment.SettingFragment;
import com.chaojishipin.sarrs.fragment.SlidingMenuFragment;
import com.chaojishipin.sarrs.fragment.TopiclistFragment;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.UpoloadHistoryRecordListener;
import com.chaojishipin.sarrs.manager.HistoryRecordManager;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ChannelUtil;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.SaveUserinfo;
import com.chaojishipin.sarrs.utils.StoragePathsManager;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.UpgradeHelper;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.TitleActionBar;
import com.ibest.thirdparty.share.presenter.ShareManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import java.util.ArrayList;
import java.util.List;

/**
 *   超级视频首页
 * */
public class ChaoJiShiPinMainActivity extends ChaoJiShiPinBaseActivity implements TitleActionBar.onActionBarClickListener, View.OnClickListener {
    private static final String TAG = "ChaoJiShiPinMainActivity";
    public static String pageid = "00S002001";
    private SlidingMenu mSlidingMenu;
//    private int mSwipePostion = -1;
    private TitleActionBar mTitleActionBar;
    private String title;
    private MainChannelFragment mainF = null;
    private TopiclistFragment topiclistFragment;
    private SlidingMenuFragment slidingMenuFragment;
    private RankListFragment rankListfragment;
    private final String playRecordMenu = "1";
    private final String downloadMenu = "2";
    private String menuType = playRecordMenu;
    private DownloadFragment downloadFragment;
    public SlidingMenuLeft slidingMenuLeft;
    ArrayList<HistoryRecord> localrecordlist;

    private Intent mIntent;
    private Context mContext;
    private UpgradeInfo mUpgradeData;
    private UpgradeHelper mUpgradeHelper;
    private String mUpgradeType;
    private boolean isExist = false;
    private String channelname;
    public static String lasttimeCheck = "1";
    //是否为提审状态 默认为提审状态
    public static String isCheck = "1";
    public boolean isfirst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mIntent = getIntent();
        channelname = ChannelUtil.getCurrentChannel(this);
        this.overridePendingTransition(R.anim.activity_start_alpha, 0);
        setTitleBarVisibile(false);
        initSlidingMenu();
        // 打开应用才判断升级逻辑
        SaveUserinfo.getLoginuserinfo(this);
        if (mIntent.getBooleanExtra(UpgradeHelper.FROM_SPLASH, false))
            initData();
        initView();
        setListener();
        registInfo(this);
        //已经登录向服务器同步数据
        if (UserLoginState.getInstance().isLogin()) {
            requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
        }
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        for (int i = 0; i < 100; ++i) {
//            final int j = i;
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    if(j==0){
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    LogUtil.e("test", "j = " + j);
//                }
//            });
//        }
    }

    private void initData() {

        StoragePathsManager.getInstanse().getExternalSDpath();
        LogUtil.e("xll_storage", "sdcard path init ok ");
        mUpgradeHelper = new UpgradeHelper(mContext);
        // 获取升级信息
        mUpgradeData = (UpgradeInfo) mIntent.getExtras().get(UpgradeHelper.UPGRADE_DATA);
        isfirst =  mIntent.getBooleanExtra(UpgradeHelper.IS_FIRST,true);
//      mUpgradeData.setIscheck("1");
        Log.d("upgrade", " get " + mUpgradeData);
        // 再次请求
//        if (mUpgradeData == null && NetWorkUtils.isNetAvailable())
//            UpgradeHelper.requestUpgradeData(new RequestUpgradeListener());

        if (mUpgradeData != null) {
            mUpgradeHelper.setmSerVerName(mUpgradeData.getVersion());
            mUpgradeHelper.setmDownUrl(mUpgradeData.getUpgradelink());
            mUpgradeType = mUpgradeData.getUpgrade();
        }
    }


    class RequestUpgradeListener implements RequestListener<UpgradeInfo> {
        @Override
        public void onResponse(UpgradeInfo result, boolean isCachedData) {
            mUpgradeData = result;
//            if(mUpgradeData!=null) {
//                isCheck = mUpgradeData.getIscheck();
//            }else{
//                isCheck = "1";
//            }
            SharedPreferences sharedPreferences = ChaoJiShiPinMainActivity.this.getSharedPreferences(ConstantUtils.SHARE_APP_TAG, Activity.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if(result !=null) {
                if (ChaojishipinSplashActivity.isFrist) {
                    edit.putString("ischeck", result.getIscheck());
                    edit.commit();
                    ChaoJiShiPinMainActivity.isCheck = result.getIscheck();
                    ChaoJiShiPinMainActivity.lasttimeCheck = result.getIscheck();
                } else {
                    //上次审核状态
                    ChaoJiShiPinMainActivity.lasttimeCheck = sharedPreferences.getString("ischeck", "1");
                    //本次审核状态
                    ChaoJiShiPinMainActivity.isCheck = result.getIscheck();
                    //上次是非审核状态之后永远都是非审核状态  而且要保存侧滑菜单的状态
                    if("0".equals(ChaoJiShiPinMainActivity.lasttimeCheck)){
                        edit.putString("ischeck", "0");
                        edit.commit();
                        ChaoJiShiPinMainActivity.isCheck = result.getIscheck();;
                    }else{
                        edit.putString("ischeck",result.getIscheck());
                        ChaoJiShiPinMainActivity.isCheck = result.getIscheck();
                        edit.commit();
                    }
                }
            }else{
                //为空为审核状态
                edit.putString("ischeck", "1");
                edit.commit();
                ChaoJiShiPinMainActivity.isCheck = "1";
                ChaoJiShiPinMainActivity.lasttimeCheck = "1";
            }
            LogUtil.e(UpgradeHelper.TAG, "!!!!!!!!!!launch activity requestUpgradeData sucess!!!!!!!!!!");
        }

        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegistInfo();
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //新浪授权
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        ShareManager.authorCallback(requestCode, resultCode, data);
    }

    private void initView() {
        mTitleActionBar = (TitleActionBar) findViewById(R.id.mainactivity_title_layout);
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        mainF = new MainChannelFragment();
        topiclistFragment = new TopiclistFragment();
        rankListfragment = new RankListFragment();
 //       mSwipePostion = mainF.getSwipePosition();
        //TODO
        title = this.getResources().getString(R.string.recommend);
        mTitleActionBar.setTitle(title);
        fragmentTransaction.replace(R.id.content, mainF);
        fragmentTransaction.commitAllowingStateLoss();

        Log.d("upgrade", " mUpgradeType " + mUpgradeType);
        if (!TextUtils.isEmpty(mUpgradeType)) {
            if (mUpgradeType.equalsIgnoreCase("1")) {
                mUpgradeHelper.doNewVersionSuggestUpdate();
            } else if (mUpgradeType.equalsIgnoreCase("2")) {
                mUpgradeHelper.doNewVersionForceUpdate();
            }
        }


    }

    private void setListener() {
        mTitleActionBar.setOnActionBarClickListener(this);
    }

    @Override
    protected void onResume() {
//        if(!TextUtils.isEmpty(channelname)){
//            requestUpgradinfo(channelname);
//        }
        super.onResume();

    }

    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.chaojishipin_mainactivity_layout, null);
    }

    /**
     * 初始化侧边滑动栏组件
     */
    private void initSlidingMenu() {
        mSlidingMenu = new SlidingMenu(this);
        //设置可以从左边开始滑动
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        //设置可以从边缘滑动
//        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        mSlidingMenu.setSelectorDrawable(R.color.color_c5242b);
        //设置菜单栏滑出时主页面遗留的宽度
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_main_ui_left);
        //设置菜单栏的宽度
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width);
        mSlidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        //支持淡入淡出
        mSlidingMenu.setFadeEnabled(true);
        //滑动时的渐变程度
        mSlidingMenu.setFadeDegree(0.35f);
        //将滑动菜单依附于当前Activity
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.slidingmenulayout);
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        slidingMenuFragment = new SlidingMenuFragment();
        slidingMenuFragment.setSlideMenu(mSlidingMenu);
        fragmentTransaction.replace(R.id.slidingmenu_content_layout, slidingMenuFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void handleInfo(Message msg) {

    }



    @Override
    public void onTitleDoubleTap() {
        if (mainF.getPullSwiteView() != null && mainF.mainActivityChannelAdapter !=null) {
            mainF.getPullSwiteView().getRefreshableView().smoothScrollToPosition(0);

//          new  Handler().postDelayed(new Runnable() {
//              @Override
//              public void run() {
//                  mainF.getPullSwiteView().getRefreshableView().setSelection(0);              }
//          },1500);


        }
    }
    SarrsArrayList slidings_gv = new SarrsArrayList<SlidingMenuLeft>();
    SarrsArrayList slidings_lv = new SarrsArrayList<SlidingMenuLeft>();
    private class RequestSlidingMenuLeftListener implements RequestListener<SarrsArrayList> {

        @Override
        public void onResponse(SarrsArrayList result, boolean isCachedData) {
            if(slidingMenuFragment!=null){
                slidings_lv = result;
                SharedPreferences sharedPreferences = ChaoJiShiPinMainActivity.this.getSharedPreferences(ConstantUtils.SHARE_APP_TAG, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String channel_list = JsonUtil.toJSONString(result);
                editor.putString(SlidingMenuFragment.CHANNEL_LIST,channel_list);
                editor.commit();
                for (int i = 0;i<slidings_lv.size();i++){
                    if( ((SlidingMenuLeft)slidings_lv.get(i)).getContent_type().equals("10") ){
                        break;
                    }else{
                        slidings_gv.add((SlidingMenuLeft)slidings_lv.get(i));
                    }
                }
                for(int i=0;i<slidings_gv.size();i++){
                    slidings_lv.remove(0);
                }
                if(slidings_gv.size()>0) {
                    slidingMenuFragment.mLeftMenuGridView.setVisibility(View.VISIBLE);
                    slidingMenuFragment.showSlidingGVMenu(slidings_gv);
                }else{
                    slidingMenuFragment.mLeftMenuGridView.setVisibility(View.GONE);
                }
                if(slidings_lv.size()>0) {
                    slidingMenuFragment.showSlidingMenu(slidings_lv);
                }
                slidingMenuFragment.showSlidingMenu(result);
                SPUtil.getInstance().putBoolean(ConstantUtils.SliddingMenuInit, true);

            }

        }

        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {

        }
    }

    @Override
    public void onTitleLeftClick(View v) {
        //用户点击了菜单栏按钮
        SharedPreferences sharedPreferences = this.getSharedPreferences(ConstantUtils.SHARE_APP_TAG, Activity.MODE_PRIVATE);
        String data =sharedPreferences.getString(SlidingMenuFragment.CHANNEL_LIST,"");
        if("0".equals(lasttimeCheck) &&"1".equals(isCheck) && null != mSlidingMenu && !TextUtils.isEmpty(data) || !NetWorkUtils.isNetAvailable()){
            slidingMenuFragment.loadLocalmenuData();
            mSlidingMenu.toggle();
            return;
        }
        if (null != mSlidingMenu) {
            boolean isInit=   SPUtil.getInstance().getBoolean(ConstantUtils.SliddingMenuInit,false);
            if(slidingMenuFragment!=null&&!isInit&&NetWorkUtils.isNetAvailable()){
                LogUtil.e("xll", "request sliding menu");
                HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SLDINGMENU_LETF_TAG);
                HttpApi.getSlidingMenuLeftRequest().start(new RequestSlidingMenuLeftListener(), ConstantUtils.REQUEST_SLDINGMENU_LETF_TAG);
            }
            mSlidingMenu.toggle();
        }
    }

    public void onEventMainThread(SlidingMenuLeft slidingMenuLeft) {
        this.slidingMenuLeft = slidingMenuLeft;
        LogUtil.e(TAG, "cid" + slidingMenuLeft.getCid());
        LogUtil.e(TAG, "content_type" + slidingMenuLeft.getContent_type());
        LogUtil.e(TAG, "title" + slidingMenuLeft.getTitle());
        title = slidingMenuLeft.getTitle();
        mTitleActionBar.setTitle(title);
        // TODO
        mTitleActionBar.setmRightButtonVisibility(true);
        mTitleActionBar.setRightEditButtonVisibility(false);
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        //专题
        if (ConstantUtils.TOPIC_CONTENT_TYPE.equals(slidingMenuLeft.getContent_type())) {
            fragmentTransaction.replace(R.id.content, topiclistFragment);
        }
        //排行榜
        else if (ConstantUtils.RANKLIST_CONTENT_TYPE.equals(slidingMenuLeft.getContent_type())) {
            fragmentTransaction.replace(R.id.content, rankListfragment);
        } else {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("slidingMenuLeft", slidingMenuLeft);
//            mainF.setArguments(bundle);
            fragmentTransaction.replace(R.id.content, mainF);
        }
        fragmentTransaction.commitAllowingStateLoss();
        super.onEventMainThread(slidingMenuLeft);
    }


    @Override
    public void onTitleRightClick(View v) {
        //用户点击了播放按钮
       /* if (null != mSlidingMenu) {
            mSlidingMenu.toggle();
        }*/
        if (v.getId() == R.id.right_edit_btn) {
            downloadFragment.updateEditView();
        }
        if (v.getId() == R.id.mainactivity_right_btn) {
            Intent intent = new Intent(this, HistoryRecordActivity.class);
            startActivity(intent);
        }
    }


    /*void replaceSaveFragment(){
        mTitleActionBar.setVisibility(View.GONE);
        title = getResources().getString(R.string.collection);

        mTitleActionBar.setTitle(title);
        mTitleActionBar.setRightEditButtonVisibility(false);
        mTitleActionBar.setmRightButtonVisibility(false);
        SaveFragment saveF = new SaveFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, saveF);
        transaction.commit();
        if (mSlidingMenu != null) {
            mSlidingMenu.showContent(true);
        }
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fragment_user_setting:
                mTitleActionBar.setRightEditButtonVisibility(false);
                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
                break;
        }
    }


    public SlidingMenuLeft getSlidingMenuLeft() {
        return this.slidingMenuLeft;
    }

    private void gotoDownload() {
//        menuType = downloadMenu;
//        title = getResources().getString(R.string.download_title);
//        mTitleActionBar.setTitle(title);
//        updateDeleteIcon();
//        downloadFragment = new DownloadFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.content, downloadFragment);
//        transaction.commitAllowingStateLoss();
//        if (mSlidingMenu != null) {
//            mSlidingMenu.showContent(true);
//        }
//        setEditMenuText(this.getString(R.string.edit));
          Intent intent = new Intent(this,DownloadActivity.class);
          startActivity(intent);
    }

    public void setEditMenuText(String text) {
        mTitleActionBar.setRightEditButtonVisibility(true);
        mTitleActionBar.setRightEditButtonText(text);
    }

    public void updateDeleteIcon() {
        mTitleActionBar.setmRightButtonVisibility(false);
        if (ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getCompletedDownloads().size() > 0) {
            mTitleActionBar.setRightEditButtonVisibility(true);
        } else {
            mTitleActionBar.setRightEditButtonVisibility(false);
        }
    }

    /**
     * 根据TOKEN获取所有的历史记录
     *
     * @param token
     */
    private void requestHistoryRecordData(String token) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_HISTORYRECORD_DETAIL);
        HttpApi.
                getHistoryRecordList(token)
                .start(new RequestHistoryRecordListener(), ConstantUtils.REQUEST_HISTORYRECORD_DETAIL);
    }

    /**
     * 获取播放记录的的监听
     */
    private class RequestHistoryRecordListener implements RequestListener<SarrsArrayList> {
        @Override
        public void onResponse(SarrsArrayList result, boolean isCachedData) {
            //进行展现的相关操作
            HistoryRecordManager.setHisToryRecordFromServer(result);
            HistoryRecordDao historyRecordDao = new HistoryRecordDao(ChaoJiShiPinMainActivity.this);
            localrecordlist = historyRecordDao.getAll();
            ArrayList<HistoryRecord> netlist = result;
            ArrayList<HistoryRecord> uploadlist = new ArrayList<HistoryRecord>();
            if (netlist != null && netlist.size() > 0) {
                for (int i = 0; i < localrecordlist.size(); i++) {
                    for (int j = 0; j < netlist.size(); j++) {
                        if (localrecordlist.get(i).getGvid().equals(netlist.get(j).getGvid())) {
                            if (localrecordlist.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) > 0) {
                                uploadlist.add(localrecordlist.get(i));
                            }
                            break;
                        }
                        if (j == netlist.size() - 1) {
                            uploadlist.add(localrecordlist.get(i));
                        }
                    }
                }
            } else {
                uploadlist = localrecordlist;
            }
            //需要上传
            List<UploadRecord> uploadRecordList = new ArrayList<UploadRecord>();
            if (uploadlist.size() > 0) {
                LogUtil.i(TAG, "向服务器同步记录");
                for (HistoryRecord historyRecord : uploadlist) {
                    UploadRecord aupload = new UploadRecord();
                    // TODO 吴联暑 check下 add by xll
                    if(!TextUtils.isEmpty(historyRecord.getCategory_id().trim())){
                        aupload.setCid(Integer.parseInt(historyRecord.getCategory_id().trim()));
                    }
                    aupload.setVid(historyRecord.getGvid());
                    aupload.setSource(historyRecord.getSource());
                    if(!TextUtils.isEmpty(historyRecord.getPlay_time())){
                        aupload.setPlayTime(Integer.parseInt(historyRecord.getPlay_time()));
                    }

                    aupload.setAction(0);
                    aupload.setDurationTime(historyRecord.getDurationTime());
                    aupload.setPid(historyRecord.getId());
                    if(!TextUtils.isEmpty(historyRecord.getTimestamp())){
                        aupload.setUpdateTime(Long.parseLong(historyRecord.getTimestamp()));
                    }
                    uploadRecordList.add(aupload);
                }
                String json = JsonUtil.toJSONString(uploadRecordList);
                uploadHistoryRecord(UserLoginState.getInstance().getUserInfo().getToken(), json);
            }
        }

        @Override
        public void netErr(int errorCode) {
            //网络异常 4000秒后重新获取数据直到播放记录获取正常为止
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
               }
           }, 4000);
        }

        @Override
        public void dataErr(int errorCode) {

        }
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {
         LogUtil.e("xll", "MainActivity netType " + netType);
//        if(netType!=-1){
//            UpgradeHelper.requestUpgradeData(new RequestUpgradeListener());
//        }

    }

    /**
     * 上报历史记录
     *
     * @paramcid
     */
    private void uploadHistoryRecord(String token, String json) {
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.UPLOAD_HISTORY_RECORD);
        HttpApi.
                uploadHistoryRecord(token, json, new UpoloadHistoryRecordListener());
//                .start( new UpoloadHistoryRecordListener(), ConstantUtils.UPLOAD_HISTORY_RECORD);
    }

    private void back()
    {
            this.finish();
            AllActivityManager.getInstance().finishAllActivity();
    }

    private long lastclicktime=0;
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
////            if (System.currentTimeMillis() - lastclicktime<2000){
//////                back();
////            }else{
////                ToastUtil.showShortToast(this, getResources().getString(R.string.again_click_exist));
////                lastclicktime = System.currentTimeMillis();
////            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mContext = this;
//        mIntent = getIntent();
////      this.overridePendingTransition(R.anim.activity_start_alpha, 0);
//        setTitleBarVisibile(false);
//        initSlidingMenu();
//        // 打开应用才判断升级逻辑
//        if (mIntent.getBooleanExtra(UpgradeHelper.FROM_SPLASH, false))
//        initData();
//        initView();
//        setListener();
////      registInfo(this);
//        //已经登录向服务器同步数据
//        if (UserLoginState.getInstance().isLogin()) {
//            requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
//        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

//        unRegistInfo();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        registInfo(this);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            LogUtil.e("wulianshu","back");
//            return false;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
            long currenttime =  System.currentTimeMillis();
            if (currenttime - lastclicktime<2000l){
                back();
            }else{
                ToastUtil.showShortToast(this, getResources().getString(R.string.again_click_exist));
                lastclicktime = System.currentTimeMillis();
                return;
            }
        super.onBackPressed();
    }
    public void setmTitleActionBarTitle(String title){
        mTitleActionBar.setTitle(title);
    }
    public void ResetmTitleActionBarTitle(){
        mTitleActionBar.setTitle(title);
    }
}
