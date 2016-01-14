package com.chaojishipin.sarrs.utils;
import java.io.File;
import java.io.InputStream;
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
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

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

    private static Context mContext;
    private StorageManager mStorageManager;
    private Method mMethodGetPaths;
    private Method mMethodGetPathsState;
    private boolean mIsReflectValide = true;
    private List<String> mAllStoragePathsByMountCommand = new ArrayList<String>();
    private String finalExterpath="";
    static StoragePathsManager instanse;
  public StoragePathsManager(Context context){

      mContext=context;
      mStorageManager=(StorageManager)mContext.
              getSystemService(Context.STORAGE_SERVICE);
  }

    public static final StoragePathsManager getInstanse(Context context)
    {
        if(instanse==null){
            instanse=new StoragePathsManager(context);
        }
        return instanse;


    }


    public String getFinalExterpath(){
        return finalExterpath;
    }


    public void getExternalSdcardpathByInvoke(){
        try{
           if(mStorageManager==null){
               return;
           }
        mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
        mMethodGetPathsState=mStorageManager.getClass().getMethod("getVolumeState",String.class);
        String[]  paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        if(paths==null){
            LogUtil.e(LOG_TAG,"get path invoke() ");
        }
        //Utils.testStorage(mContext);
        if(paths!=null){
            for(String path:paths){

                LogUtil.e(LOG_TAG,path);
                if(path.toLowerCase().contains("sdcard")){

                    finalExterpath=path;

                    LogUtil.e(LOG_TAG,"selected final path invoke() "+path);
                }
            }

        }
       }catch(Exception ex){
        ex.printStackTrace();
        LogUtil.e(LOG_TAG, "init error "+ ex.getMessage());
    }
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




   void deleV1_0_1Moviepath(){
       // 覆盖安装需要删除之前文件夹里视频
       String beforePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/chaojishipin/movies";
       File beforeFile=new File(beforePath);
       LogUtil.e(LOG_TAG,"before path "+beforePath);
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

  /**
   *  add by xll
   *
   *  获取sdcard （多个外置以及包含内置sdcard情况下取sdcard路径）
   *
   * */
   public String getExternalSDpath(){
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

           SPUtil.getInstance().putString("sdcard",""+finalExterpath+"/chaojishipin/movies");
           LogUtil.e(LOG_TAG, "sdcard path put ok : " + finalExterpath + "/chaojishipin/movies");
           SPUtil.getInstance().putBoolean("init",true);
       }




       return finalExterpath;
   }


    void deleV1_1_1moviepath(){
        String expath=finalExterpath+"/chaojishipin/movies";
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
            LogUtil.e(LOG_TAG,"ext path not exsits ");
        }
    }


    private static double getSdTotalSize(String sdPath) {
        double totalSize = 0;
        StatFs sf = new StatFs(sdPath);
        long blockSize = sf.getBlockSize();
        long totalBlocks = sf.getBlockCount();
        totalSize = totalBlocks*blockSize/1024/1024;
        return totalSize;
    }


    /**
     *  add by xll
     *  注意：对于非root用户使用cmd 方式重新挂载目录获取外置sdcard路径不能拿来读写直接，（权限不够）
     *
     *
     * */
    //获取外置sd卡路径集合
    public void init()
    {
        LogUtil.e(LOG_TAG, "init");

        if (TextUtils.isEmpty(finalExterpath))
        {
            Set<String> set = getStoragePathsByCommand();

            mAllStoragePathsByMountCommand.addAll(set);
            if(mAllStoragePathsByMountCommand==null){
                LogUtil.e(LOG_TAG, "execute Commond list null ");
            }
            for (String s : mAllStoragePathsByMountCommand)
            {
                LogUtil.e(LOG_TAG, "abtain by command: " + s);

                    finalExterpath=s;
            }
            if (mAllStoragePathsByMountCommand.size() == 0)
            {
                if (Environment.getExternalStorageDirectory().getPath() != null)
                {
                    mAllStoragePathsByMountCommand.add(Environment.getExternalStorageDirectory().getPath());
                }
            }
            for (String s : mAllStoragePathsByMountCommand)
            {
                LogUtil.e(LOG_TAG, "abtain by Environment: " + s);
                finalExterpath=s;
            }





        }
    }





    private HashSet<String> getStoragePathsByCommand() {
        LogUtil.e(LOG_TAG, "init ok getCommond ");
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        StringBuffer sb = new StringBuffer();
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                sb.append(buffer);
            }
            s = sb.toString();
            LogUtil.e(LOG_TAG,""+s);
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
            LogUtil.e(LOG_TAG, "execute Commond error "+e.getMessage());
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }

    /**
     * @return String. for example /mnt/sdcard
     */
    public String getExternalStoragePath()
    {
        String path = null;
        List<String> allMountedPaths = getMountedStoragePaths();
        String internal = getInternalStoragePath();
        LogUtil.e(LOG_TAG," internal path "+internal);
        for (String s : allMountedPaths)
        {
            if (!s.equals(internal))
            {
                path = s;
                break;
            }
        }

        return path;
    }

    public String getInternalStoragePath()
    {
        // get external path
        String pathExtNotRemovable = null;
        String pathExtRemovable = null;
        String ext = Environment.getExternalStorageDirectory().getPath();
        // if it is removable, the storage is external storage, otherwise internal storage.
        boolean isExtRemovable = Environment.isExternalStorageRemovable();
        List<String> allMountedPaths = getMountedStoragePaths();
        for (String s : allMountedPaths)
        {
            if (s.equals(ext))
            {
                if (isExtRemovable)
                {
                    pathExtRemovable = s;
                }
                else
                {
                    pathExtNotRemovable = s;
                }
                break;
            }
        }

        String intr = null;

        String refPath = null;
        if (pathExtRemovable != null)
        {
            refPath = pathExtRemovable;
        }
        else if (pathExtNotRemovable != null)
        {
            intr = pathExtNotRemovable;
            return intr;
        }

        for (String s : allMountedPaths)
        {
            if (!s.equals(refPath))
            {
                intr = s;
                break;
            }
        }

        return intr;
    }

    /**
     * @return /data/data/com.xxx.xxx/files
     */
    public String getAppStoragePath()
    {
        String path = mContext.getApplicationContext().getFilesDir().getAbsolutePath();
        LogUtil.e(LOG_TAG, "getAppStoragePath: " + path);
        return path;
    }


    private List<String> getMountedStoragePaths()
    {
        if (false == mIsReflectValide)
        {
            return mAllStoragePathsByMountCommand;
        }

        List<String> mountedPaths = new ArrayList<String>();
        String[] paths = getAllStoragePaths();
        LogUtil.e(LOG_TAG, "all paths:");
        if (paths!=null)
        {
            for (String path : paths)
            {
                LogUtil.e(LOG_TAG, "-- path: " + path);
            }
        }

        for (String path : paths)
        {
            if (isMounted(path))
            {
                LogUtil.e(LOG_TAG, "path: " + path + " is mounted");
                mountedPaths.add(path);
            }
        }

        return mountedPaths;
    }

    private String[] getAllStoragePaths()
    {
        String[] paths  =null;
        try{
            paths=(String[])mMethodGetPaths.invoke(mStorageManager);
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
        }catch(IllegalAccessException ex){
            ex.printStackTrace();
        }catch(InvocationTargetException ex){
            ex.printStackTrace();
        }

        return paths;
    }

    private String getVolumeState(String mountPoint){
        String status=null;
        try{
            status=(String)mMethodGetPathsState.invoke(mStorageManager, mountPoint);
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
        }catch(IllegalAccessException ex){
            ex.printStackTrace();
        }catch(InvocationTargetException ex){
            ex.printStackTrace();
        }
        return status;
    }

    private boolean isMounted(String mountPoint)
    {
        String status=null;
        boolean result = false;
        status = getVolumeState(mountPoint);
        if(Environment.MEDIA_MOUNTED.equals(status)){
            result = true;
        }
        return result;
    }
}
