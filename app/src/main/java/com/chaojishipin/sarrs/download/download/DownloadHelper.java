package com.chaojishipin.sarrs.download.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;


import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.config.SettingManage;
import com.chaojishipin.sarrs.download.util.DownloadFileUtil;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.SPUtil;
import com.chaojishipin.sarrs.utils.StoragePathsManager;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class DownloadHelper {
	
	private static final String TAG = "DownloadHelper";
	public static boolean RESTORE_FLAG = false;
	public static int DOWNLOAD_FILEPATH_NUMBER = 0;
	public final static String REALLY_DOWNLOADPATH_FOR_SP = "really_download_path";
	public final static String ALL_EXTSDCARD_PATH = "all_sdcard_path";
	public static String REALLY_DOWNLOAD_FILEPATH = Utils.SAVE_FILE_PATH_DIRECTORY;
	public static String NEW_ADDED_SDCARDPATH = "";
	public final static String SDCARD_COUNT = "sdcard_count";
	
	/**
	 * 获取默认的下载地址（不涉及SD卡选择）
	 * @return
	 */
//	public static String getDownloadPath(){
//		return Utils.SAVE_FILE_PATH_DIRECTORY ;
//	}
	
	/**
	 * 获取默认的下载地址，包括SD卡选择功能
	 * 
	 */
	public static String getDownloadPath(){
     return SPUtil.getInstance().getString("sdcard","");
	}
	
	/**
	 * 获取下载的文件的路径
	 * @param entity
	 * @param destination
	 * @return
	 */
	public static String getAbsolutePath(DownloadEntity entity, String destination){
		if(destination == null){
			destination = getDownloadPath();
		}
		String displayName = getSaveName(entity);
		if(DownloadInfo.MP4.equals(entity.getDownloadType())){
			return destination+"/" + displayName +".mp4";
		}else if(DownloadInfo.M3U8.equals(entity.getDownloadType())){
			return destination+"/" + displayName;
		}
		return destination+"/" + displayName +".mp4";
	}
	
	/**
	 * 返回下载文件的大小
	 * @return
	 */
	public static long getDownloadedFileSize(DownloadEntity entity,String destination){
		String path = DownloadHelper.getAbsolutePath(entity,destination);
		File file = new File(path);
		return getDirectorySize(file);
	}
	
	public static long getDirectorySize(File file){
		if(file.isFile()){
			return file.length();
		}
		if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            long fileSize = 0;;
            if (childFiles == null || childFiles.length == 0) {
             return 0;
             }
            for (int i = 0; i < childFiles.length; i++) {
            	fileSize += childFiles[i].length();
             }
           return fileSize;
       }
		return 0;
	}
	
	//有多个sd卡时，从各个sd卡中找出已下载完成的文件
	public static File getDownloadedFile(DownloadEntity entity) {
		String path = null;
		File file = null;
		try {
			ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				path = DownloadHelper.getAbsolutePath(entity,Utils.SAVE_FILE_PATH_DIRECTORY);
				createFilePath(Utils.SAVE_FILE_PATH_DIRECTORY,entity);
				file = new File(path);
				if(file.exists() && file.length() > 0) {
					return file;
				}
			}
			if(pathList != null && pathList.size() > 0) {
				for(int i = 0; i < pathList.size(); i++) {
					path = DownloadHelper.getAbsolutePath(entity,pathList.get(i) + "/"+Utils.getDownLoadFolder());
					createFilePath(pathList.get(i) + "/"+Utils.getDownLoadFolder(),entity);
					file = new File(path);
					if(file.exists() && file.length() > 0) {
						return file;
					}
				}
			}
			path = DownloadHelper.getAbsolutePath(entity,DownloadHelper.getDownloadPath());
			createFilePath(DownloadHelper.getDownloadPath(),entity);
			file = new File(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private static void createFilePath(String path,DownloadEntity entity) {
		entity.setPath(path);
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
	}
	
	private static String getSaveName(DownloadEntity entity){
//        P2pHelper p2pHelper =P2pHelper.getInstance();
//        if(p2pHelper.isP2pDownLoad(entity)||entity.getDownloadType().equals(DownloadInfo.P2P)){
//        	return p2pHelper.constructP2pName(entity);
//        }else{
        	return entity.getSaveName();
//        }
	}
	public static String constructName(DownloadEntity entity){
		
		String mediaName = entity.getMedianame();
//        String taskName = entity.getTaskname();
//        if(mediaName != null && taskName != null && mediaName.equals(taskName)) {
//			taskName = ""; 
//		} 
        

//        return StringFilter(mediaName + taskName);
        return StringFilter(mediaName);
	}
	
	//过滤特殊字符
	public static String StringFilter(String str) throws PatternSyntaxException {
		String regEx="[`~!@#$%^&*()+=|{}':;',//[//]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	//获取外置sd卡路径集合
	@SuppressLint("NewApi")
	public static ArrayList<String> getExternalSdPath(Context context) {
	    StorageManager mStorageManager = null;
	    Method mMethodGetPaths = null;
	    String[] paths = null;
	    ArrayList<String> extSdPaths = new ArrayList<String>();
	    try { 
	    	mStorageManager = (StorageManager)context.getSystemService(Activity.STORAGE_SERVICE);
	    	
	    	mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths"); 
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            	for(int i = 0; i < paths.length; i++) {
            		if(paths[i].toUpperCase().contains("sd".toUpperCase()) 
            				&& !paths[i].toUpperCase().contains("usb".toUpperCase())
            				&& !paths[i].equalsIgnoreCase(Environment.getExternalStorageDirectory().getPath())) {
            			if(getSdTotalSize(paths[i]) > 0) {
            				extSdPaths.add(paths[i]);
							LogUtil.e("xll_storage","down load final "+paths[i]);
            			}
            		}
            	}
            } else {
            	for(int i = 0; i < paths.length; i++) {
            		if(paths[i].toUpperCase().contains("sd".toUpperCase())
            				&& !paths[i].toUpperCase().contains("usb".toUpperCase())) {
//            			extSdPaths.add(paths[i]);
            			if(getSdTotalSize(paths[i]) > 0) {
            				extSdPaths.add(paths[i]);
            			}
            		}
            	}
            }
        } catch (NoClassDefFoundError noClassException) {
        	noClassException.printStackTrace();  
        } catch (Exception e) {
            e.printStackTrace();
            return extSdPaths;
        }
		return extSdPaths;
	}
	
	private static double getSdTotalSize(String sdPath) {
		double totalSize = 0;
		StatFs sf = new StatFs(sdPath);
		long blockSize = sf.getBlockSize();
		long totalBlocks = sf.getBlockCount();
		totalSize = totalBlocks*blockSize/1024/1024;
		return totalSize;
	}
	
//	public static String changeHashidToNumStr(String hashId){
//		String numStr = hashId;
//		numStr = numStr.replace("a", "1");
//		numStr = numStr.replace("b", "2");
//		numStr = numStr.replace("c", "3");
//		numStr = numStr.replace("d", "4");
//		numStr = numStr.replace("e", "5");
//		numStr = numStr.replace("f", "6");
//		return numStr;
//	}
//	
	//获取sd卡剩余空间大小，单位为M
	public static double getSdcardStorage(String sdPath) {
		double availStorage = 0;
		if(!isSdcardExist(sdPath)) {
			File file = new File(sdPath);
			file.mkdirs();
		}
		StatFs sf = new StatFs(sdPath);
		long blockSize = sf.getBlockSize();
		long availCount = sf.getAvailableBlocks();
		availStorage = availCount*blockSize/1024/1024;
        return availStorage;
	}
	
	//获取sd卡已使用的空间大小
	public static double getSdUsedStorage(String sdPath) {
		double usedStorage = 0;
		if(isSdcardExist(sdPath)) {
			StatFs sf = new StatFs(sdPath);
			long blockSize = sf.getBlockSize(); 
			long totalBlocks = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			usedStorage = (totalBlocks - availCount)*blockSize/1024/1024;
		}
        return usedStorage;
	}
	
	//某路径的sd卡是否存在
	public static boolean isSdcardExist(String path) {
		boolean isSdcardExist = false;
		ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
		if(!TextUtils.isEmpty(path) && path.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
			isSdcardExist = true;
		} else if(pathList != null && pathList.size() > 0) {
			for(int i = 0; i < pathList.size(); i++) {
				if(pathList.get(i).equals(path)) {
					isSdcardExist = true;
					break;
				}
			}
		}
		return isSdcardExist;
	}
//	public static String convertClarity(String clarity){
//		if(ResolutionChoiceJob.TV.equals(clarity)){
//			return "流畅";
//		}else if(ResolutionChoiceJob.DVD.equals(clarity)){
//			return "标清";
//		}else if(ResolutionChoiceJob.HIGHDVD2.equals(clarity)){
//			return "高清";
//		}else if(ResolutionChoiceJob.SUPERDVD2.equals(clarity)){
//			return "超清";
//		}else{
//			return "";
//		}
//	}
	//是否有多个存储设备可供选择
	public static boolean isSdcardOptional() {
		boolean extSdFlag = false;
		ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
		String extPath = null;
		if(pathList != null && pathList.size() > 0) {
			for(int i = 0; i < pathList.size(); i++) {
				if(getSdTotalSize(pathList.get(i)) > 0) {
					extPath = pathList.get(i);
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
							&& getSdTotalSize(Environment.getExternalStorageDirectory().getPath()) > 0) {
						String innerPath = Environment.getExternalStorageDirectory().getPath();
						if(extPath.contains(innerPath) && getSdTotalSize(extPath) == getSdTotalSize(innerPath)) {
							extSdFlag = false;
						} else {
							extSdFlag = true;
							break;
						}
					}
				}
			}
		}
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				&& getSdTotalSize(Environment.getExternalStorageDirectory().getPath()) > 0) {
			if(extSdFlag) {
				return true;
			} else {
				return false;
			}
		} else if(pathList != null) {
			if(pathList.size() > 1) {
				int sdcardCount = 0;
				for(int i = 0; i < pathList.size(); i++) {
					if(getSdTotalSize(pathList.get(i)) > 0) {
						sdcardCount++;
						if(sdcardCount >= 2) {
							return true;
						}
					}
				}
			} else {
				return false;
			}
		}
		return false;
	}
	
	private static String getReallyDownloadPath() {
		SharedPreferences sharePreference = ChaoJiShiPinApplication.getInstatnce()
				.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
		String path = sharePreference.getString(REALLY_DOWNLOADPATH_FOR_SP, "");
		return path;
	}
	
	public static SpannableStringBuilder[] getShowingItems() {
		ContainSizeManager mSizeManager =ContainSizeManager.getInstance();
		SpannableStringBuilder[] paths = null;
		ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
		if(isSdcardOptional()) {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				if(pathList.size() == 1) {
					paths = new SpannableStringBuilder[2];
					
					paths[0] = getSpannableStringBuilder(mSizeManager.getFreeSizeForSetting(Utils.SAVE_FILE_PATH_DIRECTORY),ChaoJiShiPinApplication.getInstatnce().getString(R.string.download_phone_path)+"\n");
					paths[1] = getSpannableStringBuilder(mSizeManager.getFreeSizeForSetting(pathList.get(0)),ChaoJiShiPinApplication.getInstatnce().getString(R.string.download_sd_path)+"\n");
//					paths[1] = pathList.get(0);   
				} else if(pathList.size() > 1) {
					paths = new SpannableStringBuilder[pathList.size() + 1];
					paths[0] = new SpannableStringBuilder(ChaoJiShiPinApplication.getInstatnce().getString(R.string.download_phone_path));
					for(int i = 0,j = 1; i < pathList.size(); i++,j++) {
						paths[j] = new SpannableStringBuilder(pathList.get(i).replace("/mnt/", ""));
					}
				}
			} else {
				paths = new SpannableStringBuilder[pathList.size()];
				for(int i = 0; i < pathList.size(); i++) {
					paths[i] = new SpannableStringBuilder(pathList.get(i).replace("/mnt/", ""));
				}
			}
		}else{
			paths = new SpannableStringBuilder[1];
			paths[0] = getSpannableStringBuilder(mSizeManager.getFreeSizeForSetting(Utils.SAVE_FILE_PATH_DIRECTORY),ChaoJiShiPinApplication.getInstatnce().getString(R.string.download_phone_path)+"\n");
		}
		return paths;
	}
	/**
	 * 拼接SpannableStringBuilder
	 * 使用SpannableStringBuilder给文字添加大小和颜色
	 * @author daipei
	 * @since 2014年8月22日 10:53:23
	 */
	private static SpannableStringBuilder getSpannableStringBuilder(String path,String stringResource){
		SpannableString ss = new SpannableString(path);
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(ChaoJiShiPinApplication.getInstatnce().getResources().getColor(R.color.color_777777));
		AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(ChaoJiShiPinApplication.getInstatnce().getResources().getDimensionPixelSize(R.dimen.font_16));
		ss.setSpan(colorSpan, 0, path.length(), 0);
		ss.setSpan(sizeSpan, 0, path.length(), 0);
		SpannableStringBuilder ssb = new SpannableStringBuilder(stringResource);
		ssb.append(ss);
		return ssb;
	}
	
	//恢复用户选择的下载路径数据
	public static void restoreUserDownloadPath() {
		RESTORE_FLAG = true;
		SharedPreferences sharePreference = ChaoJiShiPinApplication.getInstatnce()
				.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
		String downloadPathItem = sharePreference.getString(DownloadFileUtil.DOWNLOAD_PATH, "-1");
		if(!"-1".equals(downloadPathItem)) {
			if(isSdcardOptional()) {
				int pathItem = Integer.parseInt(downloadPathItem);
				ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
				if(isSdcardOptional()) {
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						if(pathItem == 0) {
							REALLY_DOWNLOAD_FILEPATH = Utils.SAVE_FILE_PATH_DIRECTORY;
						} else if(pathItem > 0 && pathList.size() >= pathItem) {
							REALLY_DOWNLOAD_FILEPATH = pathList.get(pathItem-1) + "/"+Utils.getDownLoadFolder();
						} else {
							//选择一个优先级最高的sd卡作为路径
							REALLY_DOWNLOAD_FILEPATH = getDefaultDownloadPath();
						}
					} else {
						if(pathItem >= 0 && pathList.size() > pathItem) {
							REALLY_DOWNLOAD_FILEPATH = pathList.get(pathItem) + "/"+Utils.getDownLoadFolder();
						} else {
							//选择一个优先级最高的sd卡作为路径
							REALLY_DOWNLOAD_FILEPATH = getDefaultDownloadPath();
						}
					}
				} else {
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						REALLY_DOWNLOAD_FILEPATH = Utils.SAVE_FILE_PATH_DIRECTORY;
					} else if(pathList != null && pathList.size() > 0) {
						REALLY_DOWNLOAD_FILEPATH = pathList.get(0) + "/"+Utils.getDownLoadFolder();
					} else {
						REALLY_DOWNLOAD_FILEPATH = Utils.SAVE_FILE_PATH_DIRECTORY;
					}
				}
				//如果恢复的下载地址与上次选择的不一致，那么就重新选择一个优先级最高的
				if(!REALLY_DOWNLOAD_FILEPATH.equals(getReallyDownloadPath()) && !StringUtil.isEmpty(getReallyDownloadPath())) {
					REALLY_DOWNLOAD_FILEPATH = getDefaultDownloadPath();
				}
			}
		} else {
			REALLY_DOWNLOAD_FILEPATH = getDefaultDownloadPath();
		}
	}
	
	public static void saveReallyDownloadPath(int pathOrder) {
		if(isSdcardOptional()) {
			ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
			SharedPreferences sharePreference = ChaoJiShiPinApplication.getInstatnce()
					.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
			Editor editor = sharePreference.edit();
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				if(pathOrder == 0) {
					editor.putString(REALLY_DOWNLOADPATH_FOR_SP, Utils.SAVE_FILE_PATH_DIRECTORY);
					editor.commit();
				} else if(pathOrder > 0) {
					editor.putString(REALLY_DOWNLOADPATH_FOR_SP, pathList.get(pathOrder-1) + "/"+Utils.getDownLoadFolder());
					editor.commit();
				}
			} else {
				editor.putString(REALLY_DOWNLOADPATH_FOR_SP, pathList.get(pathOrder) + "/"+Utils.getDownLoadFolder());
				editor.commit();
			}
		}
	}
	
	//只在第一次安装第一次启动app的时候初始化
	public static void initSdcardCount() {
		SharedPreferences sharePreference = ChaoJiShiPinApplication.getInstatnce()
				.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
		int count = sharePreference.getInt(SDCARD_COUNT, -1);
		if(-1 == count) {
			storageSdcardCount();
		}
	}
	
	//存储所有的外置sd卡的路径
	public static void storageAllExtSdcardPath() {
		ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
		if(pathList != null && pathList.size() > 0) {
			SharedPreferences sharePreference = ChaoJiShiPinApplication.getInstatnce()
					.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
			Editor editor = sharePreference.edit();
			String paths = pathList.toString().replace("[", "");
			paths = paths.replace("]", "");
//			LogUtils.i(TAG, "all extsd paths == " + paths);
			editor.putString(ALL_EXTSDCARD_PATH, paths);
			editor.commit();
		}
	}
	
