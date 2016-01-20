package com.chaojishipin.sarrs.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinMainActivity;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.activity.ChaojishipinRegisterActivity;
import com.chaojishipin.sarrs.activity.PlayActivityFroWebView;
import com.chaojishipin.sarrs.activity.SearchActivity;
import com.chaojishipin.sarrs.adapter.MainActivityChannelAdapter2;
import com.chaojishipin.sarrs.bean.AddFavorite;
import com.chaojishipin.sarrs.bean.CancelFavorite;
import com.chaojishipin.sarrs.bean.CheckFavorite;
import com.chaojishipin.sarrs.bean.MainActivityAlbum;
import com.chaojishipin.sarrs.bean.MainActivityData;
import com.chaojishipin.sarrs.bean.MainMenuItem;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.feedback.DataHttpApi;
import com.chaojishipin.sarrs.feedback.DataReportListener;
import com.chaojishipin.sarrs.feedback.DataReporter;
import com.chaojishipin.sarrs.manager.FavoriteManager;
import com.chaojishipin.sarrs.swipe.SwipeMenu;
import com.chaojishipin.sarrs.swipe.SwipeMenuCreator;
import com.chaojishipin.sarrs.swipe.SwipeMenuItem;
import com.chaojishipin.sarrs.thirdparty.LoginUtils;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.thirdparty.share.ShareDataConfig;
import com.chaojishipin.sarrs.uploadstat.UploadStat;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.DeleteRelativelayout;
import com.chaojishipin.sarrs.widget.NetStateView;
import com.chaojishipin.sarrs.widget.PullToRefreshSwipeListView;
import com.chaojishipin.sarrs.widget.PullToRefreshSwipeMenuListView;
import com.chaojishipin.sarrs.widget.SarrsMainMenuView;
import com.chaojishipin.sarrs.widget.SarrsToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ibest.thirdparty.share.model.ShareData;
import com.ibest.thirdparty.share.view.ShareDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/6/17.
 */
