package com.chaojishipin.sarrs.utils;

import android.content.Context;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by zhangshuo on 2015/6/11.
 */
public class FileCacheManager {

    public static final String DEFAULT_CHARSET ="UTF-8";

    private FileCacheManager(){

    }

    private static class FileCacheManagerHolder {
        private static final FileCacheManager INSTANCE = new FileCacheManager();
    }

    public static final FileCacheManager getInstance() {
        return FileCacheManagerHolder.INSTANCE;
    }

    public void writeDataToFile(String fileName,String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = ChaoJiShiPinApplication.getInstatnce().openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String redFileContent(String fileName) {
        String content = null;
        try {
            FileInputStream inputStream = ChaoJiShiPinApplication.getInstatnce().openFileInput(fileName);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            content = new String(arrayOutputStream.toByteArray(),DEFAULT_CHARSET);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 读取assets文件夹中指定文件名的内容
     * @param fileName
     * @return
     */
    public String readFileFromAsset(String fileName) {
        String content = null;
        try {
            //打开指定的文件夹
            InputStream is = ChaoJiShiPinApplication.getInstatnce().getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (-1 != is.read(buffer)) {
                arrayOutputStream.write(buffer, 0, buffer.length);
            }
            is.close();
            arrayOutputStream.close();
            content = new String(arrayOutputStream.toByteArray(), DEFAULT_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

};
