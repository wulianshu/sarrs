package com.chaojishipin.sarrs.fragment;

import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.widget.NetStateView;
import com.chaojishipin.sarrs.widget.SarrsToast;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by liuzhuo on 2016/1/20.
 */
public abstract class MainBaseFragment extends ChaoJiShiPinBaseFragment implements onRetryListener {

    protected View mView;
    protected SarrsToast mTopToast;
    protected PullToRefreshListView mXListView;
    protected ImageView mSearchIcon;
    protected ListView mLv;
    private SparseArray<NetStateView> mArray = new SparseArray<>();
    protected RelativeLayout mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView == null){
            mView = inflater.inflate(R.layout.mainactivity_channel_layout2, container, false);
            initView(mView);
            init();

        }else if(mView.getParent() != null){
            ((ViewGroup)mView.getParent()).removeView(mView);
        }

        return mView;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    protected void initView(View view) {
        mRootView = (RelativeLayout)view.findViewById(R.id.root);
        mTopToast = (SarrsToast) view.findViewById(R.id.sarrs_top_toast);
        mXListView = (PullToRefreshListView) view.findViewById(R.id.mainchannle_fragment_listview2);
        mLv = mXListView.getRefreshableView();
        mSearchIcon = (ImageView) view.findViewById(R.id.search_icon);
//        mXListView.setSwipeable(false);
    }

    protected abstract void init();
    protected abstract void requestData();

    @Override
    public void onRetry() {
        getNetData();
    }

    public void getNetData(){
        if (NetWorkUtils.isNetAvailable()) {
            hideErrorView(mRootView);
            requestData();
        } else {
            showErrorView(mRootView);
        }
    }

    protected void showErrorView(RelativeLayout root){
        int key = root.hashCode();
        if(mArray.get(key, null) != null)
            return;
        NetStateView v = new NetStateView(getActivity());
        v.setOnRetryLisener(this);
        mArray.put(key, v);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        root.addView(v, params);
    }

    protected void hideErrorView(RelativeLayout root){
        int key = root.hashCode();
        NetStateView v = mArray.get(key, null);
        if(v == null)
            return;
        v.setOnRetryLisener(null);
        root.removeView(v);
        mArray.remove(key);
    }
}
