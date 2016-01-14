package com.chaojishipin.sarrs.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import libcore.io.DiskLruCache;

/**
 * Created by zhangshuo on 2015/6/9.
 */
public class ImageLruCache extends LruCache <String,Bitmap>implements ImageLoader.ImageCache{

    private static String CACHE_FOLDER_NAME;

    private static int DISK_CACHE_SIZE;

    private ImageDiskLruCache mImageDiskLruCache;

    public ImageLruCache(int maxSize,String diskCacheFloder,int diskCacheSize) {
        super(maxSize);
        CACHE_FOLDER_NAME = diskCacheFloder;
        DISK_CACHE_SIZE = diskCacheSize;
        try {
            File diskFile = getDiskCacheDir(ChaoJiShiPinApplication.getInstatnce(), CACHE_FOLDER_NAME);
            mImageDiskLruCache = new ImageDiskLruCache(diskFile, diskCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    @Override
    public Bitmap getBitmap(String url) {
        String key = hashKeyForDisk(url);
        try {
            Bitmap bitmapData = mImageDiskLruCache.getBitmap(url);
            if (null == bitmapData) {
                return get(url);
            }
            return bitmapData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
        String key = hashKeyForDisk(url);
        if (!mImageDiskLruCache.containsKey(key)) {
            mImageDiskLruCache.put(key, bitmap);
        }
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return value.getByteCount();
        }
        return value.getRowBytes() * value.getHeight();
    }

    //根据key生成md5值，保证缓存文件名称的合法化
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
