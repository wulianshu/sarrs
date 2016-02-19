package com.chaojishipin.sarrs.thirdparty;



import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.letv.component.utils.NetWorkTypeUtils;

public class UIs {

    // 渲染layout
    public static View inflate(Context context, int resource, ViewGroup root, boolean attachToRoot) {
        return LayoutInflater.from(context).inflate(resource, root, attachToRoot);
    }

    public static View inflate(Context context, int resource, ViewGroup root) {
        return LayoutInflater.from(context).inflate(resource, root);
    }

    /**
     * 根据资源ID得到View
     * */
    public static View inflate(LayoutInflater inflater, int resource, ViewGroup root,
            boolean attachToRoot) {
        return inflater.inflate(resource, root, attachToRoot);
    }

    private static Toast mToast = null;

    public static void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(ChaoJiShiPinApplication.getInstatnce(), text, Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void showToast(int txtId) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(ChaoJiShiPinApplication.getInstatnce(), txtId, Toast.LENGTH_SHORT);
        mToast.setText(txtId);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static int dipToPx(int dipValue) {
        final float scale =ChaoJiShiPinApplication.getInstatnce().getResources().getDisplayMetrics().density;
        int pxValue = (int) (dipValue * scale + 0.5f);

        return pxValue;
    }

    public static float dipToPxFloat(int dipValue) {
        final float scale = ChaoJiShiPinApplication.getInstatnce().getResources().getDisplayMetrics().density;
        float pxValue = dipValue * scale;

        return pxValue;
    }

    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

    public static boolean hasNet(Context context) {
        NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo(context);

        if (networkInfo == null) {
            return false;
        }
        return true;
    }

    public static void setViewGone(View v) {
        if (v.getVisibility() != View.GONE) {
            v.setVisibility(View.GONE);
        }
    }

    public static void setViewVisible(View v) {
        if (v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
        }
    }



    public static void showLongToast(int txtId) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(ChaoJiShiPinApplication.getInstatnce(), txtId, Toast.LENGTH_LONG);
        mToast.setText(txtId);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    public static void showLongToast(String txt) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(ChaoJiShiPinApplication.getInstatnce(), txt, Toast.LENGTH_LONG);
        mToast.setText(txt);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

}