//	//获取优先级最高的sd卡的路径，外置sd卡>内置，剩余空间大>剩余空间小
	public static String getDefaultDownloadPath() {
		SharedPreferences sharePreference = ChaoJiShiPinApplication.getInstatnce()
				.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
		Editor editor = sharePreference.edit();
		ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
		if(isSdcardOptional()) {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				if(pathList.size() == 1) {
					editor.putString(DownloadFileUtil.DOWNLOAD_PATH, String.valueOf(1));
					editor.commit();
					return pathList.get(0) + "/"+ Utils.getDownLoadFolder();
				}
			}
			double biggestSize = 0;
			int biggestSizePosition = 0;
			for(int i = 0; i < pathList.size(); i++) {
				double sdSize = getSdcardStorage(pathList.get(i));
				if(sdSize >= biggestSize) {
					biggestSize = sdSize;
					biggestSizePosition = i;
				}
				if(pathList.size() > i+1) {
					for(int j = i+1; j < pathList.size(); j++) {
						double sdSizeTemp = getSdcardStorage(pathList.get(j));
						if(sdSizeTemp >= biggestSize) {
							biggestSize = sdSizeTemp;
							biggestSizePosition = j;
						}
					}
				}
			}
			editor.putString(DownloadFileUtil.DOWNLOAD_PATH, String.valueOf(biggestSizePosition));
			editor.commit();
			DOWNLOAD_FILEPATH_NUMBER = biggestSizePosition;
			return pathList.get(biggestSizePosition) + "/"+Utils.getDownLoadFolder();
		} else {
			DOWNLOAD_FILEPATH_NUMBER = 0;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				editor.putString(DownloadFileUtil.DOWNLOAD_PATH, String.valueOf(0));
				editor.commit();
				return Utils.SAVE_FILE_PATH_DIRECTORY;
			} else if(pathList != null && pathList.size() > 0) {
				editor.putString(DownloadFileUtil.DOWNLOAD_PATH, String.valueOf(0));
				editor.commit();
				return pathList.get(0) + "/"+Utils.getDownLoadFolder();
			} else {
				return Utils.SAVE_FILE_PATH_DIRECTORY;
			}
		}
	}
	
	//存储sd卡的个数
	public static void storageSdcardCount() {
		NEW_ADDED_SDCARDPATH = "";
		SharedPreferences sharePreference = ChaoJiShiPinApplication.getInstatnce()
				.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
		Editor editor = sharePreference.edit();
		editor.putInt(SDCARD_COUNT, getSdcardCount());
		editor.commit();
	}
	
