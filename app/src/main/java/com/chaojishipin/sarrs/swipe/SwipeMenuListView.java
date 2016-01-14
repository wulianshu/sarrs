package com.chaojishipin.sarrs.swipe;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xll
 * @date 2015 06 25
 * @des 解决下拉刷新需要实现onSrollListener
 */

public class SwipeMenuListView extends ListView implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    //////////////////////////swipe////////////////////////////////////

    private String TAG = "SwipeMenuListView";
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;
    private int MAX_Y = 5;
    private int MAX_X = 3;
    private float mDownX;
    private float mDownY;
    private int mTouchState;
    private int mTouchPosition;
    private SwipeMenuLayout mTouchView;
    private OnSwipeListener mOnSwipeListener;
    private SwipeMenuCreator mMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;
    //////////////////////////loadMore////////////////////////////////////
    // 区分当前操作是刷新还是加载
    public static final int REFRESH = 0;
    public static final int LOAD = 1;
    // load 布局
    private ProgressBar refreshing;
    private TextView noData;
    private TextView loadFull;
    private TextView more;
    private ProgressBar loading;
    private int scrollState;
    private int headerContentHeight;
    private boolean isLoading;// 判断是否正在加载
    private boolean loadEnable = true;// 开启或者关闭加载更多功能
    private boolean isLoadFull;
    private int pageSize = 10;
    private OnLoadListener onLoadListener;
    //////////////////////////下拉刷新////////////////////////////////////
    public final static int RELEASE_To_REFRESH = 0x111;
    public final static int PULL_To_REFRESH = 0x112;
    public final static int REFRESHING = 0x113;
    private final static int DONE = 0x114;
    private final static int LOADING = 0x115;
    // 实际的padding的距离与界面上偏移距离的比例
    private final static int RATIO = 3;
    private LayoutInflater inflater;
    private LinearLayout headView, footer;
    private TextView tipsTextview;
    private TextView lastUpdatedTextView;
    private ImageView arrowImageView;
    //private ProgressBar progressBar;
    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;
    // 用于保证startY的值在一个完整的touch事件中只被记录一次
    private boolean isRecored;
    private int startY;
    private int firstItemIndex;
    private int headContentWidth;
    private int headContentHeight;
    private int state;
    private boolean isBack;
    private OnRefreshListener onRefreshListener;
    private boolean isRefreshable;

    public SwipeMenuListView(Context context) {
        super(context);
        // swipe
        init();
        // 下拉刷新
        initView(context);
    }

    public SwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // 下拉刷新
        initView(context);
    }

    public SwipeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // 下拉刷新
        initView(context);

    }

    // 下拉刷新初始化
    void initView(Context context) {
        // 设置箭头特效
        inflater = LayoutInflater.from(context);
        footer = (LinearLayout) inflater.inflate(R.layout.listview_footer, null);
        loadFull = (TextView) footer.findViewById(R.id.loadFull);
        noData = (TextView) footer.findViewById(R.id.noData);
        more = (TextView) footer.findViewById(R.id.more);
        loading = (ProgressBar) footer.findViewById(R.id.loading);

        headView = (LinearLayout) inflater.inflate(R.layout.pull_to_refresh_header, null);
        arrowImageView = (ImageView) headView.findViewById(R.id.arrow);
        arrowImageView.setMinimumWidth(70);
        arrowImageView.setMinimumHeight(50);

        tipsTextview = (TextView) headView.findViewById(R.id.tip);
        lastUpdatedTextView = (TextView) headView.findViewById(R.id.lastUpdate);
        refreshing = (ProgressBar) headView.findViewById(R.id.refreshing);

        // 为listview添加头部和尾部，并进行初始化
        headerContentHeight = headView.getPaddingTop();
        measureView(headView);
        headerContentHeight = headView.getMeasuredHeight();
        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();

        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();

        LogUtil.v(TAG, "width:" + headContentWidth + " height:" + headContentHeight);
        addHeaderView(headView, null, false);
        addFooterView(footer, null, false);
        setOnScrollListener(this);

        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(250);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        isRefreshable = false;
        this.setOnScrollListener(this);
        this.setOnItemClickListener(this);
        this.setFocusable(false);
        //this.setFocusableInTouchMode(false);
        //this.setDescendantFocusability(AdapterView.FOCUS_BEFORE_DESCENDANTS);

    }


    // 用来计算header大小的。
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    // swipe init
    private void init() {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SwipeMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(menu);
                }
            }

            @Override
            public void onItemClick(SwipeMenuView view, SwipeMenu menu,
                                    int index) {
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onMenuItemClick(
                            view.getPosition(), menu, index);
                }
                if (mTouchView != null) {
                    mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
            return super.onTouchEvent(ev);
        int action = MotionEventCompat.getActionMasked(ev);
        action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                if (mTouchPosition == oldPos && mTouchView != null
                        && mTouchView.isOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    mTouchView.onSwipe(ev);
                    return true;
                }

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    return super.onTouchEvent(ev);
                }
                if (view instanceof SwipeMenuLayout) {
                    mTouchView = (SwipeMenuLayout) view;
                }
                if (mTouchView != null) {
                    mTouchView.onSwipe(ev);
                }
                ////// 下拉刷新//////
                if (firstItemIndex == 0 && !isRecored) {
                    isRecored = true;

                    // 触摸屏幕的位置
                    startY = (int) ev.getY();
                    LogUtil.e(TAG, "在down时候记录当前位置" + " startY:" + startY);
                }

                ////// 下拉刷新//////

                break;
            case MotionEvent.ACTION_MOVE:

                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (mTouchState == TOUCH_STATE_X) {
                    LogUtil.e("Touch sate", "0");
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                    }
                    getSelector().setState(new int[]{0});
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(dy) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                        ////// 下拉刷新//////
                        LogUtil.e("Touch sate", "1");

                        ////// 下拉刷新//////
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                        if (mOnSwipeListener != null) {
                            mOnSwipeListener.onSwipeStart(mTouchPosition);
                        }
                    }
                } else {
                    LogUtil.e("Touch sate", "2");
                    ////// 下拉刷新//////
                    whenMove(ev);
                    ////// 下拉刷新//////
                }


                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                        if (!mTouchView.isOpen()) {
                            mTouchPosition = -1;
                            mTouchView = null;
                        }
                    }
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                ////// 下拉刷新//////
                if (state != REFRESHING && state != LOADING) {
                    if (state == DONE) {
                        // 什么都不做
                    }

                    if (state == PULL_To_REFRESH) {
                        state = DONE;
                        changeHeaderViewByState();
                        LogUtil.e(TAG, "由下拉刷新状态，到done状态");
                    }

                    if (state == RELEASE_To_REFRESH) {
                        state = REFRESHING;
                        changeHeaderViewByState();
                        onRefresh();
                        LogUtil.e(TAG, "由松开刷新状态，到done状态");
                    }
                }

                isRecored = false;
                isBack = false;
                ////// 下拉刷新//////
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {


        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    public void smoothOpenMenu(int position) {
        if (position >= getFirstVisiblePosition()
                && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SwipeMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (SwipeMenuLayout) view;
                mTouchView.smoothOpenMenu();
            }
        }
    }

    public void onRefreshComplete(String updateTime) {
        state = DONE;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        String date = format.format(new Date());
        lastUpdatedTextView.setText("最近更新:" + date);
        changeHeaderViewByState();
    }

    // 用于下拉刷新结束后的回调
    public void onRefreshComplete() {
        String currentTime = Utils.getCurrentTime();
        onRefreshComplete(currentTime);
    }

    // 用于加载更多结束后的回调
    public void onLoadComplete() {
        isLoading = false;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int position, SwipeMenu menu, int index);
    }

    public interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }


    /*
     * 定义下拉刷新接口
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    /*
     * 定义加载更多接口
     */
    public interface OnLoadListener {
        void onLoad();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstItemIndex = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        ifNeedLoad(view, scrollState);
    }


    // 根据listview滑动的状态判断是否需要加载更多
    private void ifNeedLoad(AbsListView view, int scrollState) {
        if (!loadEnable) {
            return;
        }
        try {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                    && !isLoading
                    && view.getLastVisiblePosition() == view
                    .getPositionForView(footer) && !isLoadFull) {
                onLoad();
                isLoading = true;
            }
        } catch (Exception e) {
        }
    }

    public void onLoad() {
        if (onLoadListener != null) {
            onLoadListener.onLoad();
        }
    }

    public void onRefresh() {
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    // 解读手势，刷新header状态
    private void whenMove(MotionEvent ev) {
        int tempY = (int) ev.getY();
        //Log.v(TAG, "tempY: " + tempY);

        /**
         * 手指移动过程中tempY数据会不断变化,当滑动到firstItemIndex,即到达顶部,
         * 需要记录手指所在屏幕的位置: startY = tempY ,后面作位置比较使用
         *
         * 如果手指继续向下推,tempY继续变化,当tempY-startY>0,即是需要显示header部分
         *
         * 此时需要更改状态：state = PULL_To_REFRESH
         */
        if (!isRecored && firstItemIndex == 0) {
            isRecored = true;
            startY = tempY;
            LogUtil.e(TAG, "在move时候记录下位置" + " startY:" + startY);
        }

        if (state != REFRESHING && isRecored && state != LOADING) {
            /**
             * 保证在设置padding的过程中，当前的位置一直是在head，
             * 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
             */

            // 可以松手去刷新了
            if (state == RELEASE_To_REFRESH) {
                setSelection(0);

                // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {
                    state = PULL_To_REFRESH;
                    changeHeaderViewByState();
                    LogUtil.e(TAG, "由松开刷新状态转变到下拉刷新状态");
                }

                // 一下子推到顶了,没有显示header部分时,应该恢复DONE状态,这里机率很小
                else if (tempY - startY <= 0) {
                    state = DONE;
                    changeHeaderViewByState();
                    LogUtil.e(TAG, "---由松开刷新状态转变到done状态");
                } else {
                    // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                    // 不用进行特别的操作，只用更新paddingTop的值就行了
                }
            }

            // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
            if (state == PULL_To_REFRESH) {
                setSelection(0);

                /**
                 * 下拉到可以进入RELEASE_TO_REFRESH的状态
                 *
                 * 等于headContentHeight时,即是正好完全显示header部分
                 * 大于headContentHeight时,即是超出header部分更多
                 *
                 * 当header部分能够完全显示或者超出显示,
                 * 需要更改状态: state = RELEASE_To_REFRESH
                 */
                if ((tempY - startY) / RATIO >= headContentHeight) {
                    state = RELEASE_To_REFRESH;
                    isBack = true;
                    changeHeaderViewByState();
                    LogUtil.e(TAG, "由done或者下拉刷新状态转变到松开刷新");
                }

                // 上推到顶了,没有显示header部分时,应该恢复DONE状态
                else if (tempY - startY <= 0) {
                    state = DONE;
                    changeHeaderViewByState();
                    LogUtil.e(TAG, "由done或者下拉刷新状态转变到done状态");
                }
            }

            // done状态下
            if (state == DONE) {
                if (tempY - startY > 0) {
                    /**
                     * 手指移动过程中tempY数据会不断变化,当滑动到firstItemIndex,即到达顶部,
                     * 需要记录手指所在屏幕的位置: startY = tempY ,后面作位置比较使用
                     *
                     * 如果手指继续向下推,tempY继续变化,当tempY-startY>0,即是需要显示header部分
                     *
                     * 此时需要更改状态：state = PULL_To_REFRESH
                     */
                    //Log.v(TAG, "----------------PULL_To_REFRESH " + (tempY - startY));
                    state = PULL_To_REFRESH;
                    changeHeaderViewByState();
                }
            }

            // 更新headView的paddingTop
            if (state == PULL_To_REFRESH) {
                //Log.v(TAG, "----------------PULL_To_REFRESH2 " + (tempY - startY));
                headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);
            }

            // 继续更新headView的paddingTop
            if (state == RELEASE_To_REFRESH) {
                headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
            }
        }

    }

    /**
     * 这个方法是根据结果的大小来决定footer显示的。
     * <p>
     * 这里假定每次请求的条数为10。如果请求到了10条。则认为还有数据。如过结果不足10条，则认为数据已经全部加载，这时footer显示已经全部加载
     * </p>
     *
     * @param resultSize
     */
    public void setResultSize(int resultSize) {
        if (resultSize == 0) {
            isLoadFull = true;
            loadFull.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else if (resultSize > 0 && resultSize < pageSize) {
            isLoadFull = true;
            loadFull.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
        } else if (resultSize == pageSize) {
            isLoadFull = false;
            loadFull.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }

    }

    public void hideHeaderVeiw() {
        headView.setVisibility(View.GONE);

    }

    public void hideBottomView() {
        footer.setVisibility(View.GONE);

    }

    // 根据当前状态，调整header
    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH:
                arrowImageView.setVisibility(View.VISIBLE);
                //progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(animation);

                tipsTextview.setText("松开刷新");

                LogUtil.e(TAG, "当前状态，松开刷新");
                break;

            case PULL_To_REFRESH:
                //progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);
                lastUpdatedTextView.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.VISIBLE);

                /**
                 *  是否向下滑回，是由RELEASE_To_REFRESH状态转变来的
                 */
                if (isBack) {
                    isBack = false;
                    arrowImageView.clearAnimation();
                    arrowImageView.startAnimation(reverseAnimation);
                    tipsTextview.setText("下拉刷新");
                    //Log.v(TAG, "isBack: " + isBack);
                } else {
                    tipsTextview.setText("下拉刷新");
                    //Log.v(TAG, "isBack: " + isBack);
                }

                LogUtil.e(TAG, "当前状态，下拉刷新");
                break;

            case REFRESHING:
                LogUtil.e(TAG, "REFRESHING...");
                headView.setPadding(0, 0, 0, 0);

                //progressBar.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.INVISIBLE);
                tipsTextview.setText("正在刷新...");
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                LogUtil.e(TAG, "当前状态,正在刷新...");
                break;

            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);

                //progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();
                arrowImageView.setImageResource(R.drawable.pull_to_refresh_arrow);
                tipsTextview.setText("下拉刷新");
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                LogUtil.e(TAG, "当前状态，done");
                break;
        }
    }


    // 下拉刷新监听
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
        isRefreshable = true;
    }

    // 加载更多监听
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.loadEnable = true;
        this.onLoadListener = onLoadListener;
    }

    public boolean isLoadEnable() {
        return loadEnable;
    }

    // 设置加载更多
    public void setLoadEnable(boolean loadEnable) {
        this.loadEnable = loadEnable;
        this.removeFooterView(footer);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        LogUtil.e("onItemClick", "position0 " + position);
        if (view.getId() == R.id.more_icon) {
            LogUtil.e("onItemClick", "position1 " + position);
            this.smoothOpenMenu(position);
        } else if (view.getId() == R.id.main_small_more_icon) {
            LogUtil.e("onItemClick", "position2 " + position);
            this.smoothOpenMenu(position);
        }


    }
}
