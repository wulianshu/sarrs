package com.chaojishipin.sarrs.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.HistoryRecordResponseData;
import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.bean.LogOutInfo;
import com.chaojishipin.sarrs.bean.ModifyInfo;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.UpoloadHistoryRecordListener;
import com.chaojishipin.sarrs.manager.HistoryRecordManager;
import com.chaojishipin.sarrs.thirdparty.ACache;
import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.LoginHelper;
import com.chaojishipin.sarrs.thirdparty.LoginListener;
import com.chaojishipin.sarrs.thirdparty.LoginManager;
import com.chaojishipin.sarrs.thirdparty.UIs;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.SaveUserinfo;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChaojishipinModifyUserInfoActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener, LoginListener {
    // 头像
    private ImageView mIcon;
    // 右向箭头
    private Button mNcMore, mXbMore, mTxmore, mExit, mBack;
    // 昵称、性别
    private TextView mNcContent, mXbContent, mPhone, mTitle;
    //
    private Bitmap cutBitmap;
    private String token;
    private RelativeLayout mobileLayout;
    private LinearLayout mdivder;

    void init() {
        mBack = (Button) findViewById(R.id.register_activity_notify_back);
        mTitle = (TextView) findViewById(R.id.register_activity_title);
        mTxmore = (Button) findViewById(R.id.modify_activity_tx_right_more);
        mIcon = (ImageView) findViewById(R.id.modify_activity_tx_icon);
        mExit = (Button) findViewById(R.id.modify_activity_exit);
        mNcContent = (TextView) findViewById(R.id.modify_activity_nc_content);
        mNcMore = (Button) findViewById(R.id.modify_activity_nc_right_more);
        mXbContent = (TextView) findViewById(R.id.modify_activity_xb_content);
        mXbMore = (Button) findViewById(R.id.modify_activity_xb_right_more);
        mPhone = (TextView) findViewById(R.id.modify_activity_phone_content);
        mobileLayout = (RelativeLayout) findViewById(R.id.modify_activity_layout_phone);
        mdivder = (LinearLayout) findViewById(R.id.modify_activity_dividerbar4);
        mXbMore.setOnClickListener(this);
        mTxmore.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mNcMore.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mIcon.setOnClickListener(this);
        DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.color.color_e7e7e7)
                .showImageForEmptyUri(R.color.color_e7e7e7)
                .showImageOnLoading(R.color.color_e7e7e7)
                .build();
        ImageLoader.getInstance().displayImage(UserLoginState.getInstance().getUserInfo().getAvatar(), mIcon, options1);
        mNcContent.setText(UserLoginState.getInstance().getUserInfo().getName());
        switch (UserLoginState.getInstance().getUserInfo().getGender()) {
            case 0:
                mXbContent.setText(getString(R.string.gender_0));
                break;
            case 1:
                mXbContent.setText(getString(R.string.gender_1));
                break;
            case 2:
                mXbContent.setText(getString(R.string.gender_2));
                break;
        }
        mTitle.setText(getString(R.string.user_info));
        if (UserLoginState.getInstance().getUserInfo().getType().equalsIgnoreCase(LoginManager.TYPE_PHONE)) {
            mPhone.setText(UserLoginState.getInstance().getUserInfo().getPhone());
        } else {
            mobileLayout.setVisibility(View.GONE);
            mdivder.setVisibility(View.GONE);
        }
    }

    void LogOutRequest(String uuid, String token) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_LOGIN_TAG);
        RequestLogOutListener logoutLis = new RequestLogOutListener();
        logoutLis.setLoginListener(this);
        HttpApi.LogoutRequest(uuid, token).start(logoutLis, ConstantUtils.REQUEST_LOGOUT_TAG);

    }

    void ModifyInfoRequest(String token, String nickName, int gender, String img) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_LOGIN_TAG);
        RequestModifyINfoListener requstListner = new RequestModifyINfoListener();
        HttpApi.modifyUserRequest(token, nickName, gender, img).start(requstListner, ConstantUtils.REQUEST_MODIFYUSERINFO_TAG);
    }

    class RequestLogOutListener implements RequestListener<LogOutInfo> {
        private LoginListener lis;

        public void setLoginListener(LoginListener lis) {
            this.lis = lis;
        }

        @Override
        public void onResponse(LogOutInfo result, boolean isCachedData) {
            UIs.showToast(R.string.logout_success);
            ChaojishipinModifyUserInfoActivity.this.finish();
            UserLoginState.getInstance().setLogin(false);
            //向服务器播放记录同步
            if (token != null && !"".equals(token))
//                requestHistoryRecordData(token);
            upLoadLocalRecord2Server();
            //删除本地的播放记录
            HistoryRecordDao historyRecordDao = new HistoryRecordDao(ChaojishipinModifyUserInfoActivity.this);
            historyRecordDao.delAll();
            HistoryRecordManager.clear();
        }

        @Override
        public void dataErr(int errorCode) {

        }

        @Override
        public void netErr(int errorCode) {
            ToastUtil.showShortToast(ChaojishipinModifyUserInfoActivity.this, getResources().getString(R.string.neterror));
        }
    }

    class RequestModifyINfoListener implements RequestListener<ModifyInfo> {

        @Override
        public void onResponse(ModifyInfo result, boolean isCachedData) {
            if (result.getState() == 1) {

            }


        }

        @Override
        public void dataErr(int errorCode) {

        }

        @Override
        public void netErr(int errorCode) {

        }
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ChaojishipinModifyUserInfoActivity.this, ChaojishipinModifyUserInfoDetailActivity.class);
        switch (v.getId()) {

            case R.id.register_activity_notify_back:
                this.finish();
                break;


            case R.id.modify_activity_exit:
                token = UserLoginState.getInstance().getUserInfo().getToken();
                SaveUserinfo.logout(this);
                LogOutRequest("", "" + token);

                break;
            case R.id.modify_activity_nc_right_more:
                intent.putExtra("mode", 0);
                startActivity(intent);

                break;
            case R.id.modify_activity_tx_right_more:
                intent.putExtra("mode", 2);
                startActivity(intent);

                break;
            case R.id.modify_activity_xb_right_more:
                intent.putExtra("mode", 1);
                startActivity(intent);

                break;
            case R.id.modify_activity_tx_icon:
                showHeader();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("UserInfoActivity", "onResume " + UserLoginState.getInstance().getUserInfo().getAvatar());
        // 头像
        if (UserLoginState.getInstance().getUserInfo().getAvatar() != null) {
            cutBitmap = ImageLoader.getInstance().loadImageSync(UserLoginState.getInstance().getUserInfo().getAvatar());
            mIcon.setImageBitmap(cutBitmap);

        }
        //昵称
        mNcContent.setText("" + UserLoginState.getInstance().getUserInfo().getName());

        //性别
        switch (UserLoginState.getInstance().getUserInfo().getGender()) {
            case 0:
                mXbContent.setText(getString(R.string.gender_0));
                break;
            case 1:
                mXbContent.setText(getString(R.string.gender_1));
                break;
            case 2:
                mXbContent.setText(getString(R.string.gender_2));
                break;
        }

    }


    @Override
    protected void handleInfo(Message msg) {

    }


    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.modifyactivity, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(false);
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cutBitmap != null && !cutBitmap.isRecycled())
            cutBitmap.recycle();
        System.gc();
    }


    @Override
    public void onLoginCancel() {

    }

    @Override
    public void onLoginComplete(BaseUserInfo user) {
        this.finish();
        startActivity(new Intent(ChaojishipinModifyUserInfoActivity.this, ChaoJiShiPinMainActivity.class));
    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginFailed() {

    }

    private void showHeader() {
        String path = UserLoginState.getInstance().getUserInfo().getAvatar();
        if (path == null || (path != null && path.length() == 0)) {
            ToastUtil.showLongToast(getApplicationContext(), R.string.no_header);
        } else {
            Intent intent = new Intent(this, ShowHeaderActivity.class);
            intent.putExtra("image", path);
            startActivity(intent);
        }
    }

    public void upLoadLocalRecord2Server(){
        ArrayList<HistoryRecord> netlist =  HistoryRecordManager.getHisToryRecordFromServer();
        ArrayList<HistoryRecord> uploadlist = new ArrayList<HistoryRecord>();
        ArrayList<HistoryRecord> localrecordlist = new ArrayList<HistoryRecord>();
        HistoryRecordDao historyRecordDao = new HistoryRecordDao(ChaojishipinModifyUserInfoActivity.this);
        localrecordlist = historyRecordDao.getAll();
        if (netlist != null && netlist.size() > 0) {
            for (int i = 0; i < localrecordlist.size(); i++) {
                for (int j = 0; j < netlist.size(); j++) {
                    if (localrecordlist.get(i).getId().equals(netlist.get(j).getId())) {
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
                aupload.setCid(Integer.parseInt(historyRecord.getCategory_id()));
                aupload.setVid(historyRecord.getGvid());
                aupload.setSource(historyRecord.getSource());
                aupload.setPlayTime(Integer.parseInt(historyRecord.getPlay_time()));
                aupload.setAction(0);
                aupload.setDurationTime(historyRecord.getDurationTime());
                aupload.setPid(historyRecord.getId());
                aupload.setUpdateTime(Long.parseLong(historyRecord.getTimestamp()));
                uploadRecordList.add(aupload);
            }
            String json = JsonUtil.toJSONString(uploadRecordList);
            uploadHistoryRecord(UserLoginState.getInstance().getUserInfo().getToken(), json);
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

    private class RequestHistoryRecordListener implements RequestListener<SarrsArrayList> {

        @Override
        public void onResponse(SarrsArrayList result, boolean isCachedData) {
            //进行展现的相关操作
            ArrayList<HistoryRecord> uploadlist = new ArrayList<HistoryRecord>();
            ArrayList<HistoryRecord> localrecordlist = new ArrayList<HistoryRecord>();
            HistoryRecordDao historyRecordDao = new HistoryRecordDao(ChaojishipinModifyUserInfoActivity.this);
            localrecordlist = historyRecordDao.getAll();
            ArrayList<HistoryRecord> netlist = result;

            if (netlist != null && netlist.size() > 0) {
                for (int i = 0; i < localrecordlist.size(); i++) {
                    for (int j = 0; j < netlist.size(); j++) {
                        if (localrecordlist.get(i).getId().equals(netlist.get(j).getId())) {
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
                    aupload.setCid(Integer.parseInt(historyRecord.getCategory_id()));
                    aupload.setVid(historyRecord.getGvid());
                    aupload.setSource(historyRecord.getSource());
                    aupload.setPlayTime(Integer.parseInt(historyRecord.getPlay_time()));
                    aupload.setAction(0);
                    aupload.setDurationTime(historyRecord.getDurationTime());
                    aupload.setPid(historyRecord.getId());
                    aupload.setUpdateTime(Integer.parseInt(historyRecord.getTimestamp()));
                    uploadRecordList.add(aupload);
                }
                String json = JsonUtil.toJSONString(uploadRecordList);
//                JSONObject jsonObject = (JSONObject) JsonUtil.toJSON(uploadRecordList);
                uploadHistoryRecord(UserLoginState.getInstance().getUserInfo().getToken(), json);
            }
        }

        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {

        }
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
                uploadHistoryRecord(token, json, new com.chaojishipin.sarrs.listener.UpoloadHistoryRecordListener());
//                .start(new UpoloadHistoryRecordListener(), ConstantUtils.UPLOAD_HISTORY_RECORD);
    }

    private class UpoloadHistoryRecordListener implements RequestListener<HistoryRecordResponseData> {
        @Override
        public void onResponse(HistoryRecordResponseData result, boolean isCachedData) {

        }

        @Override
        public void netErr(int errorCode) {

        }

        @Override
        public void dataErr(int errorCode) {

        }
    }
}