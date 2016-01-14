package com.chaojishipin.sarrs.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chaojishipin.sarrs.bean.AlbumUpdateInfoBean;
import com.chaojishipin.sarrs.bean.DateTag;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
/**
 *  收藏数据库实现类
 * */

public class FavoriteDao extends BaseDao<Favorite> {

	private static final String TABLE_NAME = "favorite";
	private static final String FIELD_ID="id";
	private static final String FIELD_TYPE="type";
	private static final String FIELD_CID="cid";
	private static final String FIELD_IMG="img";
	private static final String FIELD_AID="aid";
	private static final String FIELD_GVID="gvid";
	private static final String FIELD_TITLE="title";
	private static final String FIELD_TOTALEPISODE="totalepisode";
	private static final String FIELD_LATESTEPISODE="latestepisode";
	private static final String FIELD_HISTORY="history";
	private static final String FIELD_ISEND="isend";
	private static final String FIELD_TOTALTIME="totaltime";
	private static final String FIELD_TOTALSPECIAL="totalspecial";
	private static final String FIELD_TID="tid";
	private static final String FIELD_CREATTIME="createtime";
	private static final String FIELD_CREATEDATE="createdate";
	private static final int FAVORITE_MAXIMUM_SIZE=100;
	
	public FavoriteDao(Context context) {
		super(context);
	}
	/**
	 *  单视频删除
	 * */
	public void deleteByGvid(final String gvid) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL(String.format("delete from %s where %s='%s'", TABLE_NAME, FIELD_GVID, gvid));
				safelyCloseDataBase();
			}
		});
	}

	/**
	 *  单视频删除
	 * */
	public void deleteByTid(final String tid) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL(String.format("delete from %s where %s='%s'", TABLE_NAME, FIELD_TID, tid));
				safelyCloseDataBase();
			}
		});
	}

	/**
	 *
	 * 根据Id删除
	 *  */

	public void deleteById(final String id) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL(String.format("delete from %s where %s='%s'", TABLE_NAME, FIELD_ID, id));
				safelyCloseDataBase();
			}
		});
	}

	 /**
	  *
	  * 删除一行
	  *  */

	@Override
	public void delete(final String aid) {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL(String.format("delete from %s where %s='%s'", TABLE_NAME, FIELD_AID, aid));
				safelyCloseDataBase();
			}
		});
	}
    /**
	 *  删除多行
	 * */
     public void deletePatch(final List<Favorite> list){
	/*	 doInBackground(new Runnable() {
			 @Override
			 public void run() {*/
				 SQLiteDatabase db = safelyGetDataBase();
				 db.beginTransaction();
					 for (int i = 0; i < list.size(); i++) {
						 if (list.get(i).isCheck()) {
							 db.execSQL(String.format("delete from %s where %s='%s'", TABLE_NAME, FIELD_ID,list.get(i).getId()));

							  LogUtil.e("xll", "remove sql  " + i +" id="+list.get(i).getId());
						  }
					 }

				 db.setTransactionSuccessful();
				 db.endTransaction();

				 safelyCloseDataBase();
		/*	 }
		 });*/

	 }



 	@Override
	public void delAll() {
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				db.execSQL(String.format("delete * from %s ", TABLE_NAME, FIELD_AID));
				safelyCloseDataBase();
			}
		});
	}
	

	public List<Favorite> getAll(final  int pageIndex,final  int pageSize) {
		final List<Favorite> list = new ArrayList<Favorite>();
		doInBackground(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " order by id DESC limit " + String.valueOf(pageIndex) + "," + String.valueOf(pageSize), null);
				while (cursor.moveToNext()) {
					Favorite favorite = new Favorite();
					favorite.setType(cursor.getString(1));
					favorite.setCid(cursor.getString(2));
					favorite.setImg(cursor.getString(3));
					favorite.setAid(cursor.getString(4));
					favorite.setGvid(cursor.getString(5));
					favorite.setType(cursor.getString(6));
					favorite.setTotalepisode(cursor.getString(7));
					favorite.setLatestepisode(cursor.getString(8));
					favorite.setHistory(cursor.getString(9));
					favorite.setIsend(cursor.getInt(10));
					favorite.setTotaltime(cursor.getString(11));
					favorite.setTotalspecail(cursor.getString(12));
					favorite.setTid(cursor.getString(13));
					favorite.setCreateTime(cursor.getString(14));
					favorite.setCreateDate(cursor.getString(15));
					list.add(favorite);
				}
				safelyCloseDataBase();
				cursor.close();
			}
		});

		return list;
	}

	public List<Favorite> getAll() {
		final List<Favorite> list = new ArrayList<Favorite>();
	/*	doInBackground(new Runnable() {
			@Override
			public void run() {*/
				SQLiteDatabase db = safelyGetDataBase();
				Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " order by id DESC ", null);
				while (cursor.moveToNext()) {
					Favorite favorite = new Favorite();
					favorite.setId(cursor.getInt(0));
					favorite.setType(cursor.getString(1));
					favorite.setCid(cursor.getString(2));
					favorite.setImg(cursor.getString(3));
					favorite.setAid(cursor.getString(4));
					favorite.setGvid(cursor.getString(5));
					favorite.setTitle(cursor.getString(6));
					favorite.setTotalepisode(cursor.getString(7));
					favorite.setLatestepisode(cursor.getString(8));
					favorite.setHistory(cursor.getString(9));
					favorite.setIsend(cursor.getInt(10));
					favorite.setTotaltime(cursor.getString(11));
					favorite.setTotalspecail(cursor.getString(12));
					favorite.setTid(cursor.getString(13));
					favorite.setCreateTime(cursor.getString(14));
					favorite.setCreateDate(cursor.getString(15));
					list.add(favorite);
				}
				safelyCloseDataBase();
				cursor.close();
		/*	}
		});*/

		return list;
	}
    /**
	 *  查询收藏日期列表
	 * */
	public List<DateTag> getDistinctDate() {
		final List<DateTag> list = new ArrayList<DateTag>();
	/*	doInBackground(new Runnable() {
			@Override
			public void run() {*/
		SQLiteDatabase db = safelyGetDataBase();
		Cursor cursor = db.rawQuery("select distinct createdate from " + TABLE_NAME + " order by id DESC ", null);
		while (cursor.moveToNext()) {
			DateTag tag = new DateTag();
			tag.dateTag=cursor.getString(0);
			LogUtil.e("xll","db tag "+tag.dateTag);
			list.add(tag);
		}
		safelyCloseDataBase();
		cursor.close();
		/*	}
		});*/

		return list;
	}
	/**
	 *  查询收藏日期列表
	 * */
	public List<String> getListDate() {
		final List<String> list = new ArrayList<String>();
	/*	doInBackground(new Runnable() {
			@Override
			public void run() {*/
		SQLiteDatabase db = safelyGetDataBase();
		Cursor cursor = db.rawQuery("select  createdate from " + TABLE_NAME + " order by id DESC ", null);
		while (cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		safelyCloseDataBase();
		cursor.close();
		/*	}
		});*/

		return list;
	}

	public Favorite getByAid(String aid) {
		Favorite favorite = null;
		SQLiteDatabase db = safelyGetDataBase();  
		Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where aid = '"+aid+"'", null);
		while (cursor.moveToNext()) {  
            favorite = new Favorite();
			favorite.setType(cursor.getString(1));
			favorite.setCid(cursor.getString(2));
			favorite.setImg(cursor.getString(3));
			favorite.setAid(cursor.getString(4));
			favorite.setGvid(cursor.getString(5));
			favorite.setType(cursor.getString(6));
			favorite.setTotalepisode(cursor.getString(7));
			favorite.setLatestepisode(cursor.getString(8));
			favorite.setHistory(cursor.getString(9));
			favorite.setIsend(cursor.getInt(10));
			favorite.setTotaltime(cursor.getString(11));
			favorite.setTotalspecail(cursor.getString(12));
			favorite.setTid(cursor.getString(13));
			favorite.setCreateTime(cursor.getString(14));
			favorite.setCreateDate(cursor.getString(15));

        }
		safelyCloseDataBase();
		cursor.close();
		return favorite;
	}
	
		
	public void update(final Favorite now){
		
		doInBackground(new Runnable() {

			@Override
			public void run() {

				SQLiteDatabase db = safelyGetDataBase();
				ContentValues values = new ContentValues();
				values.put(FIELD_TOTALEPISODE, now.getTotalepisode());
				values.put(FIELD_ISEND, now.getIsend());
				values.put(FIELD_LATESTEPISODE, now.getLatestepisode());
				values.put(FIELD_HISTORY, now.getHistory());
				values.put(FIELD_ISEND, now.getIsend());
				values.put(FIELD_TOTALTIME, now.getTotaltime());
				values.put(FIELD_TOTALSPECIAL, now.getTotalspecail());
				values.put(FIELD_TID, now.getTid());
				String[] where = {now.getAid()};
				db.update(TABLE_NAME, values, "aid=?", where);
				safelyCloseDataBase();
			}

		});
	}
	
    public void update(final AlbumUpdateInfoBean now) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = safelyGetDataBase();
                ContentValues values = new ContentValues();
                values.put(FIELD_LATESTEPISODE, now.getNowEpisode());
                values.put(FIELD_ISEND, now.getIsend());
                String[] where = {now.getAid()};
                db.update(TABLE_NAME, values, "aid=?", where);
                safelyCloseDataBase();
            }
        });
    }
	public void saveBatch(final List<Favorite> list){
		doInBackground(new Runnable() {

			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();

				db.beginTransaction();
				for (int i = 0; i < list.size(); i++) {
					Favorite f=list.get(i);
						ContentValues values = new ContentValues();
						values.put(FIELD_TYPE,f.getType());
						values.put(FIELD_CID, f.getCid());
						values.put(FIELD_IMG, f.getImg());
						values.put(FIELD_AID, f.getAid());
						values.put(FIELD_GVID,f.getGvid());
						values.put(FIELD_TITLE,f.getTitle());
						values.put(FIELD_TOTALEPISODE,f.getTotalepisode());
						values.put(FIELD_LATESTEPISODE,f.getLatestepisode());
						values.put(FIELD_TID, f.getTid());
						values.put(FIELD_CREATTIME,f.getCreateTime());
					    values.put(FIELD_CREATEDATE,f.getCreateDate());
						db.insert(TABLE_NAME, null, values);

				}

				db.setTransactionSuccessful();
				db.endTransaction();
				safelyCloseDataBase();
			}
		});

	}
	@Override
	public void save(final Favorite f) {
		doInBackground(new Runnable() {
			
			@Override
			public void run() {
				SQLiteDatabase db = safelyGetDataBase();
				ContentValues values = new ContentValues();
				values.put(FIELD_TYPE,f.getType());
				values.put(FIELD_CID, f.getCid());
				values.put(FIELD_IMG, f.getImg());
				values.put(FIELD_AID, f.getAid());
				values.put(FIELD_GVID,f.getGvid());
				values.put(FIELD_TITLE,f.getTitle());
				values.put(FIELD_TOTALEPISODE,f.getTotalepisode());
				values.put(FIELD_LATESTEPISODE, f.getLatestepisode());
				values.put(FIELD_TID, f.getTid());
				values.put(FIELD_CREATTIME,f.getCreateTime());
				values.put(FIELD_CREATEDATE,f.getCreateDate());
				db.insert(TABLE_NAME, null, values);
				safelyCloseDataBase();
			}
		});
	}
	/**
	 *  专辑
	 * */
	public boolean isExistsAid(String aid){
		SQLiteDatabase db = safelyGetDataBase();
		Cursor  cursor= db.rawQuery("select "+FIELD_AID+" from "+TABLE_NAME+" where "+FIELD_AID+" = '"+aid+"'", null);
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

   /**
	*  单视频
	* */
	public boolean isExistsGvid(String gvid){
		SQLiteDatabase db = safelyGetDataBase();
		Cursor  cursor= db.rawQuery("select "+FIELD_GVID+" from "+TABLE_NAME+" where "+FIELD_GVID+" = '"+gvid+"'", null);
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

	/**
	 *  专题
	 * */
	public boolean isExistsTid(String tid){
		SQLiteDatabase db = safelyGetDataBase();
		Cursor  cursor= db.rawQuery("select "+FIELD_TID+" from "+TABLE_NAME+" where "+FIELD_TID+" = '"+tid+"'", null);
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

}
