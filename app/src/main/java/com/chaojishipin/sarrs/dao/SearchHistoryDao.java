package com.chaojishipin.sarrs.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chaojishipin.sarrs.bean.SearchKeyWords;
import com.chaojishipin.sarrs.utils.ConstantUtils;


public class SearchHistoryDao extends BaseDao<SearchKeyWords> {
	

	private static final String TABLE_NAME = "search_record";
	private static final String FIELD_ID = "id";
	private static final String FIELD_NAME = "name";

	public SearchHistoryDao(Context context) {
		super(context);
	}

	@Override
	public void delete(String bean) {

	}

	public void delAll() {
		SQLiteDatabase db = safelyGetDataBase();
		db.execSQL("delete from " + TABLE_NAME);
		safelyCloseDataBase();
	}

	public SearchKeyWords getAll() {
		SearchKeyWords list = new SearchKeyWords();
		SQLiteDatabase db = safelyGetDataBase();
		Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" order by "+FIELD_ID+" DESC", null);
		while (cursor.moveToNext()) {
			list.addWords(cursor.getString(1));
        }  
		safelyCloseDataBase();
		cursor.close();
		return list;
	}

	public void save(final String bean) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL("delete from "+TABLE_NAME+" where "+FIELD_NAME+" = '"+bean+"'");
				Cursor cursor=db.rawQuery(String.format("Select * from %s;",TABLE_NAME), null);
				if (cursor.getCount()== ConstantUtils.SEARCH_RECORD_MAXIMUM_SIZE){
					db.execSQL("delete from "+TABLE_NAME+" where "+FIELD_ID+"=(select min("+FIELD_ID+") from "+TABLE_NAME+")");
				}
				ContentValues values = new ContentValues();
				values.put(FIELD_NAME, bean);
				db.insert(TABLE_NAME, null, values);
				safelyCloseDataBase();
				cursor.close();
			}
		});
	}


	@Override
	public void save(SearchKeyWords bean) {
		
	}

}
