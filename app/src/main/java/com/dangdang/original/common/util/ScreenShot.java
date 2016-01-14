package com.dangdang.original.common.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.View;

public class ScreenShot {
	// 获取指定Activity的截屏，保存到png文件
	private static Bitmap takeScreenShot(Activity activity) {
		try{
			// View是你需要截图的View
			View view = activity.getWindow().getDecorView();
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			Bitmap b1 = view.getDrawingCache();

			// 获取状态栏高度
			Rect frame = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			printLog("" + statusBarHeight);
			//
			// // 获取屏幕长和高
			// int width =
			// activity.getWindowManager().getDefaultDisplay().getWidth();
			// int height = activity.getWindowManager().getDefaultDisplay()
			// .getHeight();

			// 去掉标题栏
			//statusBarHeight += UiUtil.dip2px(activity, 50);
			printLog("w=" + b1.getWidth() + "h=" + b1.getHeight());
			Matrix matrix = new Matrix(); 
			matrix.postScale(0.1f, 0.1f);
			  
			Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, b1.getWidth(), b1.getHeight() - statusBarHeight, matrix, true);
			view.destroyDrawingCache();
			
			return b;
		}catch(Exception e){
			e.printStackTrace();
		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}
		return null;
	}

	// 程序入口
	public static Bitmap shoot(Activity a) {
		return ScreenShot.takeScreenShot(a);
	}

	private static void printLog(String msg) {

	}
}