public class MainChannelFragment extends MainBaseFragment implements  View.OnClickListener,
        AdapterView.OnItemClickListener {
    //    PullToRefreshSwipeListView.OnSwipeListener, PullToRefreshSwipeListView.OnMenuItemClickListener,
    public final String pageid = "00S002000_2";
    public MainActivityChannelAdapter2 mainActivityChannelAdapter;
    private String mCid;
    private int mSwipePosition = -1;

    private String mArea;
    private String mCurrentTitle = ConstantUtils.TITLE_SUGGEST;

    private final static int MESSAGE_DELAYED_TIME = 3000;

    private ArrayList<MainActivityAlbum> mAlbumLists=new ArrayList<MainActivityAlbum>();
    // mode ==0下拉刷新// mode==1 上拉刷新 // mode==2
    private int reQMode = 1;
    SlidingMenuLeft slidingMenuLeft;
    private List<String> alreadyupgvid = new ArrayList<String>();
    private ChaoJiShiPinMainActivity activity;
    public int firstvisiblecount = 2;
    /**
     * 构造 添加、是否存在、取消收藏统一参数
     */
    String id = "";
    String token =null;
    String type = "";
    String cid = "";
    String netType =  NetWorkUtils.getNetInfo();
    // 上报参数
    String source = "";
    String bucket;
    String seid;
    VideoDetailItem detail=null;
    int mparentId=0;

    @Override
    protected void init(){
        SarrsMainMenuView.listviewItemHeight = 350;
        SarrsMainMenuView.mode=ConstantUtils.SarrsMenuInitMode.MODE_DELETE_SAVE_SHARE;
        activity = (ChaoJiShiPinMainActivity) getActivity();

        mainActivityChannelAdapter = new MainActivityChannelAdapter2(getActivity());
        mXListView.setAdapter(mainActivityChannelAdapter);
        mSearchIcon.setOnClickListener(this);
        mXListView.setOnItemClickListener(this);
        mXListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastvisibleposition = view.getLastVisiblePosition();
                if(lastvisibleposition>firstvisiblecount){
                    activity.setmTitleActionBarTitle(activity.getResources().getString(R.string.double_click2top));
                }else{
                    activity.ResetmTitleActionBarTitle();
                }
            }
        });

        if (NetWorkUtils.isNetAvailable()) {
            hideErrorView(mRootView);
        } else {
            showErrorView(mRootView);
        }
        mXListView.setMode(PullToRefreshSwipeListView.Mode.BOTH);

        setListViewMode();
        if (mAlbumLists != null) {
            mAlbumLists.clear();
        }
        if(mainActivityChannelAdapter.menuStates!=null){
            mainActivityChannelAdapter.menuStates.clear();
        }
        mXListView.setOnRefreshListener(refreshListener2);

        slidingMenuLeft = ((ChaoJiShiPinMainActivity)getActivity()).getSlidingMenuLeft();
        if (slidingMenuLeft != null) {
            getNetData(slidingMenuLeft);
        }else{
            slidingMenuLeft = new SlidingMenuLeft();
            slidingMenuLeft.setCid("0");
            mCid = "0";
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void handleInfo(Message msg) {
    }


    @Override
    public void onRetry() {
        LogUtil.e("main ", "reloading");
        reQMode = 0;
        requestData();
    }

    @Override
    protected void requestData(){
        requestChannelData(getActivity(), mCid, ConstantUtils.MAINACTIVITY_REFRESH_AREA);
    }

    PullToRefreshBase.OnRefreshListener2 refreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            mXListView.setOnlyShowRefreshingHeader(false);
            reQMode = 0;
            requestChannelData(getActivity(), mCid, ConstantUtils.MAINACTIVITY_REFRESH_AREA);
            //Umeng上拉刷新上报
            MobclickAgent.onEvent(getActivity(), ConstantUtils.FEED_UP_LOAD);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            mXListView.setOnlyShowRefreshingHeader(false);
            reQMode = 1;
            requestChannelData(getActivity(), mCid, ConstantUtils.MAINACTIVITY_LOAD_AREA);
            MobclickAgent.onEvent(getActivity(), ConstantUtils.FEED_DOWN_LOAD);
        }
    };

    /**
     * 根据上啦下拉动作设置 刷新组件文案信息
     */
    private void setListViewMode() {
        mXListView.getLoadingLayoutProxy(true, false).setPullLabel(getActivity().getString(R.string.pull_to_refresh_pull_label));
        mXListView.getLoadingLayoutProxy(true, false).setRefreshingLabel(getActivity().getString(R.string.pull_to_refresh_refreshing_label));
        mXListView.getLoadingLayoutProxy(true, false).setReleaseLabel(getActivity().getString(R.string.pull_to_refresh_release_label));
        mXListView.getLoadingLayoutProxy(false, true).setPullLabel(getActivity().getString(R.string.pull_to_refresh_load_more_lable));
        mXListView.getLoadingLayoutProxy(false, true).setReleaseLabel(getActivity().getString(R.string.pull_to_refresh_load_more_release));
        mXListView.getLoadingLayoutProxy(false, true).setRefreshingLabel(getActivity().getString(R.string.pull_to_refresh_load_more_loading));
    }


    /**
     * 请求具体的频道数据
     *
     * @param cid
     */
    public void requestChannelData(Context context, String cid, String area) {
        mCid = cid;
        mArea = area;
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_MAINACTIVITY_DATA);
        HttpApi.
                getMainActivityDataRequest(context, cid, area)
                .start(new RequestChannelListener(), ConstantUtils.REQUEST_MAINACTIVITY_DATA);
    }


    public void onEventMainThread(SlidingMenuLeft slidingMenuLeft) {
        alreadyupgvid.clear();
        this.slidingMenuLeft = slidingMenuLeft;
        getNetData(slidingMenuLeft);
    }

    public void getNetData(SlidingMenuLeft slidingMenuLeft) {
        if(ConstantUtils.TOPIC_CONTENT_TYPE.equals(slidingMenuLeft.getContent_type()) || ConstantUtils.RANKLIST_CONTENT_TYPE.equals(slidingMenuLeft.getContent_type())){
            return;
        }
        String cid = null;
        if (ConstantUtils.TITLE_SUGGEST.equals(slidingMenuLeft.getTitle())) {
            cid = "0";
        } else {
            cid = slidingMenuLeft.getCid();
        }
        if (mCurrentTitle.equals(slidingMenuLeft.getTitle())) {
            reQMode = 3;

        } else {
            reQMode = 4;

            mCurrentTitle = slidingMenuLeft.getTitle();
        }
        if (NetWorkUtils.isNetAvailable()) {
            setListViewMode();
            if (mAlbumLists != null) {
                mAlbumLists.clear();
            }
            mXListView.setOnlyShowRefreshingHeader(true);
            mXListView.setRefreshing(true);
            requestChannelData(getActivity(), cid, ConstantUtils.MAINACTIVITY_REFRESH_AREA);
        } else {
            showErrorView(mRootView);
            mXListView.setRefreshing(true);
        }
    }
    /*
    *   swipe Menu
    * */

    // step 1. create a MenuCreator
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getActivity());
            // set item background
            deleteItem.setBackground(R.drawable.selector_main_behiend_bg);
            // set item width
            deleteItem.setWidth(Utils.dip2px(75));
            // set a icon
            deleteItem.setIcon(R.drawable.selector_main_delete);
            deleteItem.setTitle(R.string.sarrrs_str_delete);
            deleteItem.setTitleSize(12);
            deleteItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(deleteItem);


            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getActivity());
            // set item background
            openItem.setBackground(R.drawable.selector_main_behiend_bg);
            // set item width
            openItem.setIcon(R.drawable.selector_main_collect);
            openItem.setWidth(Utils.dip2px(75));
            // set item title
            openItem.setTitle(R.string.sarrs_str_collect);
            // set item title fontsize
            openItem.setTitleSize(12);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem shareItem = new SwipeMenuItem(
                    getActivity());
            // set item background
            shareItem.setBackground(R.drawable.selector_main_behiend_bg);
            // set item width
            shareItem.setWidth(Utils.dip2px(75));
            // set a icon
            shareItem.setIcon(R.drawable.selector_main_share);
            shareItem.setTitle(R.string.sarrs_str_share);
            shareItem.setTitleSize(12);
            shareItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(shareItem);
        }
    };

    @Override
    public void onResume() {
        uploadstat(mXListView.getRefreshableView());
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_icon:
                MobclickAgent.onEvent(getActivity(),ConstantUtils.SEARCH_BTN);
                buildDrawingCacheAndIntent();
                break;
            default:
                break;
        }
    }


    /**
     * 截屏，保存为Bitmap，提供给SearchAvtivity高斯模糊使用
     *
     * @auth daipei
     */
    public void buildDrawingCacheAndIntent() {
        SearchActivity.launch(getActivity());
    }

