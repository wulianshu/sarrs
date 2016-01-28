package com.chaojishipin.sarrs.download.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinMainActivity;
import com.chaojishipin.sarrs.download.activity.DownloadActivity;
import com.chaojishipin.sarrs.download.download.ContainSizeManager;
import com.chaojishipin.sarrs.download.download.DownloadJob;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.thirdparty.swipemenulistview.SwipeMenuLayout;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.Utils;

import java.util.ArrayList;

/**
 * Created by vicky on 15/9/5.
 */
public class DownloadFragment extends Fragment implements
        View.OnClickListener {
    private ViewGroup rootView;

    private ImageView mSelectBar;
    private int mImageWidth;
    private int mOffset = 0;
    private ArrayList<android.support.v4.app.Fragment> fragmentsList;
    private DownloadListFragment mDownloadListFragment;
    private int mDeletePopWindowHeight = 77;
    private PopupWindow mCheckAllPopWindow;
    private RelativeLayout mCheckTabLayout;
    private TextView mCheckTabText;
    //    public ContainSizeManager mSizeManager;
    public RelativeLayout memory_info;
    private String mTitleName;

    //网络变化广播接收
    private NetworkCheckReceiver mCheckReceiver;

    public ActionBar mActionBar;
    //是否显示mFilterButton
    public boolean isShowFilterButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_download, container, false);
//        initViewPager();// 1
        initContent();
        mTitleName = (String) getActivity().getIntent().getSerializableExtra(Utils.VIDEO_TITLE);
        initCheckTabPopWindow();
        showAvailableSpace();
        memory_info = (RelativeLayout) rootView.findViewById(R.id.available_space_layout);
