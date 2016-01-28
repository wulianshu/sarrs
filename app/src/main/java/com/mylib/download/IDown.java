package com.mylib.download;

/**
 * Created by liuzhuo on 2016/1/21.
 */
public interface IDown {

    /**
     * 下载功能是成功，用户主动取消下载，认为该操作仍然是成功
     */
    int DOWNLOAD_SUCCESS = 0;

    /**
     * 下载地址有效，可以使用该地址，如果下载中断或者失败，可以继续使用该地址重试下载
     */
    int DOWNLOAD_URL_VALID = 1;

    /**
     * 下载地址无效，需要更换地址重新下载
     */
    int DOWNLOAD_URL_INVALID = 2;

    /**
     * 下载文件错误
     */
    int DOWNLOAD_FILE_ERROR = 3;

    boolean download();
    boolean pauseTask();
}