//    @Override
//    public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//        switch (index) {
//            case 0:
//                //ToastUtil.showShortToast(getActivity(), "click 1");
//                break;
//
//            case 1:
//                //ToastUtil.showShortToast(getActivity(),"click 0");
//                break;
//
//            case 2:
//                // ToastUtil.showShortToast(getActivity(),"click 2");
//                break;
//
//
//        }
//
//
//    }
//
//
//    @Override
//    public void onSwipeStart(int position) {
//        mSwipePosition = position;
//    }
//
//    @Override
//    public void onSwipeEnd(int position) {
//        mSwipePosition = position;
//    }
//
//
//    public int getSwipePosition() {
//        return mSwipePosition;
//    }
//
//    @Override
//    public void onItemClick(int position, View view, int parentId,ListAdapter adapter) {
//        mparentId=parentId-1;
//        // 构造上报参数
//        buidlParam();
//        switch(position){
//            //不喜欢
//            case 0:
//                LogUtil.e("xll", "");
//                mAlbumLists.remove(mparentId);
//                mainActivityChannelAdapter.notifyDataSetChanged();
//                // 负反馈上报
//                DataReporter.reportDislike(id, source, cid, type, token, netType, bucket, seid);
//                break;
//            //收藏
//            case 1:
//                if(UserLoginState.getInstance().isLogin()){
//                    checkSave();
//                   /*  if(adapter!=null&&adapter instanceof HeaderViewListAdapter){
//                         mainActivityChannelAdapter=(MainActivityChannelAdapter2)((HeaderViewListAdapter) adapter).getWrappedAdapter();
//                     }
//*/
//                }else{
//                    startActivity(new Intent(getActivity(), ChaojishipinRegisterActivity.class));
//                }
//                break;
//            //分享
//            case 2:
//                share(parentId);
//                // 分享上报
//                DataReporter.reportAddShare(id, source, cid, type, token, netType, bucket, seid);
//                break;
//        }
//    }

    private class RequestChannelListener implements RequestListener<MainActivityData> {

        @Override
        public void onResponse(MainActivityData result, boolean isCachedData) {
            mXListView.onRefreshComplete();
            hideErrorView(mRootView);
            mTopToast.setVisibility(View.VISIBLE);
            bucket = result.getBucket();
            seid = result.getReid();
            if (null != result) {
                int oldsize = 0;
                if (mAlbumLists != null) {
                    oldsize = mAlbumLists.size();
                }
                ArrayList<MainActivityAlbum> albums = result.getAlbumList();
                if (null != albums && albums.size() > 0) {
                    int albumsSize = albums.size();
                    int beforeSize = mAlbumLists.size();
                    // 上拉刷新
                    if (reQMode == 0) {
                        for (int i = 0; i < albumsSize; i++) {
                            if (!isContainItem(albums.get(i).getId())) {
                                mAlbumLists.add(0, albums.get(albumsSize - 1 - i));
                                MainMenuItem item=new MainMenuItem();
                                item.setIsDelete(false);
                                item.setIsSave(false);
                                item.setIsSare(false);
                                mainActivityChannelAdapter.menuStates.add(0,item);
                            }
                        }
                        //下拉加载
                    } else if (reQMode == 1) {
                        for (int i = 0; i < albumsSize; i++) {
                            if (!isContainItem(albums.get(i).getId())) {
                                int index = mAlbumLists.size();
                                mAlbumLists.add(index, albums.get(i));
                                MainMenuItem item=new MainMenuItem();
                                item.setIsDelete(false);
                                item.setIsSave(false);
                                item.setIsSare(false);
                                mainActivityChannelAdapter.menuStates.add(index,item);

                            }
                        }
                        // 切换频道
                    } else if (reQMode == 3 || reQMode == 4) {
                        mAlbumLists.clear();
//                        mAlbumLists = new ArrayList<MainActivityAlbum>();
                        for (int i = 0; i < albumsSize; i++) {
                            if (!isContainItem(albums.get(i).getId())) {
                                int index = mAlbumLists.size();
                                mAlbumLists.add(index, albums.get(i));
                                MainMenuItem item=new MainMenuItem();
                                item.setIsDelete(false);
                                item.setIsSave(false);
                                item.setIsSare(false);
                                mainActivityChannelAdapter.menuStates.add(index,item);
                            }
                        }
//                        ToastUtil.showShortToast(getActivity(), "切换频道");

                    }
                    int endSize = mAlbumLists.size();


                    if (albumsSize == 0 || beforeSize == endSize) {
                        if(isAdded()){
                            mTopToast.setText(getString(R.string.sarrs_toast_notice_no_result));
                            mTopToast.show(1000);
                        }
                    } else {
                        if(isAdded()){
                            mTopToast.setText(getString(R.string.sarrs_toast_notice_normal_start) + albumsSize + getString(R.string.sarrs_toast_notice_normal_end));
                            mTopToast.show(1000);
                        }

                    }
                    if (null != mainActivityChannelAdapter) {
                        mainActivityChannelAdapter.setmAlbums(mAlbumLists);
                        mainActivityChannelAdapter.notifyDataSetChanged();
                    }

                    if (reQMode == 1) {
                        mXListView.getRefreshableView().setSelection(oldsize);
                    } else {
                        mXListView.getRefreshableView().setSelection(0);
                    }
                    mXListView.onRefreshComplete();
                    firstvisiblecount = mXListView.getRefreshableView().getLastVisiblePosition()- mXListView.getRefreshableView().getFirstVisiblePosition()+1;
//                    if(mXListView.isHeadShowed()){
//                        firstvisiblecount = mXListView.getRefreshableView().getLastVisiblePosition()- mXListView.getRefreshableView().getFirstVisiblePosition()+1;
//                    }else{
//                    }
                    LogUtil.e("wls","firstvisiblecount:"+firstvisiblecount);

                }else{
                    if (reQMode == 3 || reQMode == 4) {
                        mTopToast.setText(getString(R.string.sarrs_toast_notice_nodata));
                        mTopToast.show(1000);
                        if (null != mainActivityChannelAdapter) {
                            mAlbumLists.clear();
                            mainActivityChannelAdapter.setmAlbums(mAlbumLists);
                            mainActivityChannelAdapter.notifyDataSetChanged();
                        }
                    }
                }
//                if (reQMode == 3 || reQMode == 4) {
//                    mXListView.getRefreshableView().smoothScrollToPosition(0);
//                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadstatfirst(mXListView.getRefreshableView());
                    }
                },1000);

            }else {
                if (reQMode == 3 || reQMode == 4) {
                    mTopToast.setText(getString(R.string.sarrs_toast_notice_nodata));
                    mTopToast.show(1000);
                    if (null != mainActivityChannelAdapter) {
                        mAlbumLists.clear();
                        mainActivityChannelAdapter.setmAlbums(mAlbumLists);
                        mainActivityChannelAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void netErr(int errorCode) {
            showErrorView(mRootView);
            mXListView.onRefreshComplete();
            LogUtil.e("error ", " net  error code " + errorCode);

        }

        @Override
        public void dataErr(int errorCode) {
            mXListView.onRefreshComplete();
            mTopToast.setText(getString(R.string.sarrs_toast_notice_no_result));
            mTopToast.show(1000);
            LogUtil.e("error ", " data null error code " + errorCode);
            LogUtil.e("error ", " net  error code " + errorCode);
        }
    }

//    public PullToRefreshSwipeMenuListView getPullSwiteView() {
//        return mXListView;
//    }

    public PullToRefreshListView getPullSwiteView(){
        return mXListView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.e("onItemClick", "position0 " + position);
        if (mAlbumLists != null && mAlbumLists.size() > 0) {
            MainActivityAlbum item = mAlbumLists.get(position - 1);
            UploadStat.uploadstat(item, "0", "00S002000_2", "00S002000_1", position + "", "-", "-", "-", "-");
            LogUtil.e("wulianshu", "=====页面点击上报=====");
            VideoDetailItem videoDetailItem = new VideoDetailItem();
            videoDetailItem.setTitle(item.getTitle());
            videoDetailItem.setDescription(item.getDescription());
            videoDetailItem.setId(item.getId());
            videoDetailItem.setCategory_id(item.getCategory_id());
            videoDetailItem.setPlay_count(item.getPlay_count());
            videoDetailItem.setVideoItems(item.getVideos());
            // 视频来源
            videoDetailItem.setBucket(item.getBucket());
            videoDetailItem.setReid(item.getReId());
            videoDetailItem.setSource(item.getSource());
            videoDetailItem.setFromMainContentType(item.getContentType());
            videoDetailItem.setDetailImage(item.getImgage());

            if("0".equals(ChaoJiShiPinMainActivity.isCheck)) {
                Intent intent = new Intent(getActivity(), ChaoJiShiPinVideoDetailActivity.class);

                intent.putExtra("videoDetailItem", videoDetailItem);
                intent.putExtra("ref", pageid);
                intent.putExtra("seid", seid);
                //点击上报
                startActivity(intent);
            }else{
                Intent webintent = new Intent(getActivity(),PlayActivityFroWebView.class);
                webintent.putExtra("url", item.getVideos().get(0).getPlay_url());
                webintent.putExtra("title", item.getVideos().get(0).getTitle());
                webintent.putExtra("site", item.getSource());
                webintent.putExtra("videoDetailItem", videoDetailItem);
                startActivity(webintent);
            }
        }
    }



    private boolean isContainItem(String aid) {
       /* boolean isContainValue = false;
        if (null != mAlbumLists) {
            for (int i = 0; i < mAlbumLists.size(); i++) {
                if (mAlbumLists.get(i).getId().equals(aid)) {
                    isContainValue = true;
                    break;
                }
            }
        }
        return isContainValue;*/
        return false;
    }

    /**
     * 分享
     */
    void share(int position)
    {
        MainActivityAlbum item = mAlbumLists.get(position - 1);
        ShareDataConfig config = new ShareDataConfig(getActivity());
        ShareData shareData = config.configShareData(item.getId(),
                item.getTitle(),
                item.getImgage(),
                ShareDataConfig.ALBULM_SHARE,
                null);
        ShareDialog shareDialog = new ShareDialog(getActivity(), shareData, null);
        shareDialog.show();
    }

    void buidlParam() {
        token=UserLoginState.getInstance().getUserInfo().getToken();
        MainActivityAlbum item = mAlbumLists.get(mparentId);
        detail = new VideoDetailItem();
        detail.setTitle(item.getTitle());
        detail.setDescription(item.getDescription());
        detail.setId(item.getId());
        detail.setCategory_id(item.getCategory_id());
        detail.setPlay_count(item.getPlay_count());
        detail.setVideoItems(item.getVideos());
        // 视频来源
        detail.setSource(item.getSource());
        detail.setFromMainContentType(item.getContentType());
        detail.setDetailImage(item.getImgage());

        source = detail.getSource() + "";
        cid = detail.getCategory_id();
        if (TextUtils.isEmpty(detail.getId())) {
            id = detail.getVideoItems().get(0).getGvid();
            type = "2";
        } else {
            // 专辑
            id = detail.getId();
            type = "1";
        }
    }
    /**
     * 点击收藏按钮
     */
    void checkSave() {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ISEXISTS_FAVORITE);
        HttpApi.checkFavorite(id, token, type).start(new RequestListener<CheckFavorite>() {
            @Override
            public void onResponse(CheckFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0 && result.isExists()) {
                    cancelSaveOnLine();
                } else {
                    doSaveOnLine();
                    //上报
//                    doReport();
                }
            }

            @Override
            public void netErr(int errorCode) {

            }

            @Override
            public void dataErr(int errorCode) {

            }
        });
    }

    /**
     * 上报 收藏
     */

    void doReport() {
        DataReporter.reportAddCollection(id, source, cid, type, token, netType);

    }

    /**
     * cancel save
     */
    void cancelSaveOnLine() {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_CANCEL_FAVORITE);
        HttpApi.cancelFavorite(id, token, type).start(new RequestListener<CancelFavorite>() {
            @Override
            public void onResponse(CancelFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0) {
                    //cancelSaveLocal();

                    if (mainActivityChannelAdapter.menuStates != null) {
                        mainActivityChannelAdapter.menuStates.get(mparentId).setIsDelete(false);
                        mainActivityChannelAdapter.menuStates.get(mparentId).setIsSave(false);
                        mainActivityChannelAdapter.menuStates.get(mparentId).setIsSare(false);
                        mainActivityChannelAdapter.notifyDataSetChanged();

                        LogUtil.e("xll", " main activity cancel " + mparentId);
                    }
                }
            }

            @Override
            public void netErr(int errorCode) {

            }

            @Override
            public void dataErr(int errorCode) {

            }
        });
    }

    /**
     * save onLine
     */
    void doSaveOnLine() {
        if (TextUtils.isEmpty(detail.getId())) {
            id = detail.getVideoItems().get(0).getGvid();
            type = "2";
        } else {
            // 专辑
            id = detail.getId();
            type = "1";
        }

        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_ADD_FAVORITE);
        HttpApi.addFavorite(id, token, type, cid, netType, source, bucket, seid).start(new RequestListener<AddFavorite>() {
            @Override
            public void onResponse(AddFavorite result, boolean isCachedData) {
                if (result != null && result.getCode() == 0) {
                    //doSaveLocal();
                    if(mainActivityChannelAdapter.menuStates!=null){
                        mainActivityChannelAdapter.menuStates.get(mparentId).setIsDelete(false);
                        mainActivityChannelAdapter.menuStates.get(mparentId).setIsSave(true);
                        mainActivityChannelAdapter.menuStates.get(mparentId).setIsSare(false);
                        mainActivityChannelAdapter.notifyDataSetChanged();
                        LogUtil.e("xll", "  main activity  save" + mparentId);

                    }

                }
            }

            @Override
            public void netErr(int errorCode) {

            }

            @Override
            public void dataErr(int errorCode) {

            }
        });
    }
    public void uploadstat(AbsListView absListView){
        if(mAlbumLists == null || mAlbumLists.size()<=0){
            return;
        }
        int beginposition =  absListView.getFirstVisiblePosition();
        int endposition = absListView.getLastVisiblePosition();
        if(beginposition<=1){
            beginposition = 1;
        }
        if(endposition>mAlbumLists.size()){
            endposition = mAlbumLists.size();
        }
        StringBuffer sb = new StringBuffer();
        String vid = "";
        for(int j=beginposition-1;j<=endposition-1;j++){
            if(!alreadyupgvid.contains(mAlbumLists.get(j).getVideos().get(0).getGvid()) && j<mAlbumLists.size()){
                if(j < 0){
                    j=0;
                }
                LogUtil.e("wulianshu", "上报的位置:" + j);
                String tmp = mAlbumLists.get(j).getVideos().get(0).getGvid();
                alreadyupgvid.add(tmp);
                sb.append(tmp).append(",");
            }
        }
        vid = sb.toString();
        if(!TextUtils.isEmpty(vid)) {
            vid = vid.substring(0, vid.length() - 1);
            MainActivityAlbum mainActivityAlbum = mAlbumLists.get(beginposition - 1);
            mainActivityAlbum.getVideos().get(0).setGvid(vid);
            //Object object,String acode,String pageid,String ref,String rank,String rid_topcid,String sa,String pn,String input
            UploadStat.uploadstat(mainActivityAlbum, "4", "00S002000_2", "00S002000_1", "-", "-", "-", "-", "-");
        }
    }
    public void uploadstatfirst(AbsListView absListView){
        int endposition = absListView.getLastVisiblePosition();
        int firstposition = absListView.getFirstVisiblePosition();
        String vid = "";
        StringBuffer sb = new StringBuffer();
        for(int j=firstposition;j<=endposition-1;j++){
            if(j<mAlbumLists.size() && !alreadyupgvid.contains(mAlbumLists.get(j).getVideos().get(0).getGvid())){
                if(j < 0){
                    j=0;
                }
                LogUtil.e("wulianshu", "上报的位置first:" + j);
                String tmp = mAlbumLists.get(j).getVideos().get(0).getGvid();
                alreadyupgvid.add(tmp);
                sb.append(tmp).append(",");
//                UploadStat.uploadstat(mAlbumLists.get(j), "4", "00S002000_2", "00S002000_1", j + "", "-", "-", "-", "-");
            }
        }
        vid = sb.toString();
        if(vid == null || vid.length()<= 1){
            return ;
        }
        vid = vid.substring(0, vid.length() - 1);


        MainActivityAlbum mainActivityAlbum = mAlbumLists.get(firstposition);
        mainActivityAlbum.getVideos().get(0).setGvid(vid);
        UploadStat.uploadstat(mainActivityAlbum, "4", "00S002000_2", "00S002000_1", "-", "-", "-", "-", "-");
    }
}
