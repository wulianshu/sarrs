package com.ibest.thirdparty.share.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.ibest.thirdparty.R;


public abstract class BottomDialog {

	public static final int GRAVITY_BOTTOM = Gravity.BOTTOM;
	public static final int GRAVITY_RIGHT = Gravity.RIGHT;
	private static final int WIDTH_DEFAULT = ViewGroup.LayoutParams.MATCH_PARENT;

	protected static Dialog mDialog;

	public BottomDialog(){

	}

	/**
	 * 设置view.children中存在click监听事件的控件，
	 * @param view parentview
	 */
	protected abstract void setListener(View view);

	protected void init(Activity context, View view){
		init(context, view, GRAVITY_BOTTOM, WIDTH_DEFAULT);
	}

	protected void init(Activity context, View view, int gravity, int width) {
		if (context == null || view == null) {
			return;
		}

		// 创建dialog弹窗
		int theme = gravity == GRAVITY_RIGHT ? R.style.rightTransparentFrameWindowStyle : R.style.transparentFrameWindowStyle;
		dismiss();
		mDialog = new Dialog(context, theme);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (gravity == GRAVITY_RIGHT) {
			mDialog.setContentView(view, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			mDialog.setTitle(context.getString(R.string.share_to));

		} else {
			mDialog.setContentView(view, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		Window window = mDialog.getWindow();

		// 设置显示动画
		window.setWindowAnimations(R.style.popwindow_anim_style);
		if (gravity == GRAVITY_RIGHT)
		{
			window.setGravity(gravity|Gravity.TOP);
		}else {
			window.setGravity(gravity);
		}

		WindowManager.LayoutParams wl = window.getAttributes();

		wl.x = 0;
		wl.y = 0;
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = width;
		if (gravity == GRAVITY_RIGHT)
		{
			wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
		}else {
			wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		}

		// 设置显示位置
		mDialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		mDialog.setCanceledOnTouchOutside(true);
		applyCompat();
		setListener(view);
	}

	public void show() {
		if (mDialog != null) {
			mDialog.show();
		}
	}

	public static void dismiss() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	public boolean isShow() {
		if (mDialog != null) {
			return mDialog.isShowing();
		}
		return false;
	}

	/**
	 * 解决Android 4.4 Dialog 被状态栏遮挡的解决方法
	 */
	private void applyCompat() {
		if (Build.VERSION.SDK_INT < 19) {
			return;
		}
		mDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

}
