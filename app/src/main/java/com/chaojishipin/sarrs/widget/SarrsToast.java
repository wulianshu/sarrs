package com.chaojishipin.sarrs.widget;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.manager.SarrsToastManager;


/**
 * Created by xll on 2015/3/26.
 */
public class SarrsToast extends FrameLayout {

    private SarrsToastManager manager;

   /* public SarrsToast(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.sarrs_toast_layout, this);
        manager = new SarrsToastManager(this, new Handler());
        setAlpha(0.0f);
        setVisibility(View.GONE);
    }*/


    public SarrsToast(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        LayoutInflater.from(context).inflate(R.layout.sarrs_toast_layout, this);
        manager = new SarrsToastManager(this, new Handler());
        setAlpha(0.0f);
        setVisibility(View.GONE);
    }

    public SarrsToast(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.sarrs_toast_layout, this);
        manager = new SarrsToastManager(this, new Handler());
        setAlpha(0.0f);
        setVisibility(View.GONE);
    }

    public void setText(CharSequence content) {
        if(manager!=null){
            manager.setText(content);
        }

    }

    public void setText(@StringRes int stringResId) {
        if(manager!=null){
            manager.setText(stringResId);
        }
    }

    public void withRevertAction() {
        if(manager!=null){
            manager.withRevertAction(null);
        }

    }

    public void withRevertAction(Runnable action) {
        if(manager!=null){
            manager.withRevertAction(action);
        }

    }

    public void withToastClickAction(View.OnClickListener onClickListener) {
       if(manager!=null){
           manager.withToastClickAction(onClickListener);
       }
    }

    public void show() {
        if(manager!=null){
            manager.show();
        }

    }

    public void show(long duration) {
        if(manager!=null){
            manager.show(duration);
        }

    }

    public boolean isShown() {
        if(manager!=null){
            return manager.isShown();
        }else{
            return false;
        }

    }

    public void hide() {
        if(manager!=null){
            manager.hide();
        }

    }

   /* public int getDisplayOffset() {
        return manager.getDisplayOffset(getContext());
    }*/

}
