package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.DownloadListExpandlistviewAdapter;
import com.chaojishipin.sarrs.bean.VideoDetailIndex;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.bean.VideoPlayerNotifytData;
import com.chaojishipin.sarrs.download.activity.DownloadActivity;
import com.chaojishipin.sarrs.download.dao.DownloadDao;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.download.DownloadManager;
import com.chaojishipin.sarrs.download.download.DownloadObserver;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.interfaces.INetWorkObServe;
import com.chaojishipin.sarrs.receiver.NetWorkStateReceiver;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.widget.PinnedHeaderExpandableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulianshu on 2015/9/1.
 */
public class DownLoadListActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ExpandableListView.OnGroupClickListener, INetWorkObServe, DownloadObserver {
    private ImageView iv_download_back;
    private TextView tv_video_name;
    private TextView tv_download_manager;
    private VideoPlayerNotifytData mediaNotifyData;
    //    PinnedHeaderExpandableListView
    private ExpandableListView lv_juji;
    private DownloadListExpandlistviewAdapter madapter;
    private VideoDetailIndex indexItem;
    private final String TAG = "DownLoadActivity";
    private SparseArray<ArrayList<VideoItem>> fenyeList = new SparseArray<ArrayList<VideoItem>>();
    private SparseArray<SparseArray<Boolean>> downloadstatulist = new SparseArray<SparseArray<Boolean>>();
    private VideoDetailItem mVideoDetailItem;
    private NetWorkStateReceiver netWorkStateReceiver;
    private RelativeLayout relativeLayout;
    private List<String> titlelist;
    private DownloadManager downloadManager;
//    private int expandFlag = -1;//控制列表的展开
    private int from;
    SparseArray<Boolean> alist = new SparseArray<Boolean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        full(false);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(false);
        setContentView(R.layout.activity_download_layout);
        initView();
        registeListener();
        downloadManager = ChaoJiShiPinApplication.getInstatnce().getDownloadManager();
        //接收数据
        initData();
    }

    void initData(){

        mediaNotifyData = (VideoPlayerNotifytData) this.getIntent().getSerializableExtra("mediaNotifyData");
        mVideoDetailItem = (VideoDetailItem) getIntent().getSerializableExtra("mVideoDetailItem");
        madapter = new DownloadListExpandlistviewAdapter(DownLoadListActivity.this, null, null, mVideoDetailItem);
        lv_juji.setAdapter(madapter);
        tv_video_name.setText(mVideoDetailItem.getTitle());
        //请求
        getNetData();
    }

    @Override
    protected void onResume() {
        if (ConstantUtils.From_HalfPlayer_Down == from)
            tv_download_manager.setVisibility(View.VISIBLE);
        else if (ConstantUtils.From_More_Down == from)
            tv_download_manager.setVisibility(View.GONE);
        //界面重新加载需要重新跟新下载状态
        //获取当前页的下载状态
        SparseArray<Boolean> alist = new SparseArray<Boolean>();
        alist = DownloadDao.updateDownloadedFlagByDB(fenyeList.get(indexItem.getPn()), alist);
        downloadstatulist.append(indexItem.getPn(), alist);
        madapter.setDownloadstatulist(downloadstatulist);
        madapter.notifyDataSetChanged();
        downloadManager.registerDownloadObserver(DownLoadListActivity.this);
        downloadManager.notifyObservers();

        super.onResume();
    }

    private void initView() {
        from = getIntent().getIntExtra("from", 0);
        relativeLayout = (RelativeLayout) findViewById(R.id.videodetail_loading);
        relativeLayout.setVisibility(View.VISIBLE);
        iv_download_back = (ImageView) findViewById(R.id.iv_download_back);
        tv_video_name = (TextView) findViewById(R.id.tv_video_name);
        tv_download_manager = (TextView) findViewById(R.id.tv_download_manager);
        if (ConstantUtils.From_HalfPlayer_Down == from)
            tv_download_manager.setVisibility(View.VISIBLE);
        else if (ConstantUtils.From_More_Down == from)
            tv_download_manager.setVisibility(View.GONE);

        lv_juji = (ExpandableListView) findViewById(R.id.lv_juji);
        lv_juji.setVisibility(View.GONE);
    }

    public void registeListener() {
        iv_download_back.setOnClickListener(this);
        lv_juji.setGroupIndicator(null);
        lv_juji.setOnGroupClickListener(this);
        tv_download_manager.setOnClickListener(this);
        lv_juji
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        for (int i = 0, count = lv_juji
                                .getExpandableListAdapter().getGroupCount(); i < count; i++) {
                            if (groupPosition != i) {// 关闭其他分组
                                lv_juji.collapseGroup(i);
                            }
                        }
                    }
                });

        lv_juji.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
            }
        });
    }

    public void getNetData() {
        indexItem = new VideoDetailIndex();
        indexItem.setPn(0);
        requestVideoDetailByMediaAuto(mVideoDetailItem, indexItem);
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
        indexItem = new VideoDetailIndex();
        indexItem.setPn(i);
        if (titlelist != null && titlelist.size() == 1) {
            DownloadListExpandlistviewAdapter.ViewholderGroupView viewholderGroupView = (DownloadListExpandlistviewAdapter.ViewholderGroupView) view.getTag();
            viewholderGroupView.getImageview().setVisibility(View.GONE);
            return true;
        }
        if (expandableListView.isGroupExpanded(i)) {
            madapter.setCurrent_group_open_position(-1);
        } else {
            madapter.setCurrent_group_open_position(i);
            if (fenyeList.get(indexItem.getPn()) == null) {
                requestVideoDetailByMediaAuto(mVideoDetailItem, indexItem);
            } else {
                //剧集信息已经加载  但是下载状态需要更新
                SparseArray<Boolean> alist = new SparseArray<Boolean>();
                alist = DownloadDao.updateDownloadedFlagByDB(fenyeList.get(indexItem.getPn()), alist);
                downloadstatulist.append(indexItem.getPn(), alist);
                madapter.setDownloadstatulist(downloadstatulist);
                mVideoDetailItem.setVideoItems(fenyeList.get(i));
                madapter.setVideoDetailItem(mVideoDetailItem);
                synchronized (madapter) {
                    madapter.notify();
                }
            }
        }
        lv_juji.smoothScrollByOffset(i);
        return false;
    }

    /**
     * @des 刚进入半屏播放请求推荐剧集以及描述接口
     */
    private void requestVideoDetailByMediaAuto(VideoDetailItem item, VideoDetailIndex inItem) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_VIDEODETAIL_HALF_PLAY_TAG);
        RequestVideoDetailListener requstListner = new RequestVideoDetailListener();
        HttpApi.getVideoDetailRequest(item.getCategory_id(), item.getId(), inItem.getPn(), 0).start(requstListner, ConstantUtils.REQUEST_VIDEODETAIL_HALF_PLAY_TAG);
    }

    @Override
    public void observeNetWork(String netName, int netType, boolean isHasNetWork) {
        if (!isHasNetWork) {
            ToastUtil.showShortToast(this, getResources().getString(R.string.net_record_msg));
        }
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void onDownloadChanged(DownloadManager manager) {

    }

    @Override
    public void onDownloadEnd(DownloadManager manager, DownloadJob job) {
        //界面重新加载需要重新跟新下载状态
        downloadstatulist.clear();
        SparseArray<Boolean> alist = new SparseArray<Boolean>();
        alist = DownloadDao.updateDownloadedFlagByDB(fenyeList.get(indexItem.getPn()), alist);
        downloadstatulist.append(indexItem.getPn(), alist);
        madapter.setDownloadstatulist(downloadstatulist);
        madapter.notifyDataSetChanged();
    }

    /**
     * @des 请求半屏页剧集信息回调  首页传入0，折叠剧集传入1
     */
    private class RequestVideoDetailListener implements RequestListener<VideoDetailItem> {
        private int autoPageKey;

        @Override
        public void onResponse(VideoDetailItem result, boolean isCachedData) {
            Log.i("searchPlay", "RequestVideoDetailListener---->" + result.toString());
            relativeLayout.setVisibility(View.GONE);
            lv_juji.setVisibility(View.VISIBLE);
            // 半屏页和底部弹出布局同时展示
            // 将数据提交给播放器
            if (result == null) {
                return;
            }
            ArrayList<VideoItem> list = (ArrayList<VideoItem>) result.getVideoItems();
            fenyeList.append(indexItem.getPn(), list);
            //获取VideoItem对应的下载记录
            SparseArray<Boolean> alist = new SparseArray<Boolean>();
            alist = DownloadDao.updateDownloadedFlagByDB(list, alist);
            downloadstatulist.append(indexItem.getPn(), alist);
            mVideoDetailItem.setVideoItems(list);
            madapter.setVideoDetailItem(mVideoDetailItem);
            madapter.setDownloadstatulist(downloadstatulist);
            titlelist = result.getPage_titles();
            madapter.setTitlelist(titlelist);
            madapter.setData(fenyeList);
            madapter.notifyDataSetChanged();
            lv_juji.expandGroup(indexItem.getPn());

        }

        @Override
        public void dataErr(int errorCode) {
            LogUtil.e(TAG, "" + errorCode);
        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e(TAG, "" + errorCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_download_back:
                finish();
                break;
            case R.id.tv_download_manager:
                Intent intent = new Intent(DownLoadListActivity.this, DownloadActivity.class);
//              intent.putExtra("mediaNotifyData",mediaNotifyData);
//              intent.putExtra("mVideoDetailItem",mVideoDetailItem);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.lv_juji:
                break;
        }
    }

    //解决系统改变字体大小的时候导致的界面布局混乱的问题
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        netWorkStateReceiver = new NetWorkStateReceiver();
        netWorkStateReceiver.setmNetWorkObserve(this);
        registerReceiver(netWorkStateReceiver, filter);
    }

    private void unRegisterReceiver() {
        if (null != netWorkStateReceiver) {
            unregisterReceiver(netWorkStateReceiver);
            netWorkStateReceiver = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        downloadManager.deregisterDownloadObserver(this);
        super.onPause();
        unRegisterReceiver();
    }

    private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
