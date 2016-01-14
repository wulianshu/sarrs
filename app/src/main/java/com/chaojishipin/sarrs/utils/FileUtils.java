package com.chaojishipin.sarrs.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FileUtils {
    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    /**
     * 保存Image的目录名
     */
    private final static String FOLDER_NAME = "/AndroidImage";


    public FileUtils(Context context) {
        mDataRootPath = context.getCacheDir().getPath();
    }


    /**
     * 获取储存Image的目录
     *
     * @return
     */
    private String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return;
        }
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }
        File file = new File(path + File.separator + fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从手机或者sd卡获取Bitmap
     *
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String fileName) {
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExists(String fileName) {
        return new File(getStorageDirectory() + File.separator + fileName).exists();
    }

    /**
     * 获取文件的大小
     *
     * @param fileName
     * @return
     */
    public long getFileSize(String fileName) {
        return new File(getStorageDirectory() + File.separator + fileName).length();
    }


    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteFile() {
        File dirFile = new File(getStorageDirectory());
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }

        dirFile.delete();
    }



    public synchronized  static void writeHtmlToData(Context mContext,String fileName,String code ) {
        FileOutputStream fileOutputStream = null;
        PrintStream printStream = null;
            LogUtil.e("xll","NEW jscode file  save local ! ");
            try {
                fileOutputStream = mContext.openFileOutput(fileName,mContext.MODE_PRIVATE);
                printStream = new PrintStream(fileOutputStream);
                String header="<!doctype html>\n" +
                        "<html><head>\n" +
                        "    <style type='text/css'>\n" +
                        "        html { font-family:Helvetica; color:#222; }\n" +
                        "        h1 { color:steelblue; font-size:24px; margin-top:24px; }\n" +
                        "        button { margin:0 3px 10px; font-size:12px; }\n" +
                        "        .logLine { border-bottom:1px solid #ccc; padding:4px 2px; font-family:courier; font-size:11px; }\n" +
                        "        </style>\n" +
                        "</head><body>\n" +
                        "    <h1>WebViewJavascriptBridge Demo</h1>\n" +
                        "    <script>";
                String footer="</script>\n" +
                        "</body></html>\n";
                printStream.println(header);
                printStream.println(code);
                printStream.println(footer);
            }  catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    printStream.close();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



    }



}