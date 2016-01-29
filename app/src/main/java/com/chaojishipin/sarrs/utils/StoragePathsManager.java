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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.StorageBean;


public class StoragePathsManager {
    private static final String LOG_TAG = "xll_storage";

    private Context mContext;
    // 优先级情况下获取最终卡path
    private String finalExterpath="";
    // 内置卡path
    private String internalSDpath="";
    // 外置卡path
    private String outSDpath="";

    private static StoragePathsManager instanse;

    private StoragePathsManager(Context context){
        mContext=context.getApplicationContext();
    }

    public static synchronized StoragePathsManager getInstanse(Context context)
    {
        if(instanse==null){
            instanse=new StoragePathsManager(context);
        }
        return instanse;
    }

    private void deleteV_1_1_0Moviepath(){
        // 覆盖安装需要删除之前文件夹里视频
        String beforePath="/mnt/sdcard/Android/data/com.chaojishipin.sarrs/files/movies";
        File beforeFile=new File(beforePath);
        LogUtil.e(LOG_TAG,"before path "+beforePath);
        if(beforeFile.exists()){
           recursionDeleteFile(beforeFile);

        }else{
            LogUtil.e(LOG_TAG,"before path not exsits  ");
        }
    }

    private void deleV1_0_1Moviepath(){
        // 覆盖安装需要删除之前文件夹里视频
        String beforePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/chaojishipin";
        File beforeFile=new File(beforePath);
        LogUtil.e(LOG_TAG, "before path " + beforePath);
        if(beforeFile.exists()){
              recursionDeleteFile(beforeFile);
        }else{
            LogUtil.e(LOG_TAG,"before path not exsits  ");
        }
    }

    public void recursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for (File f : childFile){
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }

    public void deleteallDownloadPath(){
        getStoragePaths();
        deleV1_0_1Moviepath();
        deleteV_1_1_0Moviepath();
        deleV1_1_1moviepath();
    }

    /**
     *  add by xll
     *
     *  获取sdcard （多个外置以及包含内置sdcard情况下取sdcard路径）
     *
     * */
    private String folderName="/chaojishipin/movies";

