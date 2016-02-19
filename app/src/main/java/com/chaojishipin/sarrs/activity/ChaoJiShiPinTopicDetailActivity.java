package com.chaojishipin.sarrs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.Topic;
import com.chaojishipin.sarrs.fragment.TopicDetailFragment;
import com.chaojishipin.sarrs.listener.onRetryListener;
import com.chaojishipin.sarrs.swipe.SwipeMenu;
import com.chaojishipin.sarrs.thirdparty.share.ShareDataConfig;
import com.chaojishipin.sarrs.uploadstat.UmengPagePath;
import com.chaojishipin.sarrs.utils.AllActivityManager;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.widget.PullToRefreshSwipeListView;
import com.ibest.thirdparty.share.presenter.ShareManager;

import java.util.ArrayList;

public class ChaoJiShiPinTopicDetailActivity extends ChaoJiShiPinBaseActivity implements PullToRefreshSwipeListView.OnSwipeListener, PullToRefreshSwipeListView.OnMenuItemClickListener, View.OnClickListener,
        AdapterView.OnItemClickListener, onRetryListener {

    private LinearLayout linearLayout;
    //分享返回数据
    private ArrayList<String> shareParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisibile(false);
        Bundle bundle = this.getIntent().getExtras();
        Topic topic = (Topic) bundle.getSerializable("topic");
        if (topic == null)
        {
            shareParams = ShareDataConfig.jumpFromShare(this);
            topic = new Topic();
            topic.setTid(shareParams.get(0));
        }
        linearLayout = (LinearLayout) this.findViewById(R.id.topicdetail_content);
        TopicDetailFragment topicDetailFragment = new TopicDetailFragment();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("topic", topic);
        topicDetailFragment.setArguments(mBundle);
        replaceFragment(R.id.topicdetail_content, topicDetailFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //新浪授权
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        ShareManager.authorCallback(requestCode, resultCode, data);
    }


    @Override
    protected View setContentView() {
        return mInflater.inflate(R.layout.activity_chaojishipin_topicdetail, null);
    }
    @Override
    protected void handleInfo(Message msg) {
    }

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chao_ji_shi_pin_topic_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // return true;//返回真表示返回键被屏蔽掉
            back();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onMenuItemClick(int position, SwipeMenu menu, int index) {

    }

    @Override
    public void onSwipeStart(int position) {

    }

    @Override
    public void onSwipeEnd(int position) {

    }

    @Override
    public void onRetry() {

    }

    private void back()
    {
        if (AllActivityManager.getInstance().isExistActivy("ChaoJiShiPinMainActivity")) {
            this.finish();
        }else {
            Intent intent = new Intent(ChaoJiShiPinTopicDetailActivity.this, ChaoJiShiPinMainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        UmengPagePath.beginpage(ConstantUtils.AND_TOPIC_DETAIL,this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        UmengPagePath.endpage(ConstantUtils.AND_TOPIC_DETAIL,this);
        super.onPause();
    }
}
