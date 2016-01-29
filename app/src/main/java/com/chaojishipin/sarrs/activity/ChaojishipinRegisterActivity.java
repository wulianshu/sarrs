package com.chaojishipin.sarrs.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.thirdparty.ACache;
import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.thirdparty.LoginHelper;
import com.chaojishipin.sarrs.thirdparty.LoginListener;
import com.chaojishipin.sarrs.thirdparty.LoginManager;
import com.chaojishipin.sarrs.thirdparty.LoginUtils;
import com.chaojishipin.sarrs.thirdparty.QQLoginHelper;
import com.chaojishipin.sarrs.thirdparty.UIs;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.thirdparty.WeiboLoginHelper;
import com.chaojishipin.sarrs.uploadstat.UmengPagePath;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.MD5Utils;
import com.chaojishipin.sarrs.utils.SaveUserinfo;
import com.chaojishipin.sarrs.utils.Utils;


public class ChaojishipinRegisterActivity extends ChaoJiShiPinBaseActivity implements LoginListener, View.OnClickListener {


    private Button mNext, mWx, mQQ, mWb, mBack;
    private TextView mClear, mSecret;
    private EditText mEdit;
    LoginHelper mloginHelper;
    protected static final int TERMINATE_INPROCESS = 0;

    void init() {


        // view 获取
        mClear = (TextView) findViewById(R.id.register_activity_clear_icon);
        mSecret = (TextView) findViewById(R.id.register_activity_notice);
        mWx = (Button) findViewById(R.id.register_activity_wx);
        mWb = (Button) findViewById(R.id.register_activity_wb);
        mQQ = (Button) findViewById(R.id.register_activity_qq);
        mNext = (Button) findViewById(R.id.register_activity_next);
        mNext.setEnabled(false);
        mBack = (Button) findViewById(R.id.register_activity_notify_back);
        mEdit = (EditText) findViewById(R.id.register_activity_eidt);
        mEdit.addTextChangedListener(autoAddSpaceTextWatcher);
        mEdit.requestFocus();
        mWx.setOnClickListener(this);
        mWb.setOnClickListener(this);
        mQQ.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mClear.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mSecret.setOnClickListener(this);
        // 为隐私条款文字添加前景色
        Utils.addForeGroundColor(mSecret, Color.RED, 6, mSecret.getText().toString().length());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_activity_wx:
                mloginHelper = LoginManager.from(ChaojishipinRegisterActivity.this).getHelper(LoginManager.TYPE_WEIXIN);
                mloginHelper.excuteLogin(this);
                break;
            case R.id.register_activity_wb:
                mloginHelper = LoginManager.from(ChaojishipinRegisterActivity.this).getHelper(LoginManager.TYPE_SINA_WEIBO);
                mloginHelper.excuteLogin(this);
                break;
            case R.id.register_activity_qq:
                mloginHelper = LoginManager.from(ChaojishipinRegisterActivity.this).getHelper(LoginManager.TYPE_QQ);
                mloginHelper.excuteLogin(this);
                break;
            case R.id.register_activity_next:
                Intent intent = new Intent(this, ChaojishipinLoginActivity.class);
                String phone = mEdit.getText().toString().replace(" ", "");
                LogUtil.e("Login", "phone" + phone);
                intent.putExtra("phone", "" + phone);
                finish();
                startActivity(intent);
                break;
            case R.id.register_activity_clear_icon:
                mEdit.setText("");
                break;
            case R.id.register_activity_notify_back:
                this.finish();
                break;
            case R.id.register_activity_notice:
                startActivity(new Intent(this, ChaojishipinHtmlActivity.class));
                break;
        }
    }


    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void onLoginComplete(BaseUserInfo user) {
        if (user.getType().equalsIgnoreCase(LoginManager.TYPE_QQ)) {
            LoginRequest(LoginManager.TYPE_QQ, user.getOpenId(), user.getName(), user.getGender(), user.getAvatar(), MD5Utils.md5(ConstantUtils.ThirdpartySecret.START + user.getOpenId() + ConstantUtils.ThirdpartySecret.END));
        } else if (user.getType().equalsIgnoreCase(LoginManager.TYPE_SINA_WEIBO)) {
            LoginRequest(LoginManager.TYPE_SINA_WEIBO, user.getOpenId(), user.getName(), user.getGender(), user.getAvatar(), MD5Utils.md5(ConstantUtils.ThirdpartySecret.START + user.getOpenId() + ConstantUtils.ThirdpartySecret.END));
        } else if (user.getType().equalsIgnoreCase(LoginManager.TYPE_WEIXIN)) {
            LoginRequest(LoginManager.TYPE_WEIXIN, user.getOpenId(), user.getName(), user.getGender(), user.getAvatar(), MD5Utils.md5(ConstantUtils.ThirdpartySecret.START + user.getOpenId() + ConstantUtils.ThirdpartySecret.END));
        }
    }

    @Override
    public void onLoginCancel() {
        UIs.showToast(R.string.login_canceled);
    }

    @Override
    public void onLoginSuccess() {
        UIs.showToast(R.string.login_success);
    }

    @Override
    public void onLoginFailed() {
        UIs.showToast(R.string.login_failed);
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

    @Override
    protected void onDestroy() {


        if (null != mloginHelper) {
            mloginHelper.removeCallbacks();
        }
        super.onDestroy();

    }

    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.registeractivity, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(false);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    /**
     * 登陆接口 添加sex字段
     * signature 需要有私钥和MD5生成
     */

    void LoginRequest(String type, String openId, String userName, int sex, String img_url, String sig) {
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_LOGIN_TAG);
        RequestLoginListener requstListner = new RequestLoginListener();
        requstListner.setType(type);
        HttpApi.LoginRequest(type, null, "86", null, openId, userName, sex, img_url, sig).start(requstListner, ConstantUtils.REQUEST_LOGIN_TAG);
    }

    class RequestLoginListener implements RequestListener<BaseUserInfo> {
        private String mtype;

        public void setType(String type) {
            this.mtype = type;
        }

        @Override
        public void onResponse(BaseUserInfo result, boolean isCachedData) {
            if (result == null) {
                return;
            }
            if (result.getErrorCode().equalsIgnoreCase("0")) {
                UIs.showToast(R.string.login_success);
                UserLoginState.getInstance().setLogin(true);
                UserLoginState.getInstance().getUserInfo().setUid(result.getUid());
                UserLoginState.getInstance().getUserInfo().setToken(result.getToken());
                UserLoginState.getInstance().getUserInfo().setType(mtype);
                UserLoginState.getInstance().getUserInfo().setAvatar(result.getAvatar());
                UserLoginState.getInstance().getUserInfo().setName(result.getName());
                UserLoginState.getInstance().getUserInfo().setGender(result.getGender());
                SaveUserinfo.saveuserinfo2Sharepre(ChaojishipinRegisterActivity.this, UserLoginState.getInstance().getUserInfo(), true);
            } else
                UIs.showToast(R.string.login_failed);
            finish();
            // 测试浮层页
//            if (!result.getIsFirst())
//                startActivity(new Intent(ChaojishipinRegisterActivity.this, DefaultHeadNameActivity.class));
        }

        @Override
        public void dataErr(int errorCode) {
            UIs.showToast(R.string.login_failed);
            UserLoginState.getInstance().setUserInfo(null);
            UserLoginState.getInstance().setLogin(false);
        }

        @Override
        public void netErr(int errorCode) {
            UIs.showToast(R.string.login_failed);
            UserLoginState.getInstance().setUserInfo(null);
            UserLoginState.getInstance().setLogin(false);

        }

    }

    TextWatcher autoAddSpaceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            mClear.setVisibility(View.GONE);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            mClear.setVisibility(View.VISIBLE);
            if (charSequence == null || charSequence.length() == 0) {
                mClear.setVisibility(View.GONE);
                return;
            }
            if (charSequence.length() > 13) {
//                mNext.setBackgroundColor(getResources().getColor(R.color.color_D5D5D5));
                mNext.setBackgroundResource(R.drawable.next_step);
                return;
            }
            if (charSequence.length() == 13) {
                mNext.setEnabled(true);
                mNext.setBackgroundResource(R.drawable.next_step_red);
//                mNext.setBackgroundColor(getResources().getColor(R.color.color_c5242b));
            }
            if (charSequence.length() < 13) {
                mNext.setBackgroundResource(R.drawable.next_step);
//                mNext.setBackgroundColor(getResources().getColor(R.color.color_D5D5D5));
            }
            mClear.setVisibility(View.VISIBLE);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < charSequence.length(); i++) {
                if (i != 3 && i != 8 && charSequence.charAt(i) == ' ') {
                    continue;
                } else {
                    stringBuilder.append(charSequence.charAt(i));
                    if ((stringBuilder.length() == 4 || stringBuilder.length() == 9)
                            && stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
                        stringBuilder.insert(stringBuilder.length() - 1, ' ');
                    }
                }
            }
            if (!stringBuilder.toString().equals(charSequence.toString())) {
                int index = start + 1;
                if (stringBuilder.charAt(start) == ' ') {
                    if (before == 0) {
                        index++;
                    } else {
                        index--;
                    }
                } else {
                    if (before == 1) {
                        index--;
                    }
                }
                mEdit.setText(stringBuilder.toString());
                mEdit.setSelection(index);
            }
        }

        public void afterTextChanged(Editable editable) {
        }
    };

    /**
     * 新浪微博登陆获取用户信息回调处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mloginHelper != null && mloginHelper instanceof WeiboLoginHelper) {
            ((WeiboLoginHelper) mloginHelper).onActivityResult(requestCode, resultCode, data);
        }
        if (mloginHelper != null && mloginHelper instanceof QQLoginHelper) {
            ((QQLoginHelper) mloginHelper).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {
        
    }
}