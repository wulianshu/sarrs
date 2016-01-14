package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.adapter.ModifyUserGridViewAdapter;
import com.chaojishipin.sarrs.adapter.ModifyUserListViewAdapter;
import com.chaojishipin.sarrs.bean.GenderInfo;
import com.chaojishipin.sarrs.bean.ImageInfo;
import com.chaojishipin.sarrs.bean.LogOutInfo;
import com.chaojishipin.sarrs.bean.ModifyInfo;
import com.chaojishipin.sarrs.bean.UploadFile;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.photo.ImageFloder;
import com.chaojishipin.sarrs.photo.MyAdapter;
import com.chaojishipin.sarrs.thirdparty.BaseUserInfo;
import com.chaojishipin.sarrs.thirdparty.LoginListener;
import com.chaojishipin.sarrs.thirdparty.UIs;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LocalImageLoader;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.SaveUserinfo;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.NoScrollListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChaojishipinModifyUserInfoDetailActivity extends ChaoJiShiPinBaseActivity implements View.OnClickListener, LoginListener, AdapterView.OnItemClickListener {
    // 返回、确定按钮
    private Button mConfirm, mBack;
    // titlebar 标题
    private TextView mTitle;
    //跳入模式
    private int mode;
    //修改昵称
    private RelativeLayout mNcLayout;
    //输入框
    private EditText mEdit;
    //输出框删除按钮
    private TextView mEditClear;
    //修改性别
    private RelativeLayout mXbLayout;
    // 性别列表
    private NoScrollListView mListView;
    //修改头像
    private RelativeLayout mImageLayout;
    // 头像列表
    private GridView mGrid;
    String mEditStr;
    ModifyUserGridViewAdapter mGridAdapter;
    ModifyUserListViewAdapter mListViewAdapter;
    LocalImageLoader loader;
    PictureReceiver pictureReceiver;
    public static final String imagePickFinish = "pickFinish";
    private String mHeadUrl; // 用户头像

    void init() {
        mode = getIntent().getIntExtra("mode", 0);
        mBack = (Button) findViewById(R.id.register_activity_notify_back);
        mTitle = (TextView) findViewById(R.id.register_activity_title);
        mConfirm = (Button) findViewById(R.id.register_activity_titlebar_right_btn);
        mConfirm.setVisibility(View.VISIBLE);
        // 修改昵称
        if (mode == 0) {
            mNcLayout = (RelativeLayout) findViewById(R.id.mdetail_activity_layout_nc);
            mEditClear = (TextView) findViewById(R.id.mdetail_activity_clear_icon);
            mEdit = (EditText) findViewById(R.id.mdetail_activity_eidt);
            mNcLayout.setVisibility(View.VISIBLE);
            mTitle.setText(getString(R.string.user_nc));
            mEdit.setText(UserLoginState.getInstance().getUserInfo().getName());
            mEditClear.setOnClickListener(this);
            mEdit.setSelection(UserLoginState.getInstance().getUserInfo().getName().length());

            // 修改性别
        } else if (mode == 1) {
            mXbLayout = (RelativeLayout) findViewById(R.id.mdetail_activity_layout_xb);
            mListView = (NoScrollListView) findViewById(R.id.mdetail_activity_xb_lv);
            mListView.setOnItemClickListener(this);
            mXbLayout.setVisibility(View.VISIBLE);
            mTitle.setText(getString(R.string.user_xb));
            if (mListViewAdapter == null) {
                mListViewAdapter = new ModifyUserListViewAdapter(this);
            }
            initData();
            mListViewAdapter.setGenderList(listInfo);
            mListView.setAdapter(mListViewAdapter);
            // 修改头像
        } else if (mode == 2) {
            // 后台线程加载本地图
            if (loader == null) {
                loader = new LocalImageLoader(this, mHandler);
            }
            mHandler.post(loader);
            mImageLayout = (RelativeLayout) findViewById(R.id.mdetail_activity_layout_tx);
            mGrid = (GridView) findViewById(R.id.mdetail_activity_tx_grid);
            mGrid.setOnItemClickListener(this);
            mImageLayout.setVisibility(View.VISIBLE);
            mTitle.setText(getString(R.string.user_img));
            mConfirm.setVisibility(View.GONE);
        }
        mBack.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        // 注册头像修改广播
        registerImageReceiver();
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
        if (mode == 0)
        {
            HttpApi.modifyUserRequest(token, nickName, -1, null).start(requstListner, ConstantUtils.REQUEST_MODIFYUSERINFO_TAG);
        }else if (mode == 1)
        {
            HttpApi.modifyUserRequest(token, null, gender, null).start(requstListner, ConstantUtils.REQUEST_MODIFYUSERINFO_TAG);
        }else if (mode == 2)
        {
            HttpApi.modifyUserRequest(token, null, -1, img).start(requstListner, ConstantUtils.REQUEST_MODIFYUSERINFO_TAG);
        }
    }

    // 未使用
    class RequestLogOutListener implements RequestListener<LogOutInfo> {
        private LoginListener lis;

        public void setLoginListener(LoginListener lis) {
            this.lis = lis;
        }

        @Override
        public void onResponse(LogOutInfo result, boolean isCachedData) {
            UIs.showToast(R.string.logout_success);
            ChaojishipinModifyUserInfoDetailActivity.this.finish();
            UserLoginState.getInstance().setLogin(false);

        }

        @Override
        public void dataErr(int errorCode) {

        }

        @Override
        public void netErr(int errorCode) {
        }
    }

    class RequestModifyINfoListener implements RequestListener<ModifyInfo> {

        @Override
        public void onResponse(ModifyInfo result, boolean isCachedData) {
            if (result.getState() == 1) {
                // 昵称
                if (mode == 0) {
                    UserLoginState.getInstance().getUserInfo().setName(mEditStr);
                    ChaojishipinModifyUserInfoDetailActivity.this.finish();
                }
                // 性别
                else if (mode == 1) {
                    UserLoginState.getInstance().getUserInfo().setGender(clickId);
                    ChaojishipinModifyUserInfoDetailActivity.this.finish();
                }
                // 头像
                else if (mode == 2) {
                    UIs.showToast(getString(R.string.modify_tx_sucess));
                    UserLoginState.getInstance().getUserInfo().setAvatar(mHeadUrl);
                    ChaojishipinModifyUserInfoDetailActivity.this.finish();
                }
                SaveUserinfo.saveuserinfo2Sharepre(ChaojishipinModifyUserInfoDetailActivity.this,UserLoginState.getInstance().getUserInfo(),true);
            } else {
                UIs.showToast(getString(R.string.modify_nc_failed));
            }
        }

        @Override
        public void dataErr(int errorCode) {

        }

        @Override
        public void netErr(int errorCode) {
            ToastUtil.showShortToast(ChaojishipinModifyUserInfoDetailActivity.this, getResources().getString(R.string.neterror));
        }
    }

    class RequestUploadHeaderListener implements RequestListener<UploadFile> {

        @Override
        public void onResponse(UploadFile result, boolean isCachedData) {
            if(result!=null && result.getState() ==1) {
                ModifyInfoRequest(UserLoginState.getInstance().getUserInfo().getToken(), UserLoginState.getInstance().getUserInfo().getName(), -1, result.getFile());
            }
//            else{
//                ToastUtil.showShortToast(ChaojishipinModifyUserInfoDetailActivity.this,ChaojishipinModifyUserInfoDetailActivity.this.getResources().getString(R.string.modify_tx_fail));
//            }
//            if (result.getState() == 1) {
//
//            } else {
//                UIs.showToast(getString(R.string.modify_nc_failed));
//            }
        }

        @Override
        public void dataErr(int errorCode) {

        }

        @Override
        public void netErr(int errorCode) {
            ToastUtil.showShortToast(ChaojishipinModifyUserInfoDetailActivity.this, getResources().getString(R.string.neterror));
        }
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.register_activity_notify_back:
                this.finish();
                break;
            case R.id.register_activity_titlebar_right_btn:
                if (mode == 0) {
                    mEditStr = mEdit.getText().toString();
                    if (mEditStr.equalsIgnoreCase(UserLoginState.getInstance().getUserInfo().getName())) {
                        UIs.showToast(getString(R.string.rule_nick_null));
                    }
                    int iret = Utils.passwordFormat(mEditStr);
                    switch (iret) {
                        case 0:
                            // 昵称修改
                            ModifyInfoRequest(UserLoginState.getInstance().getUserInfo().getToken(), mEditStr, UserLoginState.getInstance().getUserInfo().getGender(), UserLoginState.getInstance().getUserInfo().getAvatar());
                            break;
                        case -1:
                            UIs.showToast(getString(R.string.rule_nick_null));
                            break;
                        case -2:
                            UIs.showToast(getString(R.string.rule_nick_error_notice));
                            break;
                        case -3:
                            UIs.showToast(getString(R.string.rule_nick_max_notice));
                            break;
                    }

                } else if (mode == 1) {
                    // 性别修改
                    ModifyInfoRequest(UserLoginState.getInstance().getUserInfo().getToken(), UserLoginState.getInstance().getUserInfo().getName(), clickId, UserLoginState.getInstance().getUserInfo().getAvatar());
                } else if (mode == 2) {

                }

                break;

            case R.id.mdetail_activity_clear_icon:
                mEdit.setText("");
                break;

        }
    }

    /**
     * 为View绑定数据
     */
    private void data2View() {


        List<ImageInfo> listImage = new ArrayList<>();
        List<ImageFloder> mImageFloders = loader.getMImageFloders();
        for (ImageFloder afolder : mImageFloders) {
//          File mImgDir = loader.getDirFile();
            if (TextUtils.isEmpty(afolder.getDir())) {
                continue;
            }
            File mImgDir = new File(afolder.getDir());
            List<String> mImgs = Arrays.asList(mImgDir.list());

            for (String url : mImgs) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setUrl("file://" + afolder.getDir() + "/" + url);
                imageInfo.setType(0);
                imageInfo.setDefaultResId(R.drawable.sarrs_pic_no_default);
                listImage.add(imageInfo);
            }
        }
        ImageInfo imageInfo2 = new ImageInfo();
        imageInfo2.setType(1);
        imageInfo2.setDefaultResId(R.drawable.sarrs_pic_no_default);
        imageInfo2.setResId(R.drawable.sarrs_pic_camera);
        listImage.add(0, imageInfo2);
        if (mGridAdapter == null) {
            mGridAdapter = new ModifyUserGridViewAdapter(this);
            mGridAdapter.setData(listImage);
        }
        mGrid.setAdapter(mGridAdapter);

    }

    @Override
    protected void handleInfo(Message msg) {
        // 加载本地图片结束
        switch (msg.what) {
            case ConstantUtils.LOAD_END:
                data2View();
                break;
            default:
                break;
        }


    }


    private int clickId;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickId = position;

        switch (parent.getId()) {
            case R.id.mdetail_activity_tx_grid:
                if (position == 0) {
                    takePhoto();
                } else {
                    Intent intent = new Intent(this, ChaojishipinCutActivity.class);
                    if (parent.getAdapter() instanceof ModifyUserGridViewAdapter) {
                        ModifyUserGridViewAdapter adapter = (ModifyUserGridViewAdapter) parent.getAdapter();
                        ImageInfo info = (ImageInfo) adapter.getItem(position);
//                       String putUrl="file://" + adapter.getDirPath() + "/" +info.getUrl();
                        String putUrl = info.getUrl();
                        intent.putExtra("uri", putUrl);
                        intent.putExtra("mode", 2);
                        startActivity(intent);
//                         this.finish();
                    }
                }
                break;

            case R.id.mdetail_activity_xb_lv:
                for (int i = 0; i < listInfo.size(); i++) {
                    if (i == position) {
                        listInfo.get(i).setIsClick(true);
                    } else {
                        listInfo.get(i).setIsClick(false);
                    }
                }
                mListViewAdapter.setGenderList(listInfo);
                mListViewAdapter.notifyDataSetChanged();

                break;


        }
    }

    List<GenderInfo> listInfo = null;

    void initData() {
        listInfo = new ArrayList<GenderInfo>();
        GenderInfo info1 = new GenderInfo(getString(R.string.gender_0), false);
        GenderInfo info2 = new GenderInfo(getString(R.string.gender_1), false);
        GenderInfo info3 = new GenderInfo(getString(R.string.gender_2), false);

        listInfo.add(info1);
        listInfo.add(info2);
        listInfo.add(info3);

        // 初始化进来显示
        listInfo.get(UserLoginState.getInstance().getUserInfo().getGender()).setIsClick(true);


    }

    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.modifydetailactivity, null);
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
        unregisterReceiver(pictureReceiver);
    }


    @Override
    public void onLoginCancel() {

    }

    @Override
    public void onLoginComplete(BaseUserInfo user) {
        this.finish();
        startActivity(new Intent(ChaojishipinModifyUserInfoDetailActivity.this, ChaoJiShiPinMainActivity.class));
    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginFailed() {

    }


    /**
     * 拍照获取图片
     */
    public static Uri photoUri;
    public static final int PICK_FROM_CAMERA = 1;

    private void takePhoto() {

//执行拍照前，应该先判断SD卡是否存在
//        String SDState = Environment.getExternalStorageState();
//        if(SDState.equals(Environment.MEDIA_MOUNTED))
//        {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
//            /***
//             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
//             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
//             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
//             */
//            ContentValues values = new ContentValues();
//            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
//            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//            /**-----------------*/
//            startActivityForResult(intent, PICK_FROM_CAMERA);
//
//        }else{
//            Toast.makeText(this,"内存卡不存在", Toast.LENGTH_LONG).show();
//        }

        photoUri = createImagePathUri(this);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == PICK_FROM_CAMERA) {

            Intent intent = new Intent();
            intent.setClass(this, ChaojishipinCutActivity.class);
            intent.setData(photoUri);
            intent.putExtra("mode", 0);
            startActivity(intent);
//            this.finish();
            if (data == null) {
                return;
            }
            if (data.getData() == null) {
                Toast.makeText(this, "uri is null", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 设置图片的存储位置
     *
     * @param context
     * @return 图片uri
     */
    private Uri createImagePathUri(Context context) {
        Uri imageFilePath = null;
        String status = Environment.getExternalStorageState();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (status.equals(Environment.MEDIA_MOUNTED)) {//
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
        }
        Log.i("", "图片的位置:" + imageFilePath.toString());
        return imageFilePath;
    }

    protected void registerImageReceiver() {
        pictureReceiver = new PictureReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(imagePickFinish);
        this.registerReceiver(pictureReceiver, intentfilter);
    }

    // 修改头像广播
    class PictureReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHeadUrl = intent.getStringExtra(ChaojishipinCutActivity.IMAGE_URL);
            mode = intent.getIntExtra("mode", -1);
            // 修改头像，先上传头像获取地址，然后将地址修改到用户信息
            HttpApi.uploadFile(mHeadUrl).start(new RequestUploadHeaderListener());

        }
    }
}