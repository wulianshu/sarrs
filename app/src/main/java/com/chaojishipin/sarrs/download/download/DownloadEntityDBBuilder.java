package com.chaojishipin.sarrs.download.download;


import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.DataUtils;
import com.chaojishipin.sarrs.utils.StringUtil;

import java.io.File;


public class DownloadEntityDBBuilder {	
	private final static String HASHID = "hashId";
	private final static String MEDIA_MID = "media_mid";
	private final static String DISPLAY_NAME = "display_name";
	private final static String MEDIA_NAME = "media_name";
	private final static String FILE_LENGTH = "file_length";
	private final static String DOWNLOAD_URL = "download_url";
	private final static String MEDIA_TASKNAME = "media_taskname";
	private static final String DOWNLOADED = "downloaded";
	private final static String REQ_DOWNLOAD_URL = "request_downloadInfo_url";
	private final static String MEDIA_CLARITY = "media_clarity";
	private static final String MEDIA_TYPE = "media_type";
	private static final String PATH = "path";
//	private static final String LANGUAGE = "language";
	private static final String INDEX = "position";
	public static final String DOWNLOAD_TYPE = "download_type";
	private static final String SITE = "site";
	private static final String PORDER = "porder";
//	private static final String REQUEST_STIE = "request_site";
//	private static final String LETV_MID = "letvMid";
	private static final String IFWATCH = "ifWatch";
	private static final String ADDTIME = "addTime";
	private static final String FOLDERNAME = "folderName";
	private static final String VT = "vt";
//	private static final String MP4API = "mp4api";
//	private static final String M3U8API = "m3u8api";
//	private static final String SNIFFERURL = "snifferUrl";
//	private static final String RULE = "rule";
//	private static final String CLOUDID = "cloudId";
	private static final String GLOBAVID = "globaVid";
	private static final String SRC = "src";
//	private static final String M3U8RULE = "m3u8Rule";
	private static final String IMAGE = "image";
	private static final String CID = "cid";

	public ContentValues deconstruct(DownloadEntity entity) {
		ContentValues values = new ContentValues();
		values.put(HASHID, entity.getId());
		values.put(MEDIA_MID, entity.getMid());
		values.put(DISPLAY_NAME, entity.getDisplayName());
		values.put(MEDIA_NAME, entity.getMedianame());
		values.put(FILE_LENGTH, entity.getFileSize());
		values.put(DOWNLOAD_URL, entity.getDownloadUrl());
		values.put(MEDIA_TASKNAME, entity.getTaskname());
//		values.put(REQ_DOWNLOAD_URL, entity.getUrl());
		values.put(MEDIA_TYPE, entity.getMediatype());
		values.put(MEDIA_CLARITY,entity.getCurrClarity());
		values.put(DOWNLOADED,entity.getStatus());
		values.put(PATH,entity.getPath());
//		values.put(LANGUAGE,entity.getLanguage());
		values.put(INDEX,entity.getIndex());
		values.put(DOWNLOAD_TYPE,entity.getDownloadType());
		values.put(SITE,entity.getSite());
		values.put(PORDER,entity.getPorder());
//		values.put(REQUEST_STIE,entity.getRequest_site());
//		values.put(LETV_MID,entity.getLetvMid());
		values.put(IFWATCH,entity.getIfWatch());
		values.put(ADDTIME,entity.getAddTime());
		values.put(FOLDERNAME,entity.getFolderName());
		values.put(VT,entity.getVt());
//		values.put(MP4API,entity.getMp4api());
//		values.put(M3U8API,entity.getM3u8api());
//		values.put(SNIFFERURL, entity.getSnifferUrl());
//		values.put(RULE, entity.getRule());
//		values.put(CLOUDID, entity.getCloudId());
		values.put(GLOBAVID, entity.getGlobaVid());
		values.put(SRC, entity.getSrc());
//		values.put(M3U8RULE, entity.getM3u8Rule());
		values.put(IMAGE, entity.getImage());
		values.put(CID, entity.getCid());
		return values;
	}

	public DownloadJob build(Cursor query, int i) {
		DownloadEntity dEntry = buildDownloadEntity(query);

		if(dEntry.getDownloadType()==null)
			dEntry.setDownloadType(DownloadInfo.MP4);
		if(dEntry.getPath()==null) {
			DownloadHelper.getDownloadedFile(dEntry);//查找下载文件的路径并给dEntity赋值
		}
		
		DownloadJob dJob = new DownloadJob(dEntry, dEntry.getPath());
		
		int progress = query.getInt(query.getColumnIndex(DOWNLOADED));
		if (progress == 1) {
			dJob.setProgress(100);
		}else{
			int p;
			if(dJob.getTotalSize() <= 0)
				p = 0;
			else {
				long size = DataUtils.getLocalFile(dJob).length();
				p = (int) (size * 100F / dJob.getTotalSize());
			}
			dJob.setProgress(p);
		}
		if(dEntry.getIndex()==0){
			int index = getIndex(dEntry);
			dEntry.setIndex(index);
			dJob.setIndex(i+500);
		}else{
			dJob.setIndex(dEntry.getIndex());
		}
			
		return dJob;
	}

