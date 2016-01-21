package com.chaojishipin.sarrs.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
	private static Toast mToast = null;

	private synchronized static Toast getToast(Context context){
		if(mToast == null)
			mToast = new Toast(context.getApplicationContext());
		return mToast;
	}

	private static void setText(Context context, String str){
		mToast = getToast(context);
		TextView tv = new TextView(context.getApplicationContext());
		tv.setTextColor(Color.WHITE);
		tv.setText(str);
		int p = Utils.dip2px(10);
		tv.setPadding(p, p, p, p);
		tv.setBackgroundColor(0xaa000000);
		mToast.setView(tv);
	}

	public static void showLongToast(Context context,int id,int position){
		if(context == null)
			return;
		String str = context.getString(id) + position;
		showLongToast(context, str);
	}

	public static void showLongToast(Context context,int id){
		if(context == null)
			return;
		String str = context.getString(id);
		showLongToast(context, str);
	}
	
	public static void showLongToast(Context context,String content){
		if(context == null)
			return;
		setText(context, content);
		getToast(context).setDuration(Toast.LENGTH_LONG);
		getToast(context).show();
	}
	
	public static void showShortToast(Context context,int id){
		if(context == null)
			return;
		String str = context.getString(id);
		showShortToast(context, str);
	}
	
	public static void showShortToast(Context context,String content){
		if(context == null)
			return;
		setText(context, content);
		getToast(context).setDuration(Toast.LENGTH_SHORT);
		getToast(context).show();
	}
	
	public static void toastPrompt(Context context,int id,int time){
		if(context == null)
			return;
		String str = context.getString(id);
		setText(context, str);
		getToast(context).setDuration(time);
		getToast(context).show();
	}
	
	public static void cancelToast(){
		if(null != mToast){
			mToast.cancel();
			mToast = null;
		}
	}
}
