package com.chaojishipin.sarrs.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.activity.ChaoJiShiPinBaseActivity;
import com.chaojishipin.sarrs.listener.onRetryListener;


/**
 * Created by xll on 2015/8/12.
 * 网络状态
 */
public class NetStateView extends FrameLayout implements View.OnClickListener {

    private View netErrorView;
    private Button retryBtn;
    private TextView error_title;


    private int taskType;
    private Context mContext;





    public interface onNetErrorListener{

        void onNetError(Context context );
        void onDataError(Context context );
        void onNetSuccess(Context context);


    }


    public NetStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext=context;
        View layout = LayoutInflater.from(context).inflate(R.layout.fragment_main_net, null);
        netErrorView = layout.findViewById(R.id.net_error_img);
        retryBtn = (Button) layout.findViewById(R.id.net_error_btn_retry);
        retryBtn.setOnClickListener(this);
        error_title = (TextView)layout.findViewById(R.id.net_error_title);
        addView(layout);
    }
    public NetStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext=context;
    }

    public NetStateView(Context context){
        this(context, null, 0);
        this.mContext = context;
    }

    // 设置 error情况下view显示提示
    public void setErrorTitle(int resId){
        error_title.setText(resId);
    }

    public void showProgress(int taskType){
        setVisibility(View.VISIBLE);
        findViewById(R.id.net_error_btn_retry).setOnClickListener(null);
        netErrorView.setVisibility(View.GONE);
        this.taskType = taskType;
    }

    public void hide(){
        setVisibility(View.GONE);
    }

    public void showNetError(){
        setVisibility(View.VISIBLE);
        netErrorView.setVisibility(View.VISIBLE);
        findViewById(R.id.net_error_btn_retry).setOnClickListener(this);
    }

    public void showLoadFail(){
        setVisibility(View.VISIBLE);
        netErrorView.setVisibility(View.GONE);
    }

    onRetryListener lis;
   public void setOnRetryLisener(onRetryListener lis){
     this.lis=lis;
   }
    @Override
    public void onClick(View v) {
        if(lis != null){
            //showProgress(taskType);
            lis.onRetry();
        }
    }



}
