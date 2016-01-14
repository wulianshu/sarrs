package com.chaojishipin.sarrs.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

/**
 * 注册、实名认证、修改手机号码，自动获取验证码，并且填上
 *
 */
public class GetSmsContent extends ContentObserver {
	public final String SMS_URI_INBOX = "content://sms/inbox";
	private Activity activity = null;
	private String smsContent = "";
	private EditText verifyText = null;

	private String SMS_ADDRESS_PRNUMBER = "400888666";//短息发送提供商

	public GetSmsContent(Activity activity, Handler handler, EditText verifyText) {
		super(handler);
		this.activity = activity;
		this.verifyText = verifyText;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Cursor cursor = null;// 光标
		// 读取收件箱中指定号码的短信
		cursor = activity.managedQuery(Uri.parse(SMS_URI_INBOX),
				new String[] { "_id", "address", "body", "read" }, //要读取的属性
				"address=? and read=?", //查询条件是什么
				new String[] { SMS_ADDRESS_PRNUMBER, "0" },//查询条件赋值
				"date desc");//排序
		if (cursor != null) {// 如果短信为未读模式
			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				String smsbody = cursor.getString(cursor.getColumnIndex("body"));
				System.out.println("smsbody=======================" + smsbody);
				String regEx = "[^0-9]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(smsbody.toString());
				smsContent = m.replaceAll("").trim().toString();
				if(verifyText != null && null!=smsContent && !"".equals(smsContent)){
					verifyText.setText(smsContent);
					verifyText.setSelection(smsContent.length());
				}
			}
		}
	}
}