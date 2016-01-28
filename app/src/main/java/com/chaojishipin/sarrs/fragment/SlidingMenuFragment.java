package com.chaojishipin.sarrs.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinMainActivity;
import com.chaojishipin.sarrs.activity.ChaojishipinModifyUserInfoActivity;
import com.chaojishipin.sarrs.activity.ChaojishipinRegisterActivity;
import com.chaojishipin.sarrs.activity.HistoryRecordActivity;
import com.chaojishipin.sarrs.activity.SaveActivity;
import com.chaojishipin.sarrs.adapter.SlidingMenuGVAdapter;
import com.chaojishipin.sarrs.adapter.SlidingMenuLeftAdapter;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.download.activity.DownloadActivity;
import com.chaojishipin.sarrs.http.parser.SlidingMenuLeftParser;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.FileCacheManager;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.SharePreferenceManager;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.NoScrollGridViewNodivider;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * 展现首页侧边栏的Fragment
 * Created by zhangshuo on 2015/6/4.
 */
public class SlidingMenuFragment extends ChaoJiShiPinBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView mLeftMenuListView;

    public NoScrollGridViewNodivider mLeftMenuGridView;
    /**
     * 左侧用于显示数据列表的Adapter)
     */
    public SlidingMenuLeftAdapter mLeftAdapter;
    public SlidingMenuGVAdapter mLeftGVAdapter;
    SlidingMenu mSlideMenu;
    private ImageView mIcon;
    private TextView mName;
    public Button msetBtn;
    private ImageView user_head_bg;

