package com.chaojishipin.sarrs.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

/**
 * @description Get the internal or external storage path.
 * 				This class used three ways to obtain the storage path.
 * 				reflect:
 * 						major method is getVolumePaths and getVolumeState. this two method is hidden for programmer, so we must to use this way.
 * 						if either getVolumePaths or getVolumeState can not be found (e.g. in some sdk version), then use next way.
 * 				command:
 * 						By filter the output of command "mount",  may be we can get the storage path that we want. if didn't, then use next way.
 * 				Api:
 * 						As is known to all, we use getExternalStorageDirectory method.
 */

//case



/*
-------------------------------device1--------------------
        01-08 16:43:40.236 30932-30932/com.chaojishipin.sarrs E/xll_storage: init
        01-08 16:43:40.243 30932-30932/com.chaojishipin.sarrs E/xll_storage: /storage/emulated/0
        01-08 16:43:40.243 30932-30932/com.chaojishipin.sarrs E/xll_storage: /storage/sdcard1
///shell///
        01-08 16:43:40.292 30932-30932/com.chaojishipin.sarrs E/xll_storage: abtain by command:
        /mnt/media_rw/sdcard1
        01-08 16:43:40.292 30932-30932/com.chaojishipin.sarrs E/xll_storage: abtain by Environment:
        /mnt/media_rw/sdcard1

        -------------------------------device2--------------------
        01-08 16:51:28.606 26380-26380/com.chaojishipin.sarrs E/xll_storage: init
        01-08 16:51:28.615 26380-26380/com.chaojishipin.sarrs E/xll_storage: /storage/emulated/0

        abtain by Environment: /storage/emulated/0


        -------------------------------device3--------------------
        01-08 16:53:30.628 8717-8717/com.chaojishipin.sarrs E/xll_storage: init
        01-08 16:53:30.633 8717-8717/com.chaojishipin.sarrs E/xll_storage: /storage/emulated/0
        01-08 16:53:30.633 8717-8717/com.chaojishipin.sarrs E/xll_storage: /storage/usbotg

        abtain by Environment: /storage/emulated/0
*/




public class StoragePathsManager {
    private static final String LOG_TAG = "xll_storage";

    private boolean isHM = false;
    private final String DIR = "chaojishipin/movies";
    private final String NEW_DIR = "Android/data/com.chaojishipin.sarrs/files";

    private String finalExterpath="";
    static StoragePathsManager instanse;

    private StoragePathsManager(){
        init();
    }

    public static synchronized final StoragePathsManager getInstanse()
    {
        if(instanse==null){
            instanse=new StoragePathsManager();
        }
        return instanse;
    }

    void deleteV_1_1_0Moviepath(){
        // 覆盖安装需要删除之前文件夹里视频
        String beforePath="/mnt/sdcard/Android/data/com.chaojishipin.sarrs/files/movies";
        File beforeFile=new File(beforePath);
        LogUtil.e(LOG_TAG,"before path "+beforePath);
        if(beforeFile.exists()){
            File[] beforemovies=  beforeFile.listFiles();
            for(File beforeChild:beforemovies){

                boolean candele= beforeChild.delete();

                LogUtil.e(LOG_TAG,"v1.0.1 path exsits delete process");
                if(candele){
                    LogUtil.e(LOG_TAG,"v1.0.1 path exsits delete ok");
                }else{
                    LogUtil.e(LOG_TAG,"v1.0.1 path exsits delete error");
                }
            }
        }else{
            LogUtil.e(LOG_TAG,"before path not exsits  ");
        }
    }

    private void deleV1_0_1Moviepath(){
        // 覆盖安装需要删除之前文件夹里视频
        String beforePath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIR;
        File beforeFile=new File(beforePath);
        LogUtil.e(LOG_TAG, "before path " + beforePath);
        if(beforeFile.exists()){
            File[] beforemovies=  beforeFile.listFiles();
            for(File beforeChild:beforemovies){

                boolean candele= beforeChild.delete();

                LogUtil.e(LOG_TAG,"v1.1.0 path exsits delete process");
                if(candele){
                    LogUtil.e(LOG_TAG,"v1.1.0 path exsits delete ok");
                }else{
                    LogUtil.e(LOG_TAG,"v1.1.0 path exsits delete error");
                }
            }
        }else{
            LogUtil.e(LOG_TAG,"before path not exsits  ");
        }
    }

