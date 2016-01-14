package com.chaojishipin.sarrs.manager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.Utils;

/**
 * 顶部toast展现辅助类
 * Created by xll on 2015/08/17.
 */
public class SarrsToastManager {

    public static final long DURATION_3_SECONDS = 3000L;

    public static final int DISPLAY_OFFSET_IN_DP = 4;
    private final Runnable hideAction = new Runnable() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            mToast.animate().alpha(0.0f).translationY(mToast.getTranslationY()).setDuration(200).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mToast.setVisibility(View.GONE);
                    reset();
                }
            }).withStartAction(new Runnable() {
                @Override
                public void run() {
                    isShown = false;
                }
            }).start();
        }
    };
    private View mToast;
    private TextView mContentText;
    private TextView mRevertButton;
    private View mDivider;
    private Handler mHandler;
    private boolean isShown;
    private boolean isRevert = false;
    private Runnable mRevertAction;

    public SarrsToastManager(View mToast, Handler mHandler) {
        this.mToast = mToast;
        this.mHandler = mHandler;

        mContentText = (TextView) mToast.findViewById(R.id.sarrs_top_toast_text);
        mRevertButton = (TextView) mToast.findViewById(R.id.sarrs_top_toast_revert);
        mDivider = mToast.findViewById(R.id.sarrs_top_toast_divider);
        assert mContentText != null;
        assert mRevertButton != null;
    }

    public SarrsToastManager setText(CharSequence content) {
        mContentText.setText(content);
        return this;
    }

    public SarrsToastManager setText(@StringRes int stringResId) {
        return setText(mToast.getContext().getString(stringResId));
    }

    public SarrsToastManager withRevertAction(@Nullable Runnable action) {
        isRevert = true;
        mRevertAction = action;
        mRevertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (mRevertAction != null) {
                    mRevertAction.run();
                }
            }
        });
        return this;
    }

    public SarrsToastManager withToastClickAction(View.OnClickListener onClickListener) {
        mContentText.setOnClickListener(onClickListener);
        return this;
    }

    public void show() {
        show(DURATION_3_SECONDS);
    }

    public void hide() {
        mHandler.removeCallbacks(hideAction);
        mHandler.post(hideAction);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void show(long duration) {
        if (!isShown) {
            mToast.animate().alpha(1.0f).translationY(mToast.getTranslationY()).setDuration(200).withStartAction(new Runnable() {
                @Override
                public void run() {
                    mToast.setVisibility(View.VISIBLE);
                    isShown = true;
                }
            }).start();
        }
        mRevertButton.setVisibility(isRevert ? View.VISIBLE : View.GONE);
        mDivider.setVisibility(isRevert ? View.VISIBLE : View.GONE);
        mHandler.removeCallbacks(hideAction);
        mHandler.postDelayed(hideAction, duration);

    }

    private void reset() {
        isRevert = false;
        mRevertAction = null;
    }

    public boolean isShown() {
        return isShown;
    }

    public int getDisplayOffset(Context context) {
        return Utils.dip2px(DISPLAY_OFFSET_IN_DP);
    }
}
