package com.chaojishipin.sarrs.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.activity.SearchActivity;
import com.chaojishipin.sarrs.adapter.MainActivityChannelAdapter2;
import com.chaojishipin.sarrs.bean.MainActivityAlbum;
import com.chaojishipin.sarrs.bean.MainActivityData;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.swipe.SwipeMenu;
import com.chaojishipin.sarrs.swipe.SwipeMenuCreator;
import com.chaojishipin.sarrs.swipe.SwipeMenuItem;
import com.chaojishipin.sarrs.swipe.SwipeMenuListView;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/6/17.
 */
public class MainChannelFragment2 extends ChaoJiShiPinBaseFragment  implements SwipeMenuListView.OnMenuItemClickListener,SwipeMenuListView.OnSwipeListener,View.OnClickListener,SwipeMenuListView.OnRefreshListener,
        SwipeMenuListView.OnLoadListener,AdapterView.OnItemClickListener {

    private SwipeMenuListView mXListView;

    private MainActivityChannelAdapter2 mainActivityChannelAdapter;

    private String mCid;

    private String mArea;
    private String mCurrentTitle = ConstantUtils.TITLE_SUGGEST;

    private final static int MESSAGE_DELAYED_TIME = 3000;

    private ArrayList<MainActivityAlbum> mAlbumLists;

    private int reQMode=1;
    private ImageView mSearchIcon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.mainactivity_channel_layout2, container, false);
        initView(view);
        return view;
    }

    @Override
    protected void handleInfo(Message msg) {

        }



    private void initView(View view) {
        mXListView = (SwipeMenuListView) view.findViewById(R.id.mainchannle_fragment_listview2);
        mainActivityChannelAdapter = new MainActivityChannelAdapter2(getActivity());
        mXListView.setAdapter(mainActivityChannelAdapter);
        // set creator
        mXListView.setMenuCreator(creator);
        mXListView.setOnMenuItemClickListener(this);
        mXListView.setOnSwipeListener(this);
        mXListView.setOnRefreshListener(this);
        mXListView.setOnLoadListener(this);
        mSearchIcon = (ImageView) view.findViewById(R.id.search_icon);
        mSearchIcon.setOnClickListener(this);
        mXListView.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        reQMode=0;
        requestChannelData(getActivity(),mCid,ConstantUtils.MAINACTIVITY_REFRESH_AREA);
    }

    @Override
    public void onLoad() {
        reQMode=1;
        requestChannelData(getActivity(),mCid,ConstantUtils.MAINACTIVITY_LOAD_AREA);
    }




    /**
     * 请求具体的频道数据
     *
     * @param cid
     */
    private void requestChannelData(Context context,String cid, String area) {
        mCid = cid;
        mArea = area;
        //请求频道页数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_MAINACTIVITY_DATA);
        HttpApi.
                getMainActivityDataRequest(context,cid, area)
                .start(new RequestChannelListener(), ConstantUtils.REQUEST_MAINACTIVITY_DATA);
    }






    public void onEventMainThread(SlidingMenuLeft slidingMenuLeft) {

        String cid = null;
        if (ConstantUtils.TITLE_SUGGEST.equals(slidingMenuLeft.getTitle())) {
            cid = "0";
        } else {
            cid = slidingMenuLeft.getCid();
        }
        if (mCurrentTitle.equals(slidingMenuLeft.getTitle())) {
            reQMode=3;
        } else {
            reQMode=4;
            mCurrentTitle = slidingMenuLeft.getTitle();
        }
        requestChannelData(getActivity(),cid, ConstantUtils.MAINACTIVITY_REFRESH_AREA);
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
            shareItem.setWidth(Utils.dip2px( 75));
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.search_icon:

                buildDrawingCacheAndIntent();
                break;

            default:
                break;

        }
    }


    /**
     * 截屏，保存为Bitmap，提供给SearchAvtivity高斯模糊使用
     * @auth daipei
     */
    public void buildDrawingCacheAndIntent() {
        SearchActivity.launch(this.getActivity());
    }

    @Override
    public void onMenuItemClick(int position, SwipeMenu menu, int index) {
        MainActivityAlbum item=null;
        if (null != mAlbumLists && mAlbumLists.size() > 0) {
             item= mAlbumLists.get(position);
        }
        switch (index){

            case 0:
                ToastUtil.showShortToast(getActivity(), "click 1");
                mAlbumLists.remove(position);
                mainActivityChannelAdapter.notifyDataSetChanged();

                break;

            case 1:
                ToastUtil.showShortToast(getActivity(),"click 0");
                break;

            case 2:
                ToastUtil.showShortToast(getActivity(),"click 2");
                break;


        }


    }

    @Override
    public void onSwipeStart(int position) {

    }

    @Override
    public void onSwipeEnd(int position) {

    }

    private class RequestChannelListener implements RequestListener<MainActivityData> {

        @Override
        public void onResponse(MainActivityData result, boolean isCachedData) {
            if (null != result) {
                ArrayList<MainActivityAlbum> albums = result.getAlbumList();
                if (null != albums && albums.size() > 0) {
                    int albumsSize = albums.size();
                    if (null == mAlbumLists) {
                        mAlbumLists = new ArrayList<MainActivityAlbum>();
                    }
                    if(reQMode==0){
                        mXListView.onRefreshComplete();
                            for (int i = 0; i < albumsSize; i++) {
                                if (!isContainItem(albums.get(i).getId())) {
                                    mAlbumLists.add(0, albums.get(i));
                                }
                            }


                    }else if(reQMode==1){
                        mXListView.onLoadComplete();
                        for (int i = 0; i < albumsSize; i++) {
                            if (!isContainItem(albums.get(i).getId())) {
                                int index=mAlbumLists.size();
                                mAlbumLists.add(index, albums.get(i));

                            }
                        }
                    }else if(reQMode==3||reQMode==4){
                        mAlbumLists.clear();
                        for (int i = 0; i < albumsSize; i++) {
                            if (!isContainItem(albums.get(i).getId())) {
                                int index=mAlbumLists.size();
                                mAlbumLists.add(index, albums.get(i));
                            }
                        }
                    }


                    // 根据加载条数判断是否没有数据
                   // mXListView.setResultSize(mAlbumLists.size());
                    LogUtil.e("MainChannelFragment",""+mAlbumLists.size());
                    LogUtil.e("MainChannelFragment","mode "+reQMode);
                    if (null != mainActivityChannelAdapter) {
                        mainActivityChannelAdapter.setmAlbums(mAlbumLists);
                        mainActivityChannelAdapter.notifyDataSetChanged();
                    } else {
                        mainActivityChannelAdapter = new MainActivityChannelAdapter2(getActivity());
                        mainActivityChannelAdapter.setmAlbums(mAlbumLists);
                        mXListView.setAdapter(mainActivityChannelAdapter);
                    }
                }
            }
        }

        @Override
        public void netErr(int errorCode) {
            LogUtil.e("error ", " net  error code " + errorCode);
          /*  mXListView.hideHeaderVeiw();
            mXListView.hideBottomView();*/

        }
        @Override
        public void dataErr(int errorCode) {
            LogUtil.e("error "," data null error code "+errorCode );
            LogUtil.e("error "," net  error code "+errorCode );
//            mXListView.hideHeaderVeiw();
//            mXListView.hideBottomView();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.e("onItemClick","position0 " +position );
        if(mAlbumLists!=null&&mAlbumLists.size()>0){
            Intent intent=new Intent(getActivity(), ChaoJiShiPinVideoDetailActivity.class);
            // header后position+1
            MainActivityAlbum item=mAlbumLists.get(position-1);

            List<VideoItem> videoItems=item.getVideos();
            PlayData playData=null;
            if(videoItems!=null&&videoItems.size()>0){
                playData=new PlayData(videoItems.get(0).getTitle(),videoItems.get(0).getGvid(),ConstantUtils.PLAYER_FROM_MAIN,videoItems.get(0).getSource());
            }
            intent.putExtra("playData", playData);
            VideoDetailItem videoDetailItem=new VideoDetailItem();
            videoDetailItem.setTitle(item.getTitle());
            videoDetailItem.setDescription(item.getDescription());
            videoDetailItem.setId(item.getId());
            videoDetailItem.setCategory_id(item.getCategory_id());
            videoDetailItem.setPlay_count(item.getPlay_count());
            videoDetailItem.setVideoItems(item.getVideos());
            videoDetailItem.setFromMainContentType(item.getContentType());
            videoDetailItem.setDetailImage(item.getImgage());
            intent.putExtra("videoDetailItem", videoDetailItem);
            startActivity(intent);
        }

    }

    private boolean isContainItem(String aid) {
        boolean isContainValue = false;
        if (null != mAlbumLists) {
            for (int i = 0; i < mAlbumLists.size(); i++) {
                if (mAlbumLists.get(i).getId().equals(aid)) {
                    isContainValue = true;
                }
            }
        }
        return isContainValue;
    }





}