//    public Button mDownBtn;
//    public Button mSaveBtn;

    private ChaoJiShiPinMainActivity context;

    public static final String CHANNEL_LIST = "channel_list";
    public SlidingMenuLeftAdapter getmLeftAdapter() {
        return mLeftAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slidingmenu_fragment_layout, container, false);
        initView(view);
        executeSlidingMenuLeftLogic();
        return view;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    public void setSlideMenu(SlidingMenu menu) {
        this.mSlideMenu = menu;
    }

    private void initView(View view) {
        context = (ChaoJiShiPinMainActivity) getActivity();

        mLeftMenuListView = (ListView) view.findViewById(R.id.slidingmenu_fragment_layout_channle_list);
        mLeftMenuGridView = (NoScrollGridViewNodivider) view.findViewById(R.id.slidingmenu_gv_menu);
        user_head_bg = (ImageView) view.findViewById(R.id.user_head_bg);
        mLeftAdapter = new SlidingMenuLeftAdapter(context, null);
        mLeftGVAdapter = new SlidingMenuGVAdapter(context,null);
        mLeftMenuListView.setAdapter(mLeftAdapter);
        mLeftMenuGridView.setAdapter(mLeftGVAdapter);
        mLeftMenuListView.setOnItemClickListener(this);
        mLeftMenuGridView.setOnItemClickListener(this);
        mIcon = (ImageView) view.findViewById(R.id.main_fragment_user_icon);
        int i = new Random().nextInt(4);
        if(i == 0){
            user_head_bg.setImageResource(R.drawable.user_header_bg1);
        }else if(i == 1){
            user_head_bg.setImageResource(R.drawable.user_header_bg2);
        }else if(i == 2){
            user_head_bg.setImageResource(R.drawable.user_header_bg3);
        }else if(i == 3){
            user_head_bg.setImageResource(R.drawable.user_header_bg4);
        }
        mName = (TextView) view.findViewById(R.id.main_fragment_user_name);
        msetBtn = (Button) view.findViewById(R.id.main_fragment_user_setting);
        mIcon.setOnClickListener(this);
        mName.setOnClickListener(this);

        msetBtn.setOnClickListener((ChaoJiShiPinMainActivity) getActivity());
//        mDownBtn.setOnClickListener((ChaoJiShiPinMainActivity) getActivity());
//        mSaveBtn.setOnClickListener((ChaoJiShiPinMainActivity) getActivity());

        boolean isLogin = UserLoginState.getInstance().isLogin();
        if (isLogin) {
            String imgUrl = UserLoginState.getInstance().getUserInfo().getAvatar();
            String name = UserLoginState.getInstance().getUserInfo().getName();
            if (!TextUtils.isEmpty(name)) {
                mName.setText(name);
            } else {
                mName.setText(getString(R.string.login_user_default_name));
            }
            if (!TextUtils.isEmpty(imgUrl)) {
                DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_pic_main_fragment_user_icon_default
                        )
                        .showImageForEmptyUri(R.drawable.sarrs_pic_main_fragment_user_icon_default
                        )
                        .showImageOnLoading(R.color.color_e7e7e7
                        )
                        .build();

                ImageLoader.getInstance().displayImage(imgUrl, mIcon, options1);
            }
        } else {
            mName.setText(getString(R.string.login_user_default_name));
            mIcon.setImageResource(R.drawable.sarrs_pic_main_fragment_user_icon_default);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        boolean isLogin = UserLoginState.getInstance().isLogin();
        if (isLogin) {
            String imgUrl = UserLoginState.getInstance().getUserInfo().getAvatar();
            String name = UserLoginState.getInstance().getUserInfo().getName();
            if (!TextUtils.isEmpty(name)) {
                mName.setText(name);
            } else {
                mName.setText(getString(R.string.login_user_default_name));
            }
            if (!TextUtils.isEmpty(imgUrl)) {
                ImageLoader.getInstance().displayImage(imgUrl, mIcon);
            }
        } else {
            mName.setText(getString(R.string.login_user_default_name));
            mIcon.setImageResource(R.drawable.sarrs_pic_main_fragment_user_icon_default);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fragment_user_icon:
            case R.id.main_fragment_user_name:
                if (UserLoginState.getInstance().isLogin()) {
                    startActivity(new Intent(context, ChaojishipinModifyUserInfoActivity.class));
                } else {
                    startActivity(new Intent(context, ChaojishipinRegisterActivity.class));
                }
                break;
        }
    }

    private void executeSlidingMenuLeftLogic() {
        try {

            if (NetWorkUtils.isNetAvailable()) {
                //加载完成后再进行网络请求
                 requestSlidingMenuLeftData();
            } else {
                //获取本地缓存的侧边栏数据]
                  loadLocalmenuData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLocalmenuData(){
        slidings_gv.clear();
        try {
            LogUtil.e("wulianshu","load local menu data");
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.SHARE_APP_TAG, Activity.MODE_PRIVATE);
            String data =sharedPreferences.getString(CHANNEL_LIST,"");
            LogUtil.e("wulianshu","data:"+data);
            if(TextUtils.isEmpty(data)){
                return;
            }
            JSONArray array = new JSONArray(data);
            JSONObject dataObj = new JSONObject();
            dataObj.put("status",ConstantUtils.REQUEST_SUCCESS);
            dataObj.put("items",array);
            SlidingMenuLeftParser slidingMenuLeftParser = new SlidingMenuLeftParser();
            SarrsArrayList result = slidingMenuLeftParser.parse(dataObj);
            slidings_lv = result;

            for (int i = 0;i<slidings_lv.size();i++){
                if( ((SlidingMenuLeft)slidings_lv.get(i)).getContent_type().equals("10") ){
                    break;
                }else{
                    slidings_gv.add((SlidingMenuLeft)slidings_lv.get(i));
                }
            }
            for(int i=0;i<slidings_gv.size();i++){
                slidings_lv.remove(0);
            }
            if(slidings_gv.size()>0) {
                mLeftMenuGridView.setVisibility(View.VISIBLE);
                showSlidingGVMenu(slidings_gv);
            }else{
                mLeftMenuGridView.setVisibility(View.GONE);
            }
            if(slidings_lv.size()>0) {
                showSlidingMenu(slidings_lv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private JSONObject getLocalSlidingMenuData() {
        JSONObject object = null;
        try {
            FileCacheManager cacheManager = FileCacheManager.getInstance();
            String data = cacheManager.redFileContent(ConstantUtils.FILECACHE_SLIDINGMENU_DATA);
            if (TextUtils.isEmpty(data)) {
                data = cacheManager.readFileFromAsset(ConstantUtils.FILECACHE_SLIDINGMENU_DATA);
            }
            object = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 执行请求侧边栏数据
     */
    public void requestSlidingMenuLeftData() {
        //执行请求首页侧边栏
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantUtils.SHARE_APP_TAG, Activity.MODE_PRIVATE);
        String data =sharedPreferences.getString(SlidingMenuFragment.CHANNEL_LIST,"");
        if("0".equals(ChaoJiShiPinMainActivity.lasttimeCheck) &&"1".equals(ChaoJiShiPinMainActivity.isCheck)  && !TextUtils.isEmpty(data)){
            loadLocalmenuData();
            return;
        }
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_SLDINGMENU_LETF_TAG);
        HttpApi.getSlidingMenuLeftRequest().start(new RequestSlidingMenuLeftListener(), ConstantUtils.REQUEST_SLDINGMENU_LETF_TAG);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.slidingmenu_fragment_layout_channle_list:
            Adapter adapter = parent.getAdapter();
//            msetBtn.setBackgroundResource(R.drawable.sarrs_pic_setting_normal);
//        mDownBtn.setTextColor(Color.WHITE);
                msetBtn.setTextColor(Color.WHITE);
                if (null != adapter && adapter instanceof SlidingMenuLeftAdapter) {
                    SlidingMenuLeftAdapter leftMenuAdapter = (SlidingMenuLeftAdapter) adapter;
                    SlidingMenuLeft menuLeft = (SlidingMenuLeft) leftMenuAdapter.getItem(position);
                    //如果当前类型不是分隔线则将相应的左侧侧边栏数据传送给首页
                    //设置选中状态
                    //refreshView();
                    if (!menuLeft.getContent_type().equals(ConstantUtils.SLIDINGMENU_LINE)) {
                        EventBus.getDefault().post(menuLeft);
                        if (mSlideMenu != null) {
                            mSlideMenu.showContent(true);
                        }
                        if (ConstantUtils.LIVE_CONTENT_TYPE.equalsIgnoreCase(menuLeft.getContent_type())) {
                            String version = menuLeft.getVersion();
                            LogUtil.e(Utils.LIVE_TAG, "################click live channel and update version################");
                            LogUtil.e(Utils.LIVE_TAG, "################server newest version is " + version);
                            SharePreferenceManager.saveVaule(context, ConstantUtils.LIVE_PUSH_KEY,
                                    version);
                        }
                        // 更新侧边栏ui
                        leftMenuAdapter.setSelectItem(position, view);
                    }
                }
                break;
            case R.id.slidingmenu_gv_menu:
                String content_type = ((SlidingMenuLeft)slidings_gv.get(position)).getContent_type();
                if("11".equals( content_type )){

                    Intent intent = new Intent(getActivity(),DownloadActivity.class);
                    startActivity(intent);

                }else if("12".equals( content_type )){

                    if (UserLoginState.getInstance().isLogin()) {
                        startActivity(new Intent(getActivity(), SaveActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), ChaojishipinRegisterActivity.class));
                    }


                }else if("14".equals( content_type )){
                Intent intent = new Intent(getActivity(), HistoryRecordActivity.class);
                startActivity(intent);
                }
                break;
        }
    }

//    public void setSelectedStatus(int index) {
//        for (int i = 0; i < mLeftAdapter.isSelectedList.size(); i++) {
//            if (i == index)
//                mLeftAdapter.isSelectedList.set(i, true);
//            else
//                mLeftAdapter.isSelectedList.set(i, false);
//        }
//    }


//    SarrsArrayList slidings;
    SarrsArrayList slidings_gv = new SarrsArrayList<SlidingMenuLeft>();
    SarrsArrayList slidings_lv = new SarrsArrayList<SlidingMenuLeft>();
//    public SarrsArrayList getSlidings() {
//        return slidings;
//    }
//    public void refreshView() {
//        if ((boolean) mLeftAdapter.isSelectedList.get(10)) {
//            msetBtn.setBackgroundResource(R.drawable.sarrs_pic_setting_press);
//            mDownBtn.setBackgroundResource(R.drawable.slinding_down_normal);
//            mDownBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
//            mSaveBtn.setBackgroundResource(R.drawable.slinding_save_normal);
//            mSaveBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
//        } else if ((boolean) mLeftAdapter.isSelectedList.get(11)) {
//            mDownBtn.setBackgroundResource(R.drawable.slinding_down_focus);
//            mDownBtn.setTextColor(getResources().getColor(R.color.color_c5242b));
//            msetBtn.setBackgroundResource(R.drawable.sarrs_pic_setting_normal);
//            mSaveBtn.setBackgroundResource(R.drawable.slinding_save_normal);
//            mSaveBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
//        } else if ((boolean) mLeftAdapter.isSelectedList.get(12)) {
//            mSaveBtn.setBackgroundResource(R.drawable.slinding_save_focus);
//            mSaveBtn.setTextColor(getResources().getColor(R.color.color_c5242b));
//            msetBtn.setBackgroundResource(R.drawable.sarrs_pic_setting_normal);
//            mDownBtn.setBackgroundResource(R.drawable.slinding_down_normal);
//            mDownBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
//        } else {
//            msetBtn.setBackgroundResource(R.drawable.sarrs_pic_setting_normal);
//            mDownBtn.setBackgroundResource(R.drawable.slinding_down_normal);
//            mDownBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
//            mSaveBtn.setBackgroundResource(R.drawable.slinding_save_normal);
//            mSaveBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
//        }
//    }

    private class RequestSlidingMenuLeftListener implements RequestListener<SarrsArrayList> {

        @Override
        public void onResponse(SarrsArrayList result, boolean isCachedData) {
            if(getActivity() != null) {
                SharedPreferences sharedPreferences = SlidingMenuFragment.this.getActivity().getSharedPreferences(ConstantUtils.SHARE_APP_TAG, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String channel_list = JsonUtil.toJSONString(result);
                editor.putString(CHANNEL_LIST, channel_list);
                editor.commit();
            }
            slidings_lv = result;
            for (int i = 0;i<slidings_lv.size();i++){
                if( ((SlidingMenuLeft)slidings_lv.get(i)).getContent_type().equals("10") ){
                    break;
                }else{
                    slidings_gv.add((SlidingMenuLeft)slidings_lv.get(i));
                }
            }
            for(int i=0;i<slidings_gv.size();i++){
                slidings_lv.remove(0);
            }
            if(slidings_gv.size()>0) {
                mLeftMenuGridView.setVisibility(View.VISIBLE);
                showSlidingGVMenu(slidings_gv);
            }else{
                mLeftMenuGridView.setVisibility(View.GONE);
            }
            if(slidings_lv.size()>0) {
                showSlidingMenu(slidings_lv);
            }
            SlidingMenuLeft leftDefault = getSlidingItemByTitle(ConstantUtils.TITLE_SUGGEST, result);
            if (null != leftDefault) {
                EventBus.getDefault().post(leftDefault);
            }
        }

        @Override
        public void netErr(int errorCode) {
            loadLocalmenuData();
        }

        @Override
        public void dataErr(int errorCode) {
            loadLocalmenuData();
        }
    }


    private SlidingMenuLeft getSlidingItemByTitle(String title, SarrsArrayList result) {
        for (int i = 0; i < result.size(); i++) {
            SlidingMenuLeft menuLeft = (SlidingMenuLeft) result.get(i);
            if (title.equals(menuLeft.getTitle())) {
                return menuLeft;
            }
        }
        return null;
    }

    public void showSlidingMenu(SarrsArrayList slidingMenuList) {
        mLeftAdapter.isSelectedList = new ArrayList(Collections.nCopies(slidingMenuList.size(), new Boolean(false)));
        mLeftAdapter.isSelectedList.set(1, true);
        //进行展现的相关操作
        if (null != slidingMenuList && slidingMenuList.size() > 0) {
            if (null != mLeftAdapter) {
                mLeftAdapter.setmDatas(slidingMenuList);
                mLeftAdapter.notifyDataSetChanged();
            } else {
                mLeftAdapter = new SlidingMenuLeftAdapter(context, slidingMenuList);
                mLeftMenuListView.setAdapter(mLeftAdapter);
            }

        }
    }
    public void showSlidingGVMenu(SarrsArrayList slidingMenuList) {
        //进行展现的相关操作
        if (null != slidingMenuList && slidingMenuList.size() > 0) {
            if (null != mLeftGVAdapter) {
                mLeftGVAdapter.setmDatas(slidingMenuList);
                mLeftGVAdapter.notifyDataSetChanged();
            } else {
                mLeftGVAdapter = new SlidingMenuGVAdapter(context, slidingMenuList);
                mLeftMenuGridView.setAdapter(mLeftGVAdapter);
            }
        }
    }
}
