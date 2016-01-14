package com.chaojishipin.sarrs.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.ApplicatitonUtil;

/**
 * Created by vicky on 15/11/7.
 */
public class PopupDialog {
    private android.app.AlertDialog.Builder customBuilder;
    private Dialog dialog;
    private PopupButtonClickInterface buttonClick;
    static PopupDialog popDialog;
    private static Handler mHandler;
    private static int num = 0;
    private static Runnable mRunnable;
    private static Context context;
    private static boolean isEnterBackground = false;

    public void buildDialog(Context context,
                            int title,
                            int content,
                            int lText,
                            int rText,
                            PopupButtonClickInterface onclick)
    {
        if (customBuilder != null)
            return;
        buttonClick = onclick;
        customBuilder = new android.app.AlertDialog.Builder(context);
        customBuilder
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(lText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dismiss();
                                buttonClick.onLeftClick();
                            }
                        })
                .setNegativeButton(rText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dismiss();
                                buttonClick.onRightClick();
                            }
                        })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                            dismiss();
                        }
                        return false;
                    }
                });
        dialog = customBuilder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
        dialog.setCanceledOnTouchOutside(false);
    }

    public void show()
    {
        if (dialog != null && dialog.isShowing())
            return;
//        if (ApplicatitonUtil.isBackground(context))
//        {
//            isEnterBackground = true;
//            return;
//        }
        dialog.show();
    }

    public void showDialog(boolean enterForeground)
    {
//        isEnterBackground = false;
        show();
    }


    public void dismiss()
    {
//        isEnterBackground = false;
        if (dialog.isShowing())
            dialog.dismiss();
        dialog = null;
        customBuilder = null;
        popDialog = null;
    }

    public interface PopupButtonClickInterface
    {
        public void onLeftClick();
        public void onRightClick();
    }

    public static void showMobileNetworkAlert(final PopupButtonClickInterface onclick)
    {
        if (popDialog != null)
            return;
        synchronized (PopupDialog.class)
        {
            context = ChaoJiShiPinApplication.getInstatnce();
            if (context == null)
            {
                mHandler = new Handler();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(num<20){
                            num++;
                            context = ChaoJiShiPinApplication.getInstatnce();
                            if (context == null)
                                mHandler.postDelayed(mRunnable, 300);
                            else
                            {
                                popDialog = new PopupDialog();
                                popDialog.buildDialog(context,
                                        R.string.tip,
                                        R.string.wireless_tip,
                                        R.string.continue_download,
                                        R.string.pause_download,
                                        onclick);
                                popDialog.show();
                            }
                        }else{
                            mHandler.removeCallbacks(mRunnable);
                        }

                    }
                };
                mHandler.postDelayed(mRunnable, 300);

            }else {
                popDialog = new PopupDialog();
                popDialog.buildDialog(context,
                        R.string.tip,
                        R.string.wireless_tip,
                        R.string.continue_download,
                        R.string.pause_download,
                        onclick);
                popDialog.show();
            }

        }
    }

    public static void showMobileDialog(boolean enterForeground)
    {
        if(popDialog != null)
            popDialog.showDialog(enterForeground);
    }

}