//	//判断手机中sd卡的个数是否变化了
//	public static boolean isSdcardAdded() {
//		String addedSdPath = NEW_ADDED_SDCARDPATH + "/kuaikan";
//		if(!TextUtils.isEmpty(NEW_ADDED_SDCARDPATH)) {
//			if(addedSdPath.equals(getDownloadPath())) {
//				return false;
//			} else {
//				if(getDownloadPath().equals(Utils.SAVE_FILE_PATH_DIRECTORY)) {
//					return true;
//				}
//				double newSdSize = getSdcardStorage(NEW_ADDED_SDCARDPATH);
//				double oldSdSize = getSdcardStorage(getDownloadPath().replace("/kuaikan", ""));
//				if(oldSdSize < newSdSize) {
//					return true;
//				} else {
//					return false;
//				}
//			}
//		}
//		return false;
//	}
//	
	//获取手机中sd卡个数
	private static int getSdcardCount() {
		int sdCount = 0;
		ArrayList<String> pathList = getExternalSdPath(ChaoJiShiPinApplication.getInstatnce());
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			sdCount = 1;
			if(pathList != null && pathList.size() > 0) {
				sdCount = pathList.size() + 1;
			}
		} else {
			if(pathList != null && pathList.size() > 0) {
				sdCount = pathList.size();
			}
		}
