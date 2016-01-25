package com.chaojishipin.sarrs.download.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinBaseActivity;
import com.chaojishipin.sarrs.download.fragment.DownloadFragment;
import com.chaojishipin.sarrs.widget.TitleActionBar;


public class DownloadActivity extends ChaoJiShiPinBaseActivity implements
        TitleActionBar.onActionBarClickListener {

    private final static String TAG = "DownloadActivity";

    private TitleActionBar mTitleActionBar;
    public TextView mEditBtn;
    private DownloadFragment downloadFragment;

    @Override
    protected void handleInfo(Message msg) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(false);
        ViewGroup root=(ViewGroup) this.getWindow().getDecorView();  //获取本Activity下的获取最外层控件
//        initView();
    }

    private void initView() {

        mTitleActionBar = (TitleActionBar) findViewById(R.id.download_title);
        mEditBtn = (TextView) findViewById(R.id.right_edit_btn);
        mTitleActionBar.setTitle(getResources().getString(R.string.download_title));
        downloadFragment = new DownloadFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.download_content, downloadFragment);
        transaction.commit();
        updateDeleteIcon();

    }

    @Override
    public void onTitleLeftClick(View v) {
        onClickBackButton();
    }

    @Override
    public void onTitleRightClick(View v) {
        if (v.getId() == R.id.right_edit_btn)
            downloadFragment.updateEditView();
    }

    @Override
    public void onTitleDoubleTap() {

    }

    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.activity_download, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        mEditBtn.setText(getResources().getString(R.string.edit));
        mTitleActionBar.setOnActionBarClickListener(this);
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onClickBackButton();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onClickBackButton() {
        finish();
    }


//    public boolean isShowFilterButton() {
//        return isShowFilterButton;
//    }
//
//    public void setIsShowFilterButton(boolean isShowFilterButton) {
//        this.isShowFilterButton = isShowFilterButton;
//    }
//
//    /**
//     * @param isShowFilterButton 是否显示
//     */
//    public void setFilterButtonState(boolean isShowFilterButton) {
//        this.isShowFilterButton = isShowFilterButton;
//        supportInvalidateOptionsMenu();
//    }

    public void updateDeleteIcon() {
        if(mTitleActionBar!=null){
            mTitleActionBar.setmRightButtonVisibility(false);
            if (ChaoJiShiPinApplication.getInstatnce().getDownloadManager().getCompletedDownloads().size() > 0) {
                mTitleActionBar.setRightEditButtonVisibility(true);
            } else {
                mTitleActionBar.setRightEditButtonVisibility(false);
            }
        }
    }
}