	private DownloadEntity buildDownloadEntity(Cursor query) {
		DownloadEntity dEntity = null;
		
		try {
			int columnHashId = query.getColumnIndex(HASHID);
			int columnMid = query.getColumnIndex(MEDIA_MID);
			int columnDname = query.getColumnIndex(DISPLAY_NAME);
			int columnMname = query.getColumnIndex(MEDIA_NAME);
			int columnFilelength = query.getColumnIndex(FILE_LENGTH);
			int columnUrl = query.getColumnIndex(DOWNLOAD_URL);
			int columnTashname = query.getColumnIndex(MEDIA_TASKNAME);
			int columnResUrl = query.getColumnIndex(REQ_DOWNLOAD_URL);
			int columnCurrClarity = query.getColumnIndex(MEDIA_CLARITY);
			int coulumnMtype = query.getColumnIndex(MEDIA_TYPE);
			int coulumnStatus = query.getColumnIndex(DOWNLOADED);
			int coulumnIndex = query.getColumnIndex(INDEX);
			int coulumnPath = query.getColumnIndex(PATH);
//			int coulumnLangugae = query.getColumnIndex(LANGUAGE);
			int coulumnDownloadType = query.getColumnIndex(DOWNLOAD_TYPE);
			int coulumnSite = query.getColumnIndex(SITE);
			int coulumnPorder = query.getColumnIndex(PORDER);
//			int coulumnRequest_site = query.getColumnIndex(REQUEST_STIE);
//			int letv_mid = query.getColumnIndex(LETV_MID);
			int ifWatch = query.getColumnIndex(IFWATCH);
			int addTime =  query.getColumnIndex(ADDTIME);
			int folderName =  query.getColumnIndex(FOLDERNAME);
			int vt =  query.getColumnIndex(VT);
//			int mp4api =  query.getColumnIndex(MP4API);
//			int m3u8api =  query.getColumnIndex(M3U8API);
//			int snifferUrl = query.getColumnIndex(SNIFFERURL);
//			int rule = query.getColumnIndex(RULE);
//			int cloudId = query.getColumnIndex(CLOUDID);
			int globaVid = query.getColumnIndex(GLOBAVID);
			int src = query.getColumnIndex(SRC);
//			int m3u8Rule = query.getColumnIndex(M3U8RULE);
			int image = query.getColumnIndex(IMAGE);
			int cid = query.getColumnIndex(CID);
			dEntity = new DownloadEntity();
			
			dEntity.setId(query.getString(columnHashId));
			dEntity.setMid(query.getString(columnMid));
			dEntity.setDisplayName(delClarity(query.getString(columnDname)));
			dEntity.setMedianame(query.getString(columnMname));
			dEntity.setFileSize(query.getInt(columnFilelength));
			dEntity.setDownloadUrl(query.getString(columnUrl));
			dEntity.setTaskname(delClarityOfTaskName(query.getString(columnTashname)));
//			dEntity.setUrl(query.getString(columnResUrl));
			dEntity.setMediatype(query.getString(coulumnMtype));
			dEntity.setCurrClarity(query.getString(columnCurrClarity));
			dEntity.setStatus(query.getInt(coulumnStatus));
			dEntity.setIndex(query.getInt(coulumnIndex));
			dEntity.setPath(query.getString(coulumnPath));
//			dEntity.setLanguage(query.getString(coulumnLangugae));
			dEntity.setDownloadType(query.getString(coulumnDownloadType));
			dEntity.setSite(query.getString(coulumnSite));
			dEntity.setPorder(query.getString(coulumnPorder));
//			dEntity.setRequest_site(query.getString(coulumnRequest_site));
//			dEntity.setLetvMid(query.getString(letv_mid));
			dEntity.setIfWatch(query.getString(ifWatch));
			dEntity.setAddTime(query.getInt(addTime));
			dEntity.setFolderName(query.getString(folderName));
			dEntity.setVt(query.getString(vt));
//			dEntity.setMp4api(query.getString(mp4api));
//			dEntity.setM3u8api(query.getString(m3u8api));
//			dEntity.setSnifferUrl(query.getString(snifferUrl));
//			dEntity.setRule(query.getString(rule));
//			dEntity.setCloudId(query.getString(cloudId));
			dEntity.setGlobaVid(query.getString(globaVid));
			dEntity.setSrc(query.getString(src));
//			dEntity.setM3u8Rule(query.getString(m3u8Rule));
			dEntity.setImage(query.getString(image));
			dEntity.setCid(query.getString(cid));
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return dEntity;
	}
	
	private String delClarity(String name){
		if(name != null){
			name = name.replace("流畅", "").replace("标清", "").replace("高清", "").replace("超清", "");
		}
		
		return name;	
	}
	
	private String delClarityOfTaskName(String name){
		if(name != null){
			name = name.replace("(流畅)", "").replace("(标清)", "").replace("(高清)", "").replace("(超清)", "");
		}
		
		return name;	
	}
	
	public int getIndex(DownloadEntity entity) {
		try {
			String taskName = entity.getTaskname();
			if(!TextUtils.isEmpty(taskName)
					&& taskName.contains(ChaoJiShiPinApplication.getInstatnce().getString(R.string.di))
					&& taskName.contains(ChaoJiShiPinApplication.getInstatnce().getString(R.string.episode))) {
				int task = 0;
				if(!StringUtil.isEmpty(taskName)) {
					if(taskName.contains("~")) {
						task = Integer.parseInt(taskName.substring(0, taskName.indexOf("~")).replaceAll("[^0-9]", ""));
					} else {
						task = Integer.parseInt(taskName.replaceAll("[^0-9]", ""));
					}
				}
				return task;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}

}