//		LogUtils.i(TAG, "sdCount == " + sdCount);
		return sdCount;
	}
//	
////	private static String getAllExtSdcardPath() {
////		SharedPreferences sharePreference = MoviesApplication.getInstance()
////				.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
////		String paths = sharePreference.getString(ALL_EXTSDCARD_PATH, "");
////		return paths;
////	}
//	
//	//获取新插入的sd卡名称
//	private static String getNewSdcardName() {
//		ArrayList<String> pathList = getExternalSdPath(MoviesApplication.getInstance());
//		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			if(pathList != null) {
//				if(pathList.size() == 1) {
//					return MoviesApplication.getInstance().getString(R.string.download_sd_path);
//				}
//			}
//		}
//		if(pathList.size() > 1) {
//			String latestPaths = pathList.toString().replace("[", "");
//			latestPaths = latestPaths.replace("]", "");
//			String oldPaths = getAllExtSdcardPath();
//			if(!TextUtils.isEmpty(oldPaths) && !TextUtils.isEmpty(latestPaths)) {
//				latestPaths = latestPaths.replace(oldPaths, "");
//				return latestPaths.replace("/mnt/", "");
//			}
//		}
//		return null;
//	}
//	
////	/*
////	 * 弹出发现新sd卡对话框
////	 */
////	public static void showNewSdDialog(Context context, final EpisodeFragment downloadFragment) {
////		Builder customBuilder = new Builder(context);
////		final String showString = getNewSdcardName();
////		if(!TextUtils.isEmpty(showString)) {
////			customBuilder
////			.setTitle("发现SD卡")
////			.setMessage("是否使用"+ showString +"作为风行默认下载路径")
////			.setPositiveButton(context.getString(R.string.ok),
////					new DialogInterface.OnClickListener() {
////				public void onClick(DialogInterface dialog,int which) {	
////					storageSdcardCount();
////					changeOldDownloadPathToNew(showString);
////					storageAllExtSdcardPath();
////					downloadFragment.confirmDownload();
////					dialog.dismiss();
////				}
////			})
////			.setNegativeButton(context.getString(R.string.cancel),
////					new DialogInterface.OnClickListener() {
////				public void onClick(DialogInterface dialog,
////						int which) {
////					storageSdcardCount();
////					storageAllExtSdcardPath();
////					dialog.dismiss();
////				}
////			})
////			.setOnKeyListener(new OnKeyListener() {
////				@Override
////				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
////					if(keyCode == KeyEvent.KEYCODE_BACK) {
////						storageSdcardCount();
////						storageAllExtSdcardPath();
////					}
////					return false;
////				}
////			});
////			
////			Dialog dialog = customBuilder.create();
////			dialog.show();
////		}
////	}
//		
////	/*
////	 * 将下载路径切换至新路径
////	 */
////	private static void changeOldDownloadPathToNew(String sdName) {
////		SharedPreferences sharePreference = MoviesApplication.getInstance()
////				.getSharedPreferences(SettingManage.SETTING_RELATIVE_SHAREPREFERENCE, Context.MODE_PRIVATE);
////		Editor editor = sharePreference.edit();
////		ArrayList<String> pathList = getExternalSdPath(MoviesApplication.getInstance());
////		int sdNumber = 0;
////		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
////			if(pathList != null) {
////				if(pathList.size() == 1) {
////					sdNumber = 1;
////				} else if(pathList.size() > 1) {
////					for(int i = 0; i < pathList.size(); i++) {
////						if(("/mnt/"+sdName).equals(pathList.get(i))) {
////							sdNumber = i+1;
////							break;
////						}
////					}
////				}
////			}
////		} else if(pathList.size() > 1) {
////			for(int i = 0; i < pathList.size(); i++) {
////				if(("/mnt/"+sdName).equals(pathList.get(i))) {
////					sdNumber = i;
////					break;
////				}
////			}
////		}
////		editor.putString(SetActivity.DOWNLOAD_PATH, String.valueOf(sdNumber));
////		editor.commit();
////		DownloadHelper.RESTORE_FLAG = false;
////		DownloadHelper.DOWNLOAD_FILEPATH_NUMBER = sdNumber;
////	}
	
}
