package com.chaojishipin.sarrs.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.ImageInfo;
import com.chaojishipin.sarrs.photo.ImageFloder;

import java.io.File;
import java.io.FilenameFilter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by xulinlin on 2015/8/5.
 */
public class LocalImageLoader implements  Runnable{
    private String TAG="LocalImageLoader";
    private Context ctx;
    private Handler mainHandler;
    private String dirPath;
    public List<ImageInfo>mImageArr=new ArrayList<ImageInfo>();

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    int totalCount = 0;
    /**
     * 扫描拿到所有的图片文件夹
     */

    public List<String> getMImages(){
        return mImgs;
    }
    public File getDirFile(){
        return mImgDir;
    }
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();


    private static final String mUriImage = MediaStore.Images.Media.DATA;
    public List<ImageInfo> getLocoalBitmaps(){
        return mImageArr;
    }

    public LocalImageLoader(Context cx,Handler mHand){
       this.ctx=cx;
       this.mainHandler=mHand;
    }
    public List<ImageFloder> getMImageFloders(){
        return this.mImageFloders;
    }
    public int getTotalCount(){
        return totalCount;
    }
    public String getDirPath(){
       return dirPath;
   }
    @Override
    public void run() {
        String firstImage = null;
        ContentResolver contentR = ctx.getContentResolver();
        String[] projection = { MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";
        String[] selectionArgs = { "image/jpeg", "image/png"  };
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        Cursor mCursor=null;
        try{
            mCursor = contentR.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection,
                    selectionArgs, sortOrder);
            if( null == mCursor)
            {
                LogUtil.d(TAG,"cursor is null");
                return;
            }

//            while( mCursor.moveToNext()){
//                // 获取图片的路径
//                String path = mCursor.getString(mCursor
//                        .getColumnIndex(MediaStore.Images.Media.DATA));
//                // 获取该图片的父路径名
//                File parentFile = new File(path).getParentFile();
//                if (parentFile == null)
//                    continue;
//                String dirPath = parentFile.getAbsolutePath();
//                ImageFloder imageFloder = null;
//                totalCount++;
//           }

            while (mCursor.moveToNext())
            {
                // 获取图片的路径
                String path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));

                LogUtil.e("TAG", path);
                // 拿到第一张图片的路径
                if (firstImage == null)
                    firstImage = path;
                // 获取该图片的父路径名
                File parentFile = new File(path).getParentFile();
                if (parentFile == null)
                    continue;
                String dirPath = parentFile.getAbsolutePath();
                ImageFloder imageFloder = null;
                // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                if (mDirPaths.contains(dirPath))
                {
                    continue;
                } else
                {
                    mDirPaths.add(dirPath);
                    // 初始化imageFloder
                    imageFloder = new ImageFloder();
                    imageFloder.setDir(dirPath);
                    imageFloder.setFirstImagePath(path);
                }

                int picSize = parentFile.list(new FilenameFilter()
                {
                    @Override
                    public boolean accept(File dir, String filename)
                    {
                        if (filename.endsWith(".jpg")
                                || filename.endsWith(".png")
                                || filename.endsWith(".jpeg"))
                            return true;
                        return false;
                    }
                }).length;
                totalCount += picSize;

                imageFloder.setCount(picSize);
                mImageFloders.add(imageFloder);

                if (picSize > mPicsSize)
                {
                    mPicsSize = picSize;
                    mImgDir = parentFile;
                }
            }

            // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                Message msg = Message.obtain();
                msg.what = ConstantUtils.LOAD_END;
                mainHandler.sendMessage(msg);

        }catch(Exception e){
            LogUtil.e(TAG, e.getMessage());
        }
        finally{
            if(mCursor!=null){
                mCursor.close();
            }

        }
    }

}
