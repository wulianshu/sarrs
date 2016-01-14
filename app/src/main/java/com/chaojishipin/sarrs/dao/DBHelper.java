package com.chaojishipin.sarrs.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chaojishipin.sarrs.utils.LogUtil;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "sarrs.db";
	private static final int VERSION = 1;
	private String TAG="DBHelper";
;
	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS search_record (id integer primary key autoincrement, name text unique)");
		String dropSql="drop table if exists favorite;";
		db.execSQL(dropSql);
		String sql="CREATE TABLE IF NOT EXISTS favorite (id integer primary key autoincrement,type text ,cid text,img text ,aid text unique, gvid text unique,title text,totalepisode text,latestepisode text, history text,isend integer default 0,totaltime text,totalspecial text,tid text unique,createtime text ,createdate text)";
		db.execSQL(sql);
		db.execSQL("CREATE TABLE IF NOT EXISTS history_record (id text, timestamp text,title text,source text,category_name text,play_time text,gvid text,image text,category_id text,content_type text,durationtime text)");
		LogUtil.e(TAG,""+sql);
	}

	/**
	 * 数据库升级 版本号
	 */

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		default:
//			db.execSQL("DROP TABLE IF EXISTS search_record;");
//			onCreate(db);
			break;
		}
	}

}
