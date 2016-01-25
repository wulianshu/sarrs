package com.chaojishipin.sarrs.download.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.text.TextUtils;
import android.util.SparseArray;


import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

import java.util.ArrayList;


public class DownloadDaoImpl implements DownloadDao {
	private final static String TAG = "DownloadDaoImpl";

	private static final String TABLE_DOWNLOAD = "download";
	private static final int DB_VERSION = 9;
	private SQLiteDatabase mDb;
	
	public DownloadDaoImpl() {
		mDb = getDb();
		if (mDb == null)
			return;

		if (mDb.getVersion() < DB_VERSION) {
			new UpdaterBuilder().getUpdater(DB_VERSION).update();
		}
	}

	private SQLiteDatabase getDb() {
		try {
			return ChaoJiShiPinApplication.getInstatnce().openOrCreateDatabase(
					"download.db", Context.MODE_PRIVATE, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isDownloaded(String id){
		Cursor c = null;
		try{
			if(TextUtils.isEmpty(id))
				return false;
			c = mDb.query(TABLE_DOWNLOAD, new String[]{"hashId"}, "hashId=?", new String[]{id}, null, null, null);
			if(c != null && c.moveToFirst())
				return true;
		}catch(Throwable e){
			e.printStackTrace();
		}finally{
			closeCursor(c);
		}
		return false;
	}

	@Override
	public boolean add(DownloadEntity entry) {
		try {
			if (mDb == null) {
				return false;
			}
			
			ContentValues values = new ContentValues();
			values.putAll(new DownloadEntityDBBuilder().deconstruct(entry));

			String[] whereArgs = { "" + entry.getId() };
			// 更新
			long row_count = mDb.update(TABLE_DOWNLOAD, values, "hashId=?", whereArgs);

			// 插入
			if (row_count == 0) {
				mDb.insert(TABLE_DOWNLOAD, null, values);
			}

			return row_count != -1l;
		} catch (SQLiteDiskIOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public void setStatus(DownloadEntity entry, int status) {
		try {
			if (mDb == null) {
				return;
			}
			
			ContentValues values = new ContentValues();
			values.put("downloaded", status);

			String[] whereArgs = { "" + entry.getId() };
			int row_count = mDb.update(TABLE_DOWNLOAD, values, "hashId=?", whereArgs);

			if (row_count == 0) {
//				LogUtil.e(TAG, "Failed to update " + TABLE_DOWNLOAD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setIfWatch(DownloadEntity entry, String ifWatch) {
		try {
			if (mDb == null) {
				return;
			}
			
			ContentValues values = new ContentValues();
			values.put("ifWatch", ifWatch);

			String[] whereArgs = { "" + entry.getId() };
			int row_count = mDb.update(TABLE_DOWNLOAD, values, "hashId=?", whereArgs);

			if (row_count == 0) {
//				LogUtil.e(TAG, "Failed to update " + TABLE_DOWNLOAD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean selectDownloadJobByMid(String mid) {
		boolean result = false;
		if (mDb == null)
			return result;

		Cursor cursor = null;
		try {
			String[] whereArgs = { mid };
			
			cursor = mDb.query(TABLE_DOWNLOAD, null, "hashId=?", whereArgs, null, null,null);
			int count = cursor.getCount();
			result = count>0 ? true : false;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		
		return result;
	}
	
	@Override
	public ArrayList<DownloadJob> getAllDownloadJobs() {
		ArrayList<DownloadJob> jobs = new ArrayList<DownloadJob>();
		if (null == mDb )
			return jobs;

		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE_DOWNLOAD, null, null, null, null, null, null);
			
			if (cursor.moveToFirst()) {
				int i = 0;
				while (!cursor.isAfterLast()) {
					i++;
					jobs.add(new DownloadEntityDBBuilder().build(cursor, i));
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		
		return jobs;
	}
	
	@Override
	public void remove(DownloadJob job) {
		if (mDb == null) {
			return;
		}
		
		try{
			String[] whereArgs = { "" + job.getEntity().getId() };
			mDb.delete(TABLE_DOWNLOAD, "hashId=?", whereArgs);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	@Deprecated
	public SparseArray<DownloadFolderJob> getAllDownloadFloderJobs() {
		SparseArray<DownloadFolderJob> folderJobs = new SparseArray<DownloadFolderJob>();
		if (mDb == null)
			return folderJobs;
		
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE_DOWNLOAD, null, null, null, "media_mid", null, "index asc");
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {			
					DownloadJob job = new DownloadEntityDBBuilder().build(cursor, 0);

					DownloadFolderJob folderJob = folderJobs.get(folderJobs.size()-1);
					if(folderJob == null){
						folderJob = new DownloadFolderJob();
					}
					
					if(job.equals(folderJob.getMediaId())){
						SparseArray<DownloadJob> jobs = folderJob.getDownloadJobs();
						if(jobs == null||jobs.size()==0){
							jobs = new SparseArray<DownloadJob>();
						}
						jobs.put(job.getEntity().getIndex(), job);
					}else{
						folderJobs.put(folderJobs.size(),folderJob);
						folderJob = new DownloadFolderJob();
						folderJob.setMediaId(job.getEntity().getMid());
					}
					
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		
		return folderJobs;
	}

	@Override
	public boolean updateValue(DownloadEntity entity, String key,  String newValue) {
		try {
			if (mDb == null) {
				return false;
			}

			ContentValues values = new ContentValues();
			values.put(key, newValue);

			String[] whereArgs = { "" + entity.getId() };
			int row_count = mDb.update(TABLE_DOWNLOAD, values, "hashId=?", whereArgs);

			if (row_count == 0) {
//				LogUtil.e(TAG, "Failed to update " + TABLE_DOWNLOAD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateValue(DownloadEntity entity, String key, int newValue)
	{
		try {
			if (mDb == null) {
				return false;
			}

			ContentValues values = new ContentValues();
			values.put(key, newValue);

			String[] whereArgs = { "" + entity.getId() };
			int row_count = mDb.update(TABLE_DOWNLOAD, values, "hashId=?", whereArgs);

			if (row_count == 0) {
//				LogUtil.e(TAG, "Failed to update " + TABLE_DOWNLOAD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private class UpdaterBuilder {
		public DatabaseUpdater getUpdater(int version) {
			DatabaseUpdater updater = null;

			switch (version) {
			default:
				updater = new DatabaseUpdaterV3();
				break;
			case 0:
				updater = null;
				break;
			}
			
			return updater;
		}
	}
	
	private class DatabaseUpdaterV3 extends DatabaseUpdater {
		private static final int VERSION = 9;

		public DatabaseUpdaterV3() {
			
		}
		
		@Override
		public void update() {
			mDb.beginTransaction();
			
			if (tableExist(TABLE_DOWNLOAD)) {	
				switch(mDb.getVersion()){
				  case 5:
					  addTableColumn(mDb, TABLE_DOWNLOAD, "vt", "VARCHAR");
				  case 6:
					  addTableColumn(mDb, TABLE_DOWNLOAD, "mp4api", "VARCHAR");
					  addTableColumn(mDb, TABLE_DOWNLOAD, "m3u8api", "VARCHAR");
					  addTableColumn(mDb, TABLE_DOWNLOAD, "snifferUrl", "VARCHAR");
					  addTableColumn(mDb, TABLE_DOWNLOAD, "rule", "VARCHAR");
				  case 7:
					  addTableColumn(mDb, TABLE_DOWNLOAD, "cloudId", "VARCHAR");
				  case 8:
					  addTableColumn(mDb, TABLE_DOWNLOAD, "globaVid", "VARCHAR");
					  addTableColumn(mDb, TABLE_DOWNLOAD, "src", "VARCHAR");
					  addTableColumn(mDb, TABLE_DOWNLOAD, "m3u8Rule", "VARCHAR");
				  default :
					  break;
				}
				
//				addTableColumn(mDb, TABLE_DOWNLOAD, "folderName", "VARCHAR");
			} else {
				installTable();
			}
			mDb.setTransactionSuccessful();
			mDb.endTransaction();
			mDb.setVersion(VERSION);
		}
		
		private void addTableColumn(SQLiteDatabase db,String tableName,String tableColum,String columType){
			String sql = "alter table "+ tableName +" add column " + tableColum +" "+ columType+";";
			db.execSQL(sql);
		}
		
		private boolean tableExist(String tableName) {
			boolean result = false;
			
			if (TextUtils.isEmpty(tableName)) {
				return false;
			}
			
			Cursor cursor = null;
			try {
				String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
						+ tableName.trim() + "'";
				
				cursor = mDb.rawQuery(sql, null);
				if (cursor.moveToNext()) {
					int count = cursor.getInt(0);
					if (count > 0) {
						result = true;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
			}
			
			return result;
		}
		
		private boolean isTableExist(String tableName) {
			boolean result = false;  
			if (null == tableName || null == mDb) {  
		           return false;  
			}
 
			Cursor cursor = null;
			try {   
	                String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='"
		                    + tableName.trim() + "' ";
	               
		            cursor = mDb.rawQuery(sql, null);  
		            if (cursor.moveToNext()) {  
		                int count = cursor.getInt(0);  
		                if (count > 0) {  
		                    result = true;  
		                }  
		            }  
		  
		        } catch (Exception e) {
		        	e.printStackTrace();
		        } finally {
		        	closeCursor(cursor);
		        }
			
		        return result;  
		}  
		
		private void installTable () {
			try {
				if(isTableExist(TABLE_DOWNLOAD))
					mDb.execSQL("DROP TABLE " + TABLE_DOWNLOAD + ";");
			} catch (Exception e) {
//				LogUtils.e(TAG, "Library table not existing");
			} finally {
				createTables();
			}
		}
		
		private void createTables() {
			mDb.execSQL("CREATE TABLE IF NOT EXISTS "
					+ TABLE_DOWNLOAD
					+ " (hashId VARCHAR UNIQUE, downloaded INTEGER,display_name VARCHAR, media_name VARCHAR,"
					+ " file_length INTEGER, media_mid VARCHAR,download_url VARCHAR,request_downloadInfo_url VARCHAR,"
					+ " media_taskname VARCHAR,media_type VARCHAR,media_clarity VARCHAR,path VARCHAR,language VARCHAR,position INTEGER,"
					+ "download_type VARCHAR,site VARCHAR,porder VARCHAR,request_site VARCHAR,letvMid VARCHAR,ifWatch VARCHAR,"
					+ "addTime INTEGER,folderName VARCHAR,vt VARCHAR,mp4api VARCHAR,m3u8api VARCHAR,snifferUrl VARCHAR,"
					+ "rule VARCHAR,cloudId VARCHAR,globaVid VARCHAR,src VARCHAR,m3u8Rule VARCHAR,ext VARCHAR,image VARCHAR,cid VARCHAR);");
		}
	}
	
	@Override
	public ArrayList<DownloadJob> getDownloadJobsByMid(String mid) {
		ArrayList<DownloadJob> jobs = new ArrayList<DownloadJob>();
		if (mDb == null)
			return jobs;

		Cursor cursor = null;
		try {
			String[] whereArgs = { "" + mid };
			
			cursor = mDb.query(TABLE_DOWNLOAD, null, "media_mid=?", whereArgs, null, null,
					"position asc");
			
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					jobs.add(new DownloadEntityDBBuilder().build(cursor, 0));
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		
		return jobs;
	}
	
	private void closeCursor(Cursor c) {
		if (c != null && !c.isClosed()) {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
				c = null;
			}
		}
	}
	
	protected void finalize(){
		mDb.close();
	}
	
	//更新数据库,用到Decorator pattern
	abstract class DatabaseUpdater {
		private DatabaseUpdater mUpdater;

		abstract void update();

		public void setUpdater(DatabaseUpdater mUpdater) {
			this.mUpdater = mUpdater;
		}

		public DatabaseUpdater getUpdater() {
			return mUpdater;
		}
	}

}
