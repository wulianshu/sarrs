package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;

/**
 * Created by zhangshuo on 2015/6/15.
 */
public class TitleActionBar extends FrameLayout implements View.OnClickListener, View.OnTouchListener {

    public ImageView getmLeftButton() {
        return mLeftButton;
    }

    public void setmLeftButton(ImageView mLeftButton) {
        this.mLeftButton = mLeftButton;
    }

    private ImageView mLeftButton;
    private ImageView mRightButton;
    private TextView mTitle;
    private int leftButtonBackgroundResId;
    private int rightButtonBackgroundResId;
    private onActionBarClickListener mListener;
    private boolean mIsLeftButtonGone;
    private boolean mIsRightButtonGone;
    private TextView mRightMenuItem;
    private RelativeLayout mTitleActionBar;
    private GestureDetector mGesture;
    private Context mContext;


    public TitleActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleActionBar);
        leftButtonBackgroundResId =
                a.getResourceId(R.styleable.TitleActionBar_leftButtonDrawable, R.drawable.sarrs_pic_mainactivity_left_btn);
        mIsLeftButtonGone = a.getBoolean(R.styleable.TitleActionBar_leftButtonGone, false);
        mIsRightButtonGone = a.getBoolean(R.styleable.TitleActionBar_rightButtonGone, false);
        rightButtonBackgroundResId =
                a.getResourceId(R.styleable.TitleActionBar_rightButtonDrawable,
                        R.drawable.sarrs_pic_mainactivity_right_playhistory);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.mainactivity_actionbar_layout, this);
        findView();
    }

    private void findView() {
        mLeftButton = (ImageView) findViewById(R.id.mainactivity_left_btn);
        mRightButton = (ImageView) findViewById(R.id.mainactivity_right_btn);
        mTitle = (TextView) findViewById(R.id.mainactivity_title);
        mRightMenuItem = (TextView) findViewById(R.id.right_edit_btn);
        mLeftButton.setBackgroundResource(leftButtonBackgroundResId);
        mRightButton.setBackgroundResource(rightButtonBackgroundResId);
        mTitleActionBar = (RelativeLayout) findViewById(R.id.title_action_bar);
        mGesture = new GestureDetector(mContext, new GestureListener());

        if (mIsLeftButtonGone) {
            mLeftButton.setVisibility(GONE);
        }
        if (mIsRightButtonGone) {
            mRightButton.setVisibility(GONE);
        }
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mRightMenuItem.setOnClickListener(this);
        mTitleActionBar.setOnTouchListener(this);
    }

    public TitleActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setTitle(String mStr) {
        mTitle.setText("" + mStr);
    }

    public TitleActionBar(Context context) {
        this(context, null);
    }

    public onActionBarClickListener getListener() {
        return mListener;
    }

    public void setOnActionBarClickListener(onActionBarClickListener mListener) {
        this.mListener = mListener;
    }

    public interface onActionBarClickListener {
        void onTitleLeftClick(View v);

        void onTitleRightClick(View v);

        void onTitleDoubleTap();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGesture.onTouchEvent(event);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainactivity_left_btn:
                if (mListener != null) {
                    mListener.onTitleLeftClick(v);
                }
                break;
            case R.id.mainactivity_right_btn:
            case R.id.right_edit_btn:
                if (mListener != null) {
                    mListener.onTitleRightClick(v);
                }
                break;
        }
    }

    /**
     * 设置右侧的按钮是否可见
     *
     * @param flag
     */
    public void setmRightButtonVisibility(boolean flag) {
        if (flag) {
            mRightButton.setVisibility(View.VISIBLE);
        } else {
            mRightButton.setVisibility(View.GONE);
        }

    }


    public void setRightEditButtonVisibility(boolean flag) {
        if (flag) {
            mRightMenuItem.setVisibility(View.VISIBLE);
            setmRightButtonVisibility(false);
        } else {
            mRightMenuItem.setVisibility(View.GONE);
        }
    }

    public void setRightEditButtonText(String text) {
        mRightMenuItem.setText(text);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mListener != null) {
                mListener.onTitleDoubleTap();
            }
            return super.onDoubleTap(e);
        }
    }
}