    public void init(){
        String s1 = Build.MANUFACTURER;
        String s2 = Build.MODEL;
        if(TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2))
            ;
        else if("xiaomi".equalsIgnoreCase(s1) && s2.toLowerCase().contains("hm note")){
            isHM = true;
        }
//        isHM = true;
        reset();
        getExternalSDpath();
    }

    public String getExternalSDpath(){
        if(!TextUtils.isEmpty(finalExterpath) && useful(finalExterpath, true) > 0){
            if(isHM)
                return finalExterpath + "/" + NEW_DIR;
            return finalExterpath + "/" + DIR;
        }
        if(isHM){
            return getExternalPath();
        }else
            return getSDpath();
    }

    /**
     *  add by xll
     *
     *  获取sdcard （多个外置以及包含内置sdcard情况下取sdcard路径）
     *
     * */
    public String getSDpath(){
        LogUtil.e(LOG_TAG, "init");
        List<String>pathList=new ArrayList<>();
        if(!SPUtil.getInstance().getBoolean("init",false)){
            deleV1_0_1Moviepath();
            deleteV_1_1_0Moviepath();
        }
        File parentFile =new File("/storage/");
        File [] childfiles=parentFile.listFiles();

        for(File child:childfiles){
            File subChild=new File(child.getAbsolutePath()+"/"+"tempchaojishipin");
            if(subChild.exists()){
                subChild.delete();
            }
            // 权限筛选
            boolean mkdirSuccess=subChild.mkdirs();
            if(mkdirSuccess){
                if(child.canWrite()&&child.canRead()){
                    String path  =child.getAbsolutePath();
                    if(path.toLowerCase().contains("sdcard")){
                        pathList.add(path);
                        LogUtil.e(LOG_TAG, "child path permission  ok " + child.getAbsolutePath());
                    }

                }
            }else{
                LogUtil.e(LOG_TAG, "child path permission not have " + child.getAbsolutePath());
            }

        }
        // 含有字母、数字排序
        Collections.sort(pathList);

        //  中兴(有外置) /storage/sdcard1  /storage/sdcard0   三星(由外置) /storage/sdcard0  /storage/extSdCard    /lemax(都是内置)  /storage/sdcard0   /storage/sdcard1
        if(pathList.size()>1) {
            for (String str : pathList) {
                if (!str.toLowerCase().equalsIgnoreCase("/storage/sdcard0")) {

                    finalExterpath = str;
                }

                LogUtil.e(LOG_TAG, " sdcard path array>1 " + finalExterpath);
            }
        }
        // 酷派 手机 返回 /storage/emulated/0   （内置）  /storage/sdcard1
        if(pathList.size()==1){
            finalExterpath= pathList.get(0);
            LogUtil.e(LOG_TAG," sdcard path array=1 "+finalExterpath);
        }

        if(pathList.size()==0){
            finalExterpath=Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        LogUtil.e(LOG_TAG," final path "+finalExterpath);
        LogUtil.e(LOG_TAG, "init ok");
        if(!TextUtils.isEmpty(finalExterpath)){
            //外置sdcard路径覆盖安装删除之前数据
            if(!SPUtil.getInstance().getBoolean("init",false)){
                deleV1_1_1moviepath();
            }
            record();
        }
        return finalExterpath;
    }

    private void record(){
        if(isHM)
            SPUtil.getInstance().putString("sdcard",""+finalExterpath + "/" + NEW_DIR);
        else
            SPUtil.getInstance().putString("sdcard",""+finalExterpath + "/" + DIR);
        SPUtil.getInstance().putBoolean("init",true);
    }

    private void reset(){
        SPUtil.getInstance().putString("sdcard", "");
        SPUtil.getInstance().putBoolean("init", false);
    }

    private void deleV1_1_1moviepath(){
        String expath=finalExterpath+"/" + DIR;
        File exFile=new File(expath);
        if(exFile.exists()){
            LogUtil.e(LOG_TAG,"ext path exsits  "+expath);
            File [] exFileArray =exFile.listFiles();
            for(File chileExFile:exFileArray){
                boolean childCandel=  chileExFile.delete();
                if(childCandel){
                    LogUtil.e(LOG_TAG,"ext path exsits delete ok");
                }else{
                    LogUtil.e(LOG_TAG,"ext path exsits delete error");
                }
            }
        }else{
            LogUtil.e(LOG_TAG, "ext path not exsits ");
        }
    }

    private String getExternalPath(){
        LongSparseArray<String> array = getExterPath();
        if(array.size() == 0)
            finalExterpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        else {
            finalExterpath = array.valueAt(array.size() - 1);
            record();
        }

        return finalExterpath;
    }

    private LongSparseArray<String> getExterPath() {
        LongSparseArray<String> path = new LongSparseArray<>();
        try {
            File f = Environment.getExternalStorageDirectory();
            long space = useful(f.getAbsolutePath(), true);
            if (space > 0)
                path.put(space, f.getAbsolutePath());

            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null) {
                        for (int j = 0; j < columns.length; j++) {
                            long tmp = useful(columns[j], true);
                            if (tmp > 0)
                                path.put(tmp, columns[j]);
                        }
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null) {
                        for (int j = 0; j < columns.length; j++) {
                            long tmp = useful(columns[j], true);
                            if (tmp > 0)
                                path.put(tmp, columns[j]);
                        }
                    }
                }
            }

            br.close();
            isr.close();
            is.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return path;
    }

    private File mkDirs(String path, boolean child){
        try{
            File f;
            if(child){
                if(isHM)
                    f = new File(path, NEW_DIR);
                else
                    f = new File(path, DIR);
            }else
                f = new File(path);
            if(f.exists())
                return f;
            if(f.mkdirs())
                return f;

            MediaFile tmp = new MediaFile(ChaoJiShiPinApplication.getInstatnce().getContentResolver(), f);
            if(tmp.mkdir())
                return tmp.getFile();

        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    private long useful(String path, boolean child) {
        try {
            if(TextUtils.isEmpty(path) || !path.startsWith("/"))
                return -1;
            File file = new File(path);
            if (file.canWrite() && file.canRead()) {
                try {
                    file = mkDirs(path, child);
                    if(file == null)
                        return -1;
                    file = new File(file, System.currentTimeMillis() + "");
                    FileOutputStream fout = new FileOutputStream(file);
                    fout.write("a".getBytes());
                    fout.close();
                    file.delete();
                } catch (Throwable e) {
                    e.printStackTrace();
                    return -1;
                }
                if (!child)
                    return 1;
                StatFs statFs = new StatFs(path);
                long blockSize = statFs.getBlockSize();
                long availableBlocks = statFs.getAvailableBlocks();
                if (blockSize > 0 && availableBlocks > 0)
                    return blockSize * availableBlocks;
            }
            return -1;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }
}
