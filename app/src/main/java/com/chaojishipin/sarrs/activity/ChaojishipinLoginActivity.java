package com.chaojishipin.sarrs.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.HistoryRecordResponseData;
import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.UploadRecord;
import com.chaojishipin.sarrs.bean.VerifyCode;
import com.chaojishipin.sarrs.dao.HistoryRecordDao;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.listener.UpoloadHistoryRecordListener;
import com.chaojishipin.sarrs.manager.HistoryRecordManager;
import com.chaojishipin.sarrs.thirdparty.ACache;
import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.thirdparty.LoginListener;
import com.chaojishipin.sarrs.thirdparty.LoginManager;
import com.chaojishipin.sarrs.thirdparty.LoginUtils;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.uploadstat.UmengPagePath;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.GetSmsContent;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.SaveUserinfo;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;


import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChaojishipinLoginActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener, LoginListener {


    private Button mSend, mLogin;
    private TextView mSms, mError, mSecret, mClear;
    private EditText mEdit;
    private Button mBack;
    String yzm = null;
    private int time = 60;
    private Timer timer = new Timer();
    TimerTask task;
    GetSmsContent content;
    private String phoneNumber = "18101091083";
    private String verCode = "";
    private RelativeLayout allparentview;
    private RelativeLayout buttomview;
    void init() {
        // view 获取
        mClear = (TextView) findViewById(R.id.register_activity_notify_clear_icon);
        mSms = (TextView) findViewById(R.id.register_activity_notify_sms);
        mError = (TextView) findViewById(R.id.register_activity_notify_qrcode);
        mSend = (Button) findViewById(R.id.register_activity_notify_timer);
        mLogin = (Button) findViewById(R.id.register_activity_notify_login);
        mBack = (Button) findViewById(R.id.register_activity_notify_back);
        mEdit = (EditText) findViewById(R.id.register_activity_notify_eidt);
        allparentview = (RelativeLayout) findViewById(R.id.allparentview);
        buttomview = (RelativeLayout) findViewById(R.id.buttomview);
        mEdit.requestFocus();
        mSecret = (TextView) findViewById(R.id.register_activity_notify_notice);
        mEdit.addTextChangedListener(autoAddSpaceTextWatcher);
        mLogin.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mSecret.setOnClickListener(this);
        mClear.setOnClickListener(this);
        allparentview.setOnClickListener(this);
        buttomview.setOnClickListener(this);

        phoneNumber = getIntent().getStringExtra("phone");
        if (!TextUtils.isEmpty(phoneNumber)) {
            mSms.setText(getResources().getString(R.string.login_verifycode_title) + Utils.buildFormatPhone(phoneNumber));
            Utils.addForeGroundColor(mSms, getResources().getColor(R.color.color_00A0E9), 3, 8);
        }
        content = new GetSmsContent(ChaojishipinLoginActivity.this, new Handler(), mEdit);
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
        // 请求验证码
        Utils.addForeGroundColor(mSecret, Color.RED, 6, mSecret.getText().toString().length());
        RequestVerifyCode();

    }

    void RequestVerifyCode() {

        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_GETVERIFYCODE_TAG);
        RequestVerifyCodeListener requstListner = new RequestVerifyCodeListener();
        HttpApi.getVerifyCodeRequest(phoneNumber, "86").start(requstListner, ConstantUtils.REQUEST_GETVERIFYCODE_TAG);

    }

    void LoginRequest(String code) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_LOGIN_TAG);
        RequestLoginListener requstListner = new RequestLoginListener();
        requstListner.setLoginListener(this);
        //性别默认保密2
        HttpApi.LoginRequest("0", phoneNumber, "86", code, null, null, 2, null, null).start(requstListner, ConstantUtils.REQUEST_GETVERIFYCODE_TAG);
    }


    class RequestVerifyCodeListener implements RequestListener<VerifyCode> {

        @Override
        public void onResponse(VerifyCode result, boolean isCachedData) {

            LogUtil.e("Login", "verifyCode " + result.getCode());
            verCode = result.getCode();
          /*  try{
                Thread.sleep(1000);
            }catch (Exception e){

            }*/

            if (!TextUtils.isEmpty(phoneNumber)) {
                mSms.setText(getResources().getString(R.string.login_verifycode_title) + Utils.buildFormatPhone(phoneNumber));
                Utils.addForeGroundColor(mSms, getResources().getColor(R.color.color_00A0E9), 3, 8);
            }
            task = new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (time > 0) {
                                mSend.setText("" + time + getString(R.string.login_verifycode_notice));
                                mSend.setTextSize(11);
                                mSend.setTextColor(getResources().getColor(R.color.color_999999));
                                mSend.setEnabled(false);
                            } else {
                                mSend.setEnabled(true);
                                mSend.setTextSize(15);
                                mSend.setTextColor(getResources().getColor(R.color.color_00000000));
                                mSend.setText(getResources().getString(R.string.login_verifycode_send_retry));
                                task.cancel();
                                //mError.setVisibility(View.GONE);
                                //mSms.setVisibility(View.VISIBLE);
                                mSend.setTextColor(getResources().getColor(R.color.color_444444));
                            }
                            time--;

                        }
                    });

                }
            };
            time = 60;
            timer.schedule(task, 0, 1000);


        }

        @Override
        public void dataErr(int errorCode) {

        }

        @Override
        public void netErr(int errorCode) {

        }
    }

    class RequestLoginListener implements RequestListener<BaseUserInfo> {
        private LoginListener lis;

        public void setLoginListener(LoginListener lis) {
            this.lis = lis;
        }

        @Override
        public void onResponse(BaseUserInfo result, boolean isCachedData) {
            if (result == null) {
                return;
            }
            if (result.getErrorCode() == null) {
                mSms.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                mError.setText(getString(R.string.login_verifycode_error_max));
            }
            if (result.getErrorCode().equalsIgnoreCase("0")) {
                ACache.get(ChaojishipinLoginActivity.this).remove(Constant.CACHE.LOGGED_USER_INFO + Constant.CACHE.VERSION);
                BaseUserInfo userInfo = new BaseUserInfo();
                userInfo.setName(result.getName());
                userInfo.setPhone(phoneNumber);
                userInfo.setAvatar(result.getAvatar());
                userInfo.setUid(result.getUid());
                userInfo.setGender(result.getGender());
                userInfo.setIsFirst(result.getIsFirst());// 设置用户是否第一次登录
                userInfo.setToken(result.getToken());
                userInfo.setType(LoginManager.TYPE_PHONE);
                UserLoginState.getInstance().setLogin(true);
                Log.d("ThirdLogin", " phone uid is " + result.getUid() + " and token is " + result.getToken());
                UserLoginState.getInstance().setUserInfo(userInfo);
                onLoginComplete(userInfo);
                LoginUtils.setLastLoginType(LoginManager.TYPE_PHONE);
                //缓存本地
                SaveUserinfo.saveuserinfo2Sharepre(ChaojishipinLoginActivity.this,userInfo,true);
//                SharedPreferences mySharedPreferences= getSharedPreferences("logininfo",
//                        ChaojishipinLoginActivity.MODE_PRIVATE);
//                //实例化SharedPreferences.Editor对象（第二步）
//                SharedPreferences.Editor editor = mySharedPreferences.edit();
//                //用putString的方法保存数据
//                editor.putBoolean("islogin", true);
//                editor.putString("ujson", JsonUtil.toJSONString(userInfo));
//                editor.putString("login_type", LoginManager.TYPE_PHONE);
//                //提交当前数据
//                editor.commit();

            } else {
                mSms.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                mError.setText(getString(R.string.login_verifycode_error_max));
            }
        }

        @Override
        public void dataErr(int errorCode) {
            if (lis != null) {
                lis.onLoginFailed();
            }
        }

        @Override
        public void netErr(int errorCode) {
            if (lis != null) {
                lis.onLoginFailed();
            }
        }
    }

    TextWatcher autoAddSpaceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {


        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (charSequence.length() > 0) {
                mClear.setVisibility(View.VISIBLE);
            } else {
                mClear.setVisibility(View.GONE);
            }

            if (charSequence.length() > 4) {
                mSms.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                mError.setText(getResources().getString(R.string.login_verifycode_error_max));
                mLogin.setEnabled(false);
//                mLogin.setBackgroundColor(getResources().getColor(R.color.color_D5D5D5));
                mLogin.setBackgroundResource(R.drawable.next_step);
                return;
            }
            if (charSequence.length() < 4) {
                mLogin.setEnabled(false);
//                mLogin.setBackgroundColor(getResources().getColor(R.color.color_D5D5D5));
                mLogin.setBackgroundResource(R.drawable.next_step);
            }

            if (charSequence.length() == 4) {
                mLogin.setBackgroundResource(R.drawable.next_step_red);
//                mLogin.setBackgroundColor(getResources().getColor(R.color.color_c5242b));
                mLogin.setEnabled(true);
                return;
            }

        }

        public void afterTextChanged(Editable editable) {


        }
    };

    @Override
    public void onClick(View v) {
        View view = getWindow().peekDecorView();
        switch (v.getId()) {
            case R.id.register_activity_notify_clear_icon:
                mEdit.setText("");
                break;
            case R.id.register_activity_notify_login:
                Utils.hideInput(this);
                verCode = mEdit.getText().toString();
                if (verCode != null && verCode.length() == 4) {
                    LoginRequest(verCode);
                }


                break;
            case R.id.register_activity_notify_timer:
                mSms.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
                RequestVerifyCode();

                break;

            case R.id.register_activity_notify_back:
                this.finish();
                break;
            case R.id.register_activity_notify_notice:
                startActivity(new Intent(this, ChaojishipinHtmlActivity.class));
                break;
            case R.id.allparentview:
            case R.id.buttomview:
                Utils.hideInput(this);
                break;
        }
    }


    @Override
    protected void handleInfo(Message msg) {

    }


    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.registeractivity_notify, null);
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
        this.getContentResolver().unregisterContentObserver(content);
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    public void onLoginCancel() {

    }

    @Override
    public void onLoginComplete(BaseUserInfo user) {
        //向服务器同步
        ToastUtil.showShortToast(this,getResources().getString(R.string.login_success));
        if (UserLoginState.getInstance().isLogin()) {
            requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
        }
        if (user.getIsFirst())
            startActivity(new Intent(this, DefaultHeadNameActivity.class)); // 手机登录
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void onLoginSuccess() {
//        if (UserLoginState.getInstance().isLogin()) {
//            requestHistoryRecordData(UserLoginState.getInstance().getUserInfo().getToken());
//        }
    }

    @Override
    public void onLoginFailed() {

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
            HistoryRecordManager.setHisToryRecordFromServer(result);
            ArrayList<HistoryRecord> uploadlist = new ArrayList<HistoryRecord>();
            ArrayList<HistoryRecord> localrecordlist = new ArrayList<HistoryRecord>();
            HistoryRecordDao historyRecordDao = new HistoryRecordDao(ChaojishipinLoginActivity.this);
            localrecordlist = historyRecordDao.getAll();
            ArrayList<HistoryRecord> netlist = result;
            if (netlist != null && netlist.size() > 0) {
                for (int i = 0; i < localrecordlist.size(); i++) {
                    for (int j = 0; j < netlist.size(); j++) {
                        if (localrecordlist.get(i).getId() !=null && localrecordlist.get(i).getId().equals(netlist.get(j).getId()) &&  "".equals(localrecordlist.get(i).getId())) {
                            if (localrecordlist.get(i).getTimestamp().compareTo(netlist.get(j).getTimestamp()) > 0) {
                                uploadlist.add(localrecordlist.get(i));
                            }
                            break;
                        }
                        if((localrecordlist.get(i).getId() ==null || "".equals(localrecordlist.get(i).getId())) && localrecordlist.get(i).getGvid().equals(netlist.get(j).getGvid())){
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
            } else if (localrecordlist.size() > 0) {
                uploadlist = localrecordlist;
            }
            //需要上传
            List<UploadRecord> uploadRecordList = new ArrayList<UploadRecord>();
            if (uploadlist.size() > 0) {
                for (HistoryRecord historyRecord : uploadlist) {
                    UploadRecord aupload = new UploadRecord();
                    aupload.setCid(Integer.parseInt(historyRecord.getCategory_id().trim()));
                    aupload.setVid(historyRecord.getGvid());
                    aupload.setSource(historyRecord.getSource());
                    aupload.setPlayTime(Integer.parseInt(historyRecord.getPlay_time()));
                    aupload.setAction(0);
                    aupload.setDurationTime(historyRecord.getDurationTime());
                    aupload.setPid(historyRecord.getId());
                    String timesmap = historyRecord.getTimestamp().trim();
                    aupload.setUpdateTime(Long.parseLong(timesmap));
                    uploadRecordList.add(aupload);
                }

                String json = JsonUtil.toJSONString(uploadRecordList);
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
                uploadHistoryRecord(token, json, new UpoloadHistoryRecordListener());
    }

    @Override
    protected void onResume() {
        UmengPagePath.beginpage(ConstantUtils.AND_REGISTER,this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        UmengPagePath.endpage(ConstantUtils.AND_REGISTER,this);
        super.onPause();
    }
}