    public List<StorageBean> getStoragePaths(){
        List<StorageBean> mList=new ArrayList<>();

        LogUtil.e("v1.2.0","build sdk "+Build.VERSION.SDK_INT);
        LogUtil.e("v1.2.0","level sdk "+Build.VERSION_CODES.KITKAT);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT){
            List<String> pathList=new ArrayList<>();
            File[] fs = mContext.getExternalFilesDirs(folderName);
            try{
                 LogUtil.e("v1.2.0","new leve19 check sdcard num is "+fs.length);
                  //打印出拔出sd卡后的可用目录

                 for(File file:fs){
                     if(file!=null){
                         LogUtil.e("v1.2.0","new level19 ok "+file.getAbsolutePath()+" after delete null diretory  ");
                         pathList.add(file.getAbsolutePath());
                     }


                 }
                LogUtil.e("v1.2.0","new api leve19 after delte nousable directroy size is "+pathList.size());
                if(pathList.size()>1){
                    //LogUtil.e("v1.2.0"," size >1 new leve19  "+fs[0].getAbsolutePath());
                    LogUtil.e("v1.2.0"," size >1 new leve19_sdk  "+Environment.getExternalStorageDirectory().getAbsolutePath()+folderName);

                    finalExterpath= fs[1].getAbsolutePath();
                    internalSDpath=Environment.getExternalStorageDirectory().getAbsolutePath()+folderName;
                    outSDpath=fs[1].getAbsolutePath();
                    StorageBean interl=new StorageBean();
                    interl.setName(mContext.getString(R.string.setting_internal));
                    interl.setType(1);
                    interl.setIsClick(false);
                    interl.setIsEnable(true);
                    interl.setPath(internalSDpath);
                    mList.add(interl);
                    // 外置sd卡可用
                    StorageBean outSD=new StorageBean();
                    outSD.setName(mContext.getString(R.string.setting_sd_extend));
                    outSD.setType(2);
                    outSD.setIsClick(true);
                    outSD.setIsEnable(true);
                    outSD.setPath(outSDpath);
                    mList.add(outSD);


                }else if(pathList.size()==1){
                   // LogUtil.e("v1.2.0","new leve19  "+fs[0].getAbsolutePath());
                    LogUtil.e("v1.2.0","new leve19_sdk  "+Environment.getExternalStorageDirectory().getAbsolutePath()+folderName);

                    finalExterpath= Environment.getExternalStorageDirectory().getAbsolutePath()+folderName;
                    internalSDpath=Environment.getExternalStorageDirectory().getAbsolutePath()+folderName;
                    StorageBean interl=new StorageBean();
                    interl.setName(mContext.getString(R.string.setting_internal));
                    interl.setType(1);
                    interl.setIsClick(true);
                    interl.setIsEnable(true);
                    interl.setPath(internalSDpath);
                    mList.add(interl);

                    // 构造 假外置存储
                    StorageBean outSD=new StorageBean();
                    outSD.setName(mContext.getString(R.string.setting_sd_extend));
                    outSD.setType(2);
                    outSD.setIsClick(false);
                    outSD.setIsEnable(false);
                    outSD.setPath("");
                    mList.add(outSD);
                }

            }catch (Exception e){
                e.printStackTrace();
                LogUtil.e("v1.2.0", "new api file error logic " + e.getMessage());

            }


        }else{
            LogUtil.e(LOG_TAG, "init");
            List<String>pathList=new ArrayList<>();
            if(!SPUtil.getInstance().getBoolean("init",false)){
                deleV1_0_1Moviepath();
                deleteV_1_1_0Moviepath();
            }
            // exec();
            File parentFile =new File("/storage/");
            File [] childfiles=parentFile.listFiles();

            for(File child:childfiles){
                File subChild=new File(child.getAbsolutePath()+"/"+"tempchaojishipin.temp");
                if(subChild.exists()){
                    subChild.delete();
                }
                try{



                    boolean isCreate=   subChild.createNewFile();
                    LogUtil.e("v1.2.0", "create file " + isCreate);
                    if(isCreate){
                        LogUtil.e("v1.2.0","create file "+subChild.getAbsolutePath());
                    }


                    if(isCreate){
                        if(child.canWrite()&&child.canRead()){
                            String path  =child.getAbsolutePath();
                            if(path.toLowerCase().contains("sdcard")){
                                pathList.add(path);
                                LogUtil.e(LOG_TAG, "child path permission  ok " + child.getAbsolutePath());
                            }

                        }else{
                            LogUtil.e(LOG_TAG, "child path permission(R&W) not have " + child.getAbsolutePath());

                        }
                    }else{
                        LogUtil.e(LOG_TAG, "child path permission(create) not have " + child.getAbsolutePath());
                    }


                }catch (Exception e){
                    LogUtil.e(LOG_TAG, "child path permission(create exception) not have " + child.getAbsolutePath());
                    LogUtil.e(LOG_TAG,e.getMessage());
                    e.printStackTrace();
                }

                // 权限筛选
                // boolean mkdirSuccess=subChild.mkdirs();


            }
            // 含有字母、数字排序
            Collections.sort(pathList);

            //  中兴(有外置) /storage/sdcard1  /storage/sdcard0   三星(由外置) /storage/sdcard0  /storage/extSdCard    /lemax(都是内置)  /storage/sdcard0   /storage/sdcard1
            if(pathList.size()>1) {
                for (String str : pathList) {
                    if (!str.toLowerCase().equalsIgnoreCase("/storage/sdcard0")) {
                        finalExterpath = str+folderName;
                        outSDpath=str+folderName;
                        StorageBean outSD=new StorageBean();
                        outSD.setName(mContext.getString(R.string.setting_sd_extend));
                        outSD.setType(2);
                        outSD.setIsClick(true);
                        outSD.setIsEnable(true);
                        outSD.setPath(outSDpath);
                        mList.add(outSD);
                    }else{
                        internalSDpath=str+folderName;
                        StorageBean intel=new StorageBean();
                        intel.setName(mContext.getString(R.string.setting_internal));
                        intel.setType(1);
                        intel.setIsClick(false);
                        intel.setIsEnable(true);
                        intel.setPath(internalSDpath);
                        mList.add(intel);
                    }
                    LogUtil.e(LOG_TAG, " sdcard path array>1 " + finalExterpath);
                }
            }
            // 酷派 手机 返回 /storage/emulated/0   （内置）  /storage/sdcard1
            if(pathList.size()==1){
                finalExterpath= pathList.get(0)+folderName;
                internalSDpath=pathList.get(0)+folderName;
                StorageBean interl=new StorageBean();
                interl.setName(mContext.getString(R.string.setting_internal));
                interl.setType(1);
                interl.setIsClick(true);
                interl.setIsEnable(true);
                interl.setPath(internalSDpath);
                mList.add(interl);
                // 构造 假外置存储
                StorageBean outSD=new StorageBean();
                outSD.setName(mContext.getString(R.string.setting_sd_extend));
                outSD.setType(2);
                outSD.setIsClick(false);
                outSD.setIsEnable(false);
                outSD.setPath("");
                mList.add(outSD);

                LogUtil.e(LOG_TAG," sdcard path array=1 "+finalExterpath);
            }

            if(pathList.size()==0){
                finalExterpath=Environment.getExternalStorageDirectory().getAbsolutePath()+folderName;
                internalSDpath=Environment.getExternalStorageDirectory().getAbsolutePath()+folderName;
                StorageBean intel=new StorageBean();
                intel.setName(mContext.getString(R.string.setting_internal));
                intel.setType(1);
                intel.setIsClick(false);
                intel.setIsEnable(true);
                intel.setPath(internalSDpath);
                mList.add(intel);

                // 构造 假外置存储
                StorageBean outSD=new StorageBean();
                outSD.setName(mContext.getString(R.string.setting_sd_extend));
                outSD.setType(2);
                outSD.setIsClick(false);
                outSD.setIsEnable(false);
                outSD.setPath("");
                mList.add(outSD);

            }



        }
        LogUtil.e(LOG_TAG," final path "+finalExterpath);
        LogUtil.e(LOG_TAG, "init ok");
        if(!TextUtils.isEmpty(finalExterpath)){
            //初次安装外置sdcard路径删除之前数据
            if(!SPUtil.getInstance().getBoolean("init",false)){
                deleV1_1_1moviepath();
            }

            SPUtil.getInstance().putString("sdcard",""+finalExterpath);
            LogUtil.e(LOG_TAG, "sdcard path put ok : " + finalExterpath );
            SPUtil.getInstance().putBoolean("init",true);
        }

        return mList;
    }


    private void deleV1_1_1moviepath(){
        String expath=finalExterpath+"/chaojishipin/movies";
        File exFile=new File(expath);
        if(exFile.exists()){
            LogUtil.e(LOG_TAG,"ext path exsits  "+expath);
              recursionDeleteFile(exFile);
        }else{
            LogUtil.e(LOG_TAG,"ext path not exsits ");
        }
    }
}
