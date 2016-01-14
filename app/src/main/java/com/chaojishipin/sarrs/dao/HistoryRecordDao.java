package com.chaojishipin.sarrs.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.bean.HistoryRecord;
import com.chaojishipin.sarrs.bean.SearchKeyWords;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class HistoryRecordDao extends BaseDao<HistoryRecord> {


//	private static final String TABLE_NAME = "history_record";
	private final String TAG = "HistoryRecordDao";
	private static final String FIELD_ID = "id";
	private static final String FIELD_TIMESTMAP = "timestamp";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_SOURCE = "source";
	private static final String FIELD_CATEGORY_NAME = "category_name";
	private static final String FIELD_PLAY_TIME = "play_time";
	private static final String FIELD_GVID = "gvid";
	private static final String FIELD_IMAGE = "image";
	private static final String FIELD_CATEGORY_ID = "category_id";
	private static final String FIELD_CONTENT_TYPE = "content_type";
	private static final String FIELD_DURATIONTIME = "durationtime";

	public HistoryRecordDao(Context context) {
		super(context);
	}

	@Override
	public void delete(final String keyWords) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL("delete from " + HistoryRecord.tablename + " where " + FIELD_ID + " = '" + keyWords + "'");
				safelyCloseDataBase();
			}
		});
	}
	public void deleteByGvid(final String gvid) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL("delete from " + HistoryRecord.tablename + " where " + FIELD_GVID + " = '" + gvid + "'");
				safelyCloseDataBase();
			}
		});
	}

	@Override
	public void delAll() {
		SQLiteDatabase db = safelyGetDataBase();
		db.execSQL("delete from " + HistoryRecord.tablename);
		safelyCloseDataBase();
	}

	@Override
	public void save(final HistoryRecord bean) {
		if(bean.getId() !=null) {
			if (!isExistsAid(bean.getId())) {
		         save2db(bean);
			} else {
				update(bean);
				//update
			}
		}else{
			if(!isExistsGvid(bean.getGvid())){
				LogUtil.e("wulianshu","playtimedao:"+bean.getPlay_time());
				save2db(bean);
			}else{
				LogUtil.e("wulianshu","playtimedao:"+bean.getPlay_time());
				updatebygvid(bean);
			}
		}
	}

	public void save2db(final HistoryRecord bean){
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				ContentValues values = new ContentValues();
				values.put(FIELD_TIMESTMAP, bean.getTimestamp());
				values.put(FIELD_ID, bean.getId());
				values.put(FIELD_TITLE, bean.getTitle());
				values.put(FIELD_SOURCE, bean.getSource());
				values.put(FIELD_CATEGORY_NAME, bean.getCategory_name());
				values.put(FIELD_PLAY_TIME, bean.getPlay_time());
				values.put(FIELD_GVID, bean.getGvid());
				values.put(FIELD_IMAGE, bean.getImage());
				values.put(FIELD_CATEGORY_ID, bean.getCategory_id());
				values.put(FIELD_CONTENT_TYPE, bean.getContent_type());
				values.put(FIELD_DURATIONTIME, bean.getDurationTime());
				Log.e(TAG, "playtime" + bean.getPlay_time());
				Log.e(TAG, "getDurationTime" + bean.getDurationTime());
				db.insert(HistoryRecord.tablename, null, values);
				safelyCloseDataBase();
			}
		});
	}
	public ArrayList<HistoryRecord> getAll() {
		ArrayList<HistoryRecord> list = new ArrayList<HistoryRecord>();
		SQLiteDatabase db = safelyGetDataBase();
		Cursor cursor = db.rawQuery("select * from "+HistoryRecord.tablename+" order by "+FIELD_TIMESTMAP+" DESC", null);
		while (cursor.moveToNext()) {
			HistoryRecord historyRecord = new HistoryRecord();
			historyRecord.setId(cursor.getString(0));
			historyRecord.setTimestamp(cursor.getString(1));
			historyRecord.setTitle(cursor.getString(2));
			String ss = cursor.getString(2);
			historyRecord.setSource(cursor.getString(3));
			historyRecord.setCategory_name(cursor.getString(4));
			historyRecord.setPlay_time(cursor.getString(5));
			historyRecord.setGvid(cursor.getString(6));
			historyRecord.setImage(cursor.getString(7));
			historyRecord.setCategory_id(cursor.getString(8));
			historyRecord.setContent_type(cursor.getString(9));
			historyRecord.setDurationTime(cursor.getInt(10));
//			historyRecord.setContent_type(cursor.getString(FIELD_CATEGORY_ID));
//			list.add(cursor.getString(1));
			list.add(historyRecord);
		}
		safelyCloseDataBase();
		cursor.close();
		return list;
	}
	/**
	 *  专辑
	 * */
	public boolean isExistsAid(String aid){
		SQLiteDatabase db = safelyGetDataBase();
		Cursor  cursor= db.rawQuery("select " + FIELD_ID + " from " + HistoryRecord.tablename + " where " + FIELD_ID + " = '" + aid + "'", null);
		if(cursor.getCount()==0){
			cursor.close();
			safelyCloseDataBase();
			return false;
		}
		else {
			cursor.close();
			safelyCloseDataBase();
			return true;
		}
	}
	public boolean isExistsGvid(String gvid){
		SQLiteDatabase db = safelyGetDataBase();
		Cursor  cursor= db.rawQuery("select " + FIELD_GVID + " from " + HistoryRecord.tablename + " where " + FIELD_GVID + " = '" + gvid + "'", null);
		if(cursor.getCount()==0){
			cursor.close();
			safelyCloseDataBase();
			return false;
		}
		else {
			cursor.close();
			safelyCloseDataBase();
			return true;
		}
	}

	public void update(final HistoryRecord bean){

		doInBackground(new Runnable() {

			@Override
			public void run() {

				SQLiteDatabase db = safelyGetDataBase();
				ContentValues values = new ContentValues();
				values.put(FIELD_TIMESTMAP, bean.getTimestamp());

				values.put(FIELD_TITLE, bean.getTitle());
				values.put(FIELD_SOURCE, bean.getSource());
				values.put(FIELD_CATEGORY_NAME, bean.getCategory_name());
				values.put(FIELD_PLAY_TIME, bean.getPlay_time());
				values.put(FIELD_GVID, bean.getGvid());
				values.put(FIELD_IMAGE, bean.getImage());
				values.put(FIELD_CATEGORY_ID, bean.getCategory_id());
				values.put(FIELD_CONTENT_TYPE, bean.getContent_type());
				values.put(FIELD_DURATIONTIME, bean.getDurationTime());
//				values.put(FIELD_TOTALEPISODE, now.getTotalepisode());
//				values.put(FIELD_ISEND, now.getIsend());
//				values.put(FIELD_LATESTEPISODE, now.getLatestepisode());
//				values.put(FIELD_HISTORY, now.getHistory());
//				values.put(FIELD_ISEND, now.getIsend());
//				values.put(FIELD_TOTALTIME, now.getTotaltime());
//				values.put(FIELD_TOTALSPECIAL, now.getTotalspecail());
//				values.put(FIELD_TID, now.getTid());
				String[] where = {	bean.getId()};
				db.update(HistoryRecord.tablename, values, FIELD_ID+"=?", where);
				Log.e(TAG, "playtime" + bean.getPlay_time());
				Log.e(TAG, "getDurationTime" + bean.getDurationTime());
				safelyCloseDataBase();
			}

		});

	}
	public void updatebygvid(final HistoryRecord bean) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				ContentValues values = new ContentValues();
				values.put(FIELD_ID, bean.getId());
				values.put(FIELD_TIMESTMAP, bean.getTimestamp());
				values.put(FIELD_TITLE, bean.getTitle());
				values.put(FIELD_SOURCE, bean.getSource());
				values.put(FIELD_CATEGORY_NAME, bean.getCategory_name());
				values.put(FIELD_PLAY_TIME, bean.getPlay_time());
				values.put(FIELD_IMAGE, bean.getImage());
				values.put(FIELD_CATEGORY_ID, bean.getCategory_id());
				values.put(FIELD_CONTENT_TYPE, bean.getContent_type());
				values.put(FIELD_DURATIONTIME, bean.getDurationTime());
				String[] where = {	bean.getGvid()};
				db.update(HistoryRecord.tablename, values, FIELD_GVID+"=?", where);
				Log.e(TAG, "playtime" + bean.getPlay_time());
				Log.e(TAG, "getDurationTime" + bean.getDurationTime());
				safelyCloseDataBase();
			}
		});

	}
}
