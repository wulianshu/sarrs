package com.chaojishipin.sarrs.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinTopicDetailActivity;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.adapter.BatchFavoriteAdapter;
import com.chaojishipin.sarrs.adapter.SaveListAdapter;
import com.chaojishipin.sarrs.bean.BatichFavoriteInfos;
import com.chaojishipin.sarrs.bean.DateTag;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.bean.FavoriteInfos;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.download.Constants;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.BitchFavoriteListener;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.manager.FavoriteManager;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.DateTagUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.widget.NetStateView;
import com.chaojishipin.sarrs.widget.PullToRefreshSwipeMenuListView;
import com.chaojishipin.sarrs.widget.SarrsMainMenuView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xll on 2015/8/30.
 */
public class SaveFragment extends ChaoJiShiPinBaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, SarrsMainMenuView.onSlideMenuItemClick, onRetryListener, AbsListView.OnScrollListener {
    public static final String pageid ="00S0020014";
    private ImageView save_back;
    private TextView save_edit;
    private PullToRefreshSwipeMenuListView mListView;
    private TextView save_all;
    private TextView confirm_delete;
    private RelativeLayout mSavenoResult, mSaveContent;
    private List<Favorite> mainList = new ArrayList<>();
    private List<String> idList = new ArrayList<>();
    private List<Favorite> list;
    private SaveListAdapter adapter;
    private boolean isEditMode;
    //    private boolean isSelectAll;
    private RelativeLayout save_edit_layout;
    // 服务端返回数据开始位置
    private int startIndex = 0;
    // 服务端返回数据条数
    private int limit = 20;
    private NetStateView mNetView;
    //左上按钮状态:true edit, false:complete
    private boolean editable = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SarrsMainMenuView.listviewItemHeight = 84;
        SarrsMainMenuView.mode = ConstantUtils.SarrsMenuInitMode.MODE_DELETE;
        View contentView = inflater.inflate(R.layout.fragment_save, null);
        initView(contentView);
        // initSwipe();
        initPullToReshMode();
        // initDataLocal();
        initListener();
        if (NetWorkUtils.isNetAvailable()) {
            initDataOnline();
        } else {
            mNetView.setVisibility(View.VISIBLE);
            mSavenoResult.setVisibility(View.GONE);
            mSaveContent.setVisibility(View.GONE);
        }
        return contentView;
    }

    /**
     * 初始化 pull to refresh mode
     */
    void initPullToReshMode() {

        mListView.setMode(PullToRefreshSwipeMenuListView.Mode.PULL_FROM_END);
        mListView.getLoadingLayoutProxy(false, true).setPullLabel(getActivity().getString(R.string.pull_to_refresh_load_more_lable));
        mListView.getLoadingLayoutProxy(false, true).setReleaseLabel(getActivity().getString(R.string.pull_to_refresh_load_more_release));
        mListView.getLoadingLayoutProxy(false, true).setRefreshingLabel(getActivity().getString(R.string.pull_to_refresh_load_more_loading));
    }


    /**
     * 初始化view
     */
    void initView(View cv) {
        save_back = (ImageView) cv.findViewById(R.id.save_back);
        save_edit = (TextView) cv.findViewById(R.id.save_edit);
        save_all = (TextView) cv.findViewById(R.id.all_save);
        mSavenoResult = (RelativeLayout) cv.findViewById(R.id.save_no_layout);
        mSaveContent = (RelativeLayout) cv.findViewById(R.id.save_content);
        confirm_delete = (TextView) cv.findViewById(R.id.confirm_delete);
        save_edit_layout = (RelativeLayout) cv.findViewById(R.id.save_edit_layout);
        mListView = (PullToRefreshSwipeMenuListView) cv.findViewById(R.id.save_list_view);
        save_edit_layout.setVisibility(View.GONE);
        mNetView = (NetStateView) cv.findViewById(R.id.save_net_layout);
        if (!NetWorkUtils.isNetAvailable()) {
            mNetView.setVisibility(View.VISIBLE);
            mSavenoResult.setVisibility(View.GONE);
            mSaveContent.setVisibility(View.GONE);
            save_edit.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 初始化listener
     */

    void initListener() {
        save_back.setOnClickListener(this);
        save_edit.setOnClickListener(this);
        save_all.setOnClickListener(this);
        confirm_delete.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnMenuItemClick(this);
        mNetView.setOnRetryLisener(this);
        mListView.setOnScrollListener(this);
        mListView.setSwipeMenuStatusListener(swipeMenuStatusListener);
    }


    @Override
    public void onRetry() {
        requestFavoriteList(startIndex, limit);
    }

    /**
     * initData local
     */
    void initDataLocal() {
        list = FavoriteManager.getInstanse().queryAll();
        list.get(0).setIsShowTag(true);
        if (adapter == null) {
            adapter = new SaveListAdapter(getActivity(), list);
        }
        adapter.setIsShowCheck(false);
        mListView.setAdapter(adapter);
        if (!isEditMode) {
            mListView.setSwipeable(true);
        }
    }


    /**
     * initData online
     */
    void initDataOnline() {
        if (mainList != null) {
            mainList.clear();
        }
        requestFavoriteList(startIndex, limit);


        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (list != null) {
                    int size = list.size();
                    startIndex = startIndex + list.size();
                    list.clear();
                }
                LogUtil.e("xll", "startIndex " + startIndex);
                LogUtil.e("xll", "limit " + limit);
                requestFavoriteList(startIndex, limit);


            }
        });

    }

    int firstItem;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstItem = firstVisibleItem;
        LogUtil.e("xll ", " first visible " + firstVisibleItem);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * 刚进入网络请求收藏列表
     */
    void requestFavoriteList(final int index, int limit) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_GET_FAVORITE_RESULT);
        HttpApi.getFavoriteList(UserLoginState.getInstance().getUserInfo().getToken(), index, limit).start(new RequestListener<FavoriteInfos>() {
            @Override
            public void onResponse(FavoriteInfos result, boolean isCachedData) {
                if (result != null && result.getStatus() != null && result.getStatus().equalsIgnoreCase(ConstantUtils.REQUEST_SUCCESS)) {
                    list = result.getFs();
                    if (mainList != null && list != null) {
                        //去除重复数据
                        //TODO 服务端返回重复数据（王迪）
                        // mainList.removeAll(list);
                        mainList.addAll(list);

                    }
                    if (mainList.size() == 0) {
                        save_edit.setVisibility(View.INVISIBLE);
                        save_edit_layout.setVisibility(View.GONE);
                        mSaveContent.setVisibility(View.GONE);
                        mNetView.setVisibility(View.GONE);
                        mSavenoResult.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        save_edit.setVisibility(View.VISIBLE);
                        confirm_delete.setVisibility(View.VISIBLE);
                        mSaveContent.setVisibility(View.VISIBLE);
                        mSavenoResult.setVisibility(View.GONE);
                        mNetView.setVisibility(View.GONE);
                    }

                    mListView.onRefreshComplete();
                    if (mainList != null && mainList.size() > 0) {
                        //本地拉取
                        //  FavoriteManager.getInstanse().saveBatch(mainList);
                    }

                    if (adapter == null) {
                        adapter = new SaveListAdapter(getActivity(), mainList);
                        buildCheckList();
                        adapter.setIsShowCheck(false);
                        mListView.setAdapter(adapter);
                    } else {
                        adapter.setData(mainList);
                        buildCheckList();
                        adapter.setIsShowCheck(false);
                        adapter.notifyDataSetChanged();
                    }
                    mListView.getRefreshableView().setSelection(firstItem);

                    if (!isEditMode) {
                        mListView.setSwipeable(true);
                    }


                }
            }

            @Override
            public void netErr(int errorCode) {
                mNetView.setVisibility(View.VISIBLE);
                mSavenoResult.setVisibility(View.GONE);
                mSaveContent.setVisibility(View.GONE);
            }

            @Override
            public void dataErr(int errorCode) {
                LogUtil.e("xll", " data error !");
            }
        });


    }

    void buildCheckList() {
        adapter.isshowtimelist = null;
        adapter.isshowtimelist = new ArrayList<Boolean>();
        if (mainList.size() > 0) {
            for (int i = 0; i < mainList.size(); i++) {
                if (i == 0) {
                    adapter.isshowtimelist.add(true);
                } else {
                    String stime1 = mainList.get(i - 1).getCreateDate();
                    String stime2 = mainList.get(i).getCreateDate();
                    if (stime1 != null) {
                        if (stime1.equals(stime2)) {
                            adapter.isshowtimelist.add(false);
                        } else {
                            adapter.isshowtimelist.add(true);
                        }
                    } else {
                        adapter.isshowtimelist.add(false);
                    }

                }
            }
        }

    }


    void removeLocal(int position) {

        Favorite f = null;
        if (list != null) {
            f = list.get(position);
            if (f != null) {
                int iret = FavoriteManager.getInstanse().deletById(String.valueOf(f.getId()));

                if (iret == 0) {
                    ToastUtil.showShortToast(getActivity(), "删除成功");
                }
            }

            list.remove(position);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }

    }

    boolean isByClick;
    int mCurrentPositon = -1;

    @Override
    public void onItemClick(int position, View view, int parentId, ListAdapter adapter) {
        mCurrentPositon = parentId - 1;
        LogUtil.e("xll", "delete batch  by swipeMenu! ");
        switch (position) {
            case 0:
                LogUtil.e("xll", "delete batch  by swipeMenu! ");
                isByClick = true;
                deleteByCheckOnline();
                break;

        }
        save_edit.setText(getResources().getString(R.string.edit));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.all_save:
//                if(adapter.deletecount == mainList.size()){
//                    isSelectAll = true;
//                }else{
//                    isSelectAll = false;
//                }
                saveAllClick();
                updateSelectDeleteButton();
                break;

            case R.id.save_back:
                getActivity().finish();

                break;

            case R.id.save_edit:
                adapter.deletecount = 0;
                if (!editable) {
                    mListView.closeMenu();
                    return;
                }
                isEditMode = !isEditMode;
                setTextByEditMode();
                break;
            case R.id.confirm_delete:
                adapter.deletecount = 0;
                isByClick = false;
                //删除完退出编辑状态
                initPullToReshMode();
                save_edit.setText(getString(R.string.edit));
                if (adapter != null) {
                    adapter.setIsShowCheck(false);
                }
                save_edit_layout.setVisibility(View.GONE);
                mListView.setSwipeable(true);

                deleteByCheckOnline();
                updateSelectDeleteButton();
                break;

        }

    }

    /**
     * 删除按钮点击
     */
    void deleteByCheckOnline() {
        String json = "";
        if (isByClick) {
            LogUtil.e("xll", "mCurrentPositon " + mCurrentPositon);
            json = BatchFavoriteAdapter.getInstanse().wrapItemsByClick(mainList, mCurrentPositon);
        } else {
            json = BatchFavoriteAdapter.getInstanse().wrapItems(mainList);
        }

        LogUtil.e("xll", " json wrap : " + json);
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_FAVORITE_BATCH);
        HttpApi.batchFavoriteList(UserLoginState.getInstance().getUserInfo().getToken(), json, new BitchFavoriteListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(String response) {
                LogUtil.e("xll", "batch favorite " + response);
                deleteByCheckLocal();
            }
        });

    }

    /**
     * 删除按钮点击
     */
    void deleteByCheckLocal() {
        int size = mainList.size();
        if (mainList != null && mainList.size() > 0) {

            if (isByClick) {
                String type = mainList.get(mCurrentPositon).getType();
                if (type != null) {
                    if (type.equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_ALBUM)) {
                        int iret = FavoriteManager.getInstanse().deletById(mainList.get(mCurrentPositon).getAid() + "");
                        if (iret == 0) {
                            ToastUtil.showShortToast(getActivity(), getString(R.string.delete_success));
                        }
                    } else if (type.equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SINGLE)) {
                        int iret = FavoriteManager.getInstanse().deletById(mainList.get(mCurrentPositon).getGvid() + "");
                        if (iret == 0) {
                            ToastUtil.showShortToast(getActivity(), getString(R.string.delete_success));
                        }
                    } else if (type.equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SPECIAL)) {
                        int iret = FavoriteManager.getInstanse().deletById(mainList.get(mCurrentPositon).getTid() + "");
                        if (iret == 0) {
                            ToastUtil.showShortToast(getActivity(), getString(R.string.delete_success));
                        }
                    }
                }
                //注意线程同步
                synchronized (mainList) {
                    mainList.remove(mCurrentPositon);
                }

            } else {
                //注意线程同步
                int iret = FavoriteManager.getInstanse().deletePatch(mainList);
                if (iret == 0) {
                    ToastUtil.showShortToast(getActivity(), getString(R.string.delete_success));
                }
                Iterator<Favorite> iterator = mainList.iterator();
                while (iterator.hasNext()) {
                    Favorite f = iterator.next();
                    if (f.isCheck()) {
                        iterator.remove();
                        LogUtil.e("xll", "  remove index " + f.getTitle());
                    }

                }
            }
            if (mainList.size() == 0) {
                save_edit.setVisibility(View.INVISIBLE);
                save_edit_layout.setVisibility(View.GONE);
                mSaveContent.setVisibility(View.GONE);
                mSavenoResult.setVisibility(View.VISIBLE);
                return;
            } else {
                LogUtil.e("xll", " after remove size " + mainList.size());
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

        }

    }

    /**
     * 全选按钮点击
     */

    void saveAllClick() {
        if (adapter.deletecount < mainList.size()) {
            adapter.deletecount = mainList.size();
            save_all.setText(getString(R.string.deselect_all));
            confirm_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
            if (adapter != null) {
                if (mainList != null) {
                    for (Favorite f : mainList) {
                        f.setIsCheck(true);
                    }
                }
            }
        } else {
            adapter.deletecount = 0;
            save_all.setText(getString(R.string.check_all));
            confirm_delete.setTextColor(getResources().getColor(R.color.all_select));
            if (adapter != null) {
                if (mainList != null) {
                    for (Favorite f : mainList) {
                        f.setIsCheck(false);
                    }
                }


            }
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();


    }

    /**
     * 编辑按钮点击
     */
    void setTextByEditMode() {
        if (isEditMode) {
            mListView.setMode(PullToRefreshSwipeMenuListView.Mode.DISABLED);
            mListView.setSwipeable(false);
            save_edit.setText(getString(R.string.complete));
            save_all.setText(getString(R.string.check_all));
            confirm_delete.setText(getString(R.string.delete_up));
            confirm_delete.setTextColor(getResources().getColor(R.color.all_select));
            save_edit_layout.setVisibility(View.VISIBLE);
            if (adapter != null) {
                if (mainList != null) {
                    for (Favorite f : mainList) {
                        f.setIsCheck(false);
                    }
                }
                adapter.setIsShowCheck(true);

            }
        } else {
            initPullToReshMode();
            save_edit.setText(getString(R.string.edit));
            if (adapter != null) {
                adapter.setIsShowCheck(false);
            }
            save_edit_layout.setVisibility(View.GONE);
            mListView.setSwipeable(true);

        }


    }


    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCurrentPositon = position - 1;
        adapter.deletecount = 0;
        LogUtil.e("xll", "menu onItemClick positon " + position);
        if (isEditMode) {
            if (adapter != null) {
                if (mainList != null) {
                    mainList.get(mCurrentPositon).setIsCheck(!mainList.get(mCurrentPositon).isCheck());
                    if (mainList.get(mCurrentPositon).isCheck()) {
                        confirm_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
                    }
                }
                for (Favorite favorite : mainList) {
                    if (favorite.isCheck()) {
                        adapter.deletecount++;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            updateSelectDeleteButton();
        } else {
            //
            if (mainList.get(mCurrentPositon).getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SINGLE)) {
                Intent intent = new Intent(getActivity(), ChaoJiShiPinVideoDetailActivity.class);
                VideoDetailItem itemdetail = new VideoDetailItem();
                ArrayList<VideoItem> items = new ArrayList<>();
                VideoItem item = new VideoItem();
                item.setCategory_id(mainList.get(mCurrentPositon).getCid());
                item.setGvid(mainList.get(mCurrentPositon).getGvid());
                item.setSource(mainList.get(mCurrentPositon).getSource());
                item.setTitle(mainList.get(mCurrentPositon).getTitle());
                items.add(item);
                itemdetail.setSource(mainList.get(mCurrentPositon).getSource());
                itemdetail.setVideoItems(items);
                itemdetail.setCategory_id(mainList.get(mCurrentPositon).getCid());
                itemdetail.setDescription(item.getDescription());
                intent.putExtra("videoDetailItem", itemdetail);
                intent.putExtra("ref",pageid);
                LogUtil.e("xll", "onItemClick single");
                startActivity(intent);


            } else if (mainList.get(mCurrentPositon).getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_ALBUM)) {

                Intent intent = new Intent(getActivity(), ChaoJiShiPinVideoDetailActivity.class);

                VideoDetailItem itemdetail = new VideoDetailItem();
                itemdetail.setId(mainList.get(mCurrentPositon).getAid());
                ArrayList<VideoItem> items = new ArrayList<>();
                VideoItem item = new VideoItem();
                //TODO  收藏未完结专辑点击进入半屏页播放最新剧集
                item.setCategory_id(mainList.get(mCurrentPositon).getCid());
                item.setGvid(mainList.get(mCurrentPositon).getGvid());
//               item.setId(mainList.get(mCurrentPositon).getId());
                item.setSource(mainList.get(mCurrentPositon).getSource());
                item.setTitle(mainList.get(mCurrentPositon).getTitle());
                items.add(item);
                itemdetail.setTitle(mainList.get(mCurrentPositon).getTitle());
                itemdetail.setVideoItems(items);
                itemdetail.setSource(mainList.get(mCurrentPositon).getSource());
                itemdetail.setCategory_id(mainList.get(mCurrentPositon).getCid());
                intent.putExtra("videoDetailItem", itemdetail);
                intent.putExtra("ref",pageid);
                LogUtil.e("xll", "onItemClick Album");
                startActivity(intent);

            } else if (mainList.get(mCurrentPositon).getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SPECIAL)) {

                Intent intent = new Intent(getActivity(), ChaoJiShiPinTopicDetailActivity.class);
                Topic topic = new Topic();
                topic.setTid(mainList.get(mCurrentPositon).getTid());
                topic.setTitle(mainList.get(mCurrentPositon).getTitle());
                topic.setImage(mainList.get(mCurrentPositon).getImg());

                //TODO 服务端des字段
                Bundle bundle = new Bundle();
                bundle.putSerializable("topic", topic);
                intent.putExtras(bundle);
                LogUtil.e("xll", "onItemClick Specail");
                startActivity(intent);
            }
        }

    }


    private void setEditable(boolean editable) {
        this.editable = editable;
        if (editable) {
            save_edit.setText(getString(R.string.edit));
        } else {
            save_edit.setText(getString(R.string.complete));
        }
    }

    PullToRefreshSwipeMenuListView.SwipeMenuStatusListener swipeMenuStatusListener = new PullToRefreshSwipeMenuListView.SwipeMenuStatusListener() {
        @Override
        public void onMenuSmoothOpen() {
            setEditable(false);
        }

        @Override
        public void onMenuSmoothClose() {
            setEditable(true);
        }
    };

    public void updateSelectDeleteButton() {
        if (adapter.deletecount == mainList.size()) {
            save_all.setText(getResources().getString(R.string.deselect_all));
        } else if (adapter.deletecount < mainList.size()) {
            save_all.setText(getResources().getString(R.string.check_all));
        }
        if (adapter.deletecount > 0) {
            confirm_delete.setClickable(true);
            confirm_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
        } else {
            confirm_delete.setTextColor(getResources().getColor(R.color.color_666666));
            confirm_delete.setClickable(false);
        }
    }
}