//        ChaoJiShiPinApplication.getInstatnce().setActivityStack(getActivity());
        return rootView;
    }

    private void showAvailableSpace() {
        if (ContainSizeManager.getInstance() != null) {
            ContainSizeManager.getInstance().setView(this.getActivity());
            ContainSizeManager.getInstance().ansynHandlerSdcardSize();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerCheckNetwork();
        showAvailableSpace();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterCheckNetwork();
    }

    private void initContent() {
        mDownloadListFragment = new DownloadListFragment();
        mDownloadListFragment.downloadFragment = this;
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.download_content, mDownloadListFragment);
        fragmentTransaction.commit();

    }

    /**
     * @param isShowFilterButton 是否显示
     */
    public void setFilterButtonState(boolean isShowFilterButton) {
        this.isShowFilterButton = isShowFilterButton;
        if (getActivity() instanceof ChaoJiShiPinMainActivity) {
            ((ChaoJiShiPinMainActivity) getActivity()).updateDeleteIcon();
        } else {
            if(getActivity() !=null) {
                ((DownloadActivity) getActivity()).updateDeleteIcon();
            }
        }
        if (getActivity() != null) {
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    private void updateDeleteIcon() {
        if (DataUtils.getInstance().getCompletedDownloads().size() > 0) {
            setFilterButtonState(true);
        } else {
            setFilterButtonState(false);
        }
    }

    private void autoScanLocalVideo() {
//		if (mCurrItem == 1 && null != mLocalVideoFragment
//				&& !mLocalVideoFragment.mIsScaning) {
//			mLocalVideoFragment.doFirstScanLocalVideo();
//		}
    }

    public void restoreDeleteView() {
//        mTitle.setVisibility(View.VISIBLE);
//        mDownloadListFragment.restoreDeleteView();
        mDownloadListFragment.mConfirm_delete.setText(R.string.delete_up);
        mDownloadListFragment.mConfirm_delete.setTextColor(getResources().getColor(R.color.all_select));
//		mUserDeletecount.setVisibility(View.GONE);

//		mDeleteIcon.setBackgroundResource(R.drawable.pic_delete_normal1);
    }

    private void cancelDelete(boolean isAll) {
        if (isAll) {
            mDownloadListFragment.mUserDeletecount.setText(R.string.check_all);
        } else {
            mDownloadListFragment.mUserDeletecount.setText(R.string.deselect_all);
        }
    }

    public void deleteView(int deleteNum) {
//		mTitle.setVisibility(View.GONE);
//		mUserDeletecount.setVisibility(View.VISIBLE);
        String content = getString(R.string.delete_up);
//        + " (" + deleteNum
//                + ")";
        mDownloadListFragment.mConfirm_delete.setText(content);
        mDownloadListFragment.mConfirm_delete.setTextColor(getResources().getColor(R.color.color_FF1E27));
//		mDeleteIcon.setBackgroundResource(R.drawable.pic_delete_highlight_normal1);
//		checkUserSelectAll();
    }

    public void checkUserSelectAll() {
        if (null != mDownloadListFragment.adapter) {
            boolean isAll = mDownloadListFragment.adapter.deletedNum == DataUtils.getInstance()
                    .getRemainNum() ? true : false;
            setCheckTabText(isAll);
        }
    }

    // 我的下载界面编辑下全选状态判断
    public void checkUserSelectStatus() {
        if (null != mDownloadListFragment.adapter) {
            boolean isAll = mDownloadListFragment.adapter.deletedNum == DataUtils.getInstance()
                    .getRemainNum() ? false : true;
            setCheckTabText(isAll);
        }
    }

    public void setCheckTabText(boolean isAll) {
        if (isAll) {
            mDownloadListFragment.mUserDeletecount.setText(R.string.check_all);
        } else {
            mDownloadListFragment.mUserDeletecount.setText(R.string.deselect_all);
        }
    }

    private void initCheckTabPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View mPopWindowView = inflater
                .inflate(R.layout.user_checkall_tab, null);
        if (null != mPopWindowView) {
            mCheckAllPopWindow = new PopupWindow(mPopWindowView, 232,
                    mDeletePopWindowHeight);
            mCheckAllPopWindow.setFocusable(true);
            mCheckAllPopWindow.setBackgroundDrawable(new BitmapDrawable());
            initPopWindowComponent(mPopWindowView);
        }
    }

    private void initPopWindowComponent(View view) {
        mCheckTabLayout = (RelativeLayout) view
                .findViewById(R.id.check_tab_layout);
        mCheckTabText = (TextView) view.findViewById(R.id.user_checkall_tv);
        mCheckTabLayout.setOnClickListener(this);
    }

    ;

    private void dismissPopWindow() {
        if (mCheckAllPopWindow.isShowing()) {
            mCheckAllPopWindow.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//		case R.id.refresh_layout:
//			if (mCurrItem == 1) {
//				startRotateAni();
////				mLocalVideoFragment.refresh();
//			}
//			break;
//            case R.id.delete_layout:
//                if (mCurrItem == 0) {
//                    ArrayList<DownloadJob> jobsList = ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getProvider().getCompletedDownloads();
//                    if (null != jobsList && jobsList.size() > 0) {
//                        mDownloadListFragment.updateDeleteView(mBottomlayout);
//                        mUserDeletecount.setText(R.string.check_all);
//                        mConfirm_delete.setText(R.string.delete_up);
//                        mConfirm_delete.setTextColor(getResources().getColor(R.color.all_select));
//                    }
//
//                } else if (mCurrItem == 1) {
////				mLocalVideoFragment.updateDeleteView();
//                }
//                break;
//            case R.id.confirm_delete:
//                if (mCurrItem == 0) {
//                    mDownloadListFragment.updateConfirmDeleteView(mBottomlayout);
//                } else if (mCurrItem == 1) {
////				mLocalVideoFragment.updateDeleteView();
//                }
//                break;
////		case R.id.check_tab_layout:
////			if (null != mCheckAllPopWindow) {
////				if (mCurrItem == 0) {
////					mDownloadListFragment.selectDownloadVideo();
////				} else if (mCurrItem == 1) {
//////					mLocalVideoFragment.localVideoSelected();
////				}
////				dismissPopWindow();
////			}
////			break;
////		case R.id.userselectdeleteitem:
////			if (mCurrItem == 0) {
////				if (null != mDownloadListFragment.adapter) {
////					boolean isAll = mDownloadListFragment.adapter.deletedNum == mDownloadListFragment
////							.getItemCount() ? true : false;
////					setCheckTabText(isAll);
////				}
////			} else if (mCurrItem == 1) {
//////				if(null != mLocalVideoFragment.mLocalVideoAdapter) {
//////					int deleteNum = mLocalVideoFragment.mLocalVideoAdapter
//////							.getmUserDeleteNum();
//////					boolean isAll = deleteNum == mLocalVideoFragment.mLocalVideoAdapter
//////							.getCount() ? true : false;
//////					setCheckTabText(isAll);
//////				}
////			}
////			onPopWindowSelected();
////			break;
//
//            case R.id.all_select:
//                if (mCurrItem == 0) {
//                    checkUserSelectAll();
////				if (null != mDownloadListFragment.adapter) {
////					boolean isAll = mDownloadListFragment.adapter.deletedNum == mDownloadListFragment
////							.getRemainNum() ? true : false;
////					setCheckTabText(isAll);
////				}
//                    mDownloadListFragment.selectDownloadVideo();
//                } else if (mCurrItem == 1) {
////				if(null != mLocalVideoFragment.mLocalVideoAdapter) {
////					int deleteNum = mLocalVideoFragment.mLocalVideoAdapter
////							.getmUserDeleteNum();
////					boolean isAll = deleteNum == mLocalVideoFragment.mLocalVideoAdapter
////							.getCount() ? true : false;
////					setCheckTabText(isAll);
////				}
//                }
//                break;
            case R.id.leftButtonLayout:
//                onClickBackButton();
                break;
            default:
                break;
        }
    }

    /**
     * qinguoli
     * 网络广播监听*
     */
    class NetworkCheckReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if(DownloadFragment.this.isDetached()){
//            }
            if (intent != null) {
                //读取保存的之前网络状态
                SharedPreferences priorNetState = ChaoJiShiPinApplication.getInstatnce().getSharedPreferences("priornetstate",
                        Activity.MODE_PRIVATE);

                int priorNetType = priorNetState.getInt("netstate", 2);

                int nowNetType = NetworkUtil.checkNet(getActivity().getApplicationContext(), intent, priorNetType);


                SharedPreferences.Editor editor = priorNetState.edit();

                editor.putInt("netstate", nowNetType);
                editor.commit();

            }
        }
    }

    /**
     * qinguoli
     * 注册监听手机网络广播 接收
     */
    protected void registerCheckNetwork() {
        mCheckReceiver = new NetworkCheckReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(PlayerUtils.CONNECTIVTY_CHANGE);
        getActivity().registerReceiver(mCheckReceiver, intentfilter);
    }

    /**
     * qinguoli
     * 取消对网络变化的监听*
     */
    protected void unregisterCheckNetwork() {
        if (mCheckReceiver != null) {
            getActivity().unregisterReceiver(mCheckReceiver);
        }
    }

    /**
     * 修改parent activity function
     */
    public void changeDeleteIconText(String text) {
        if (getActivity() instanceof DownloadActivity) {
            ((DownloadActivity) getActivity()).mEditBtn.setText(text);
        } else {
            ((ChaoJiShiPinMainActivity) getActivity()).setEditMenuText(text);
        }
    }

    public void updateEditView() {
        ArrayList<DownloadJob> jobsList = DataUtils.getInstance().getCompletedDownloads();
        if (null != jobsList && jobsList.size() > 0) {
            SwipeMenuLayout mSwipe = null;
            mSwipe = mDownloadListFragment.mListView.getSwipeMenuLayout();
            if (mSwipe != null) {
                if (mSwipe.isOpen()) {
                    mSwipe.smoothCloseMenu();
                    changeDeleteIconText(getResources().getString(R.string.edit));
                } else {
                    mDownloadListFragment.updateDeleteView(mDownloadListFragment.mBottomlayout);
                    mDownloadListFragment.mUserDeletecount.setText(R.string.check_all);
                    mDownloadListFragment.mConfirm_delete.setText(R.string.delete_up);
                    mDownloadListFragment.mConfirm_delete.setTextColor(getResources().getColor(R.color.all_select));
                }
            } else {
                mDownloadListFragment.updateDeleteView(mDownloadListFragment.mBottomlayout);
                mDownloadListFragment.mUserDeletecount.setText(R.string.check_all);
                mDownloadListFragment.mConfirm_delete.setText(R.string.delete_up);
                mDownloadListFragment.mConfirm_delete.setTextColor(getResources().getColor(R.color.all_select));
            }
        }
    }
}
