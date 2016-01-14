package com.chaojishipin.sarrs.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.UpgradeInfo;
import com.chaojishipin.sarrs.http.volley.HttpApi;
import com.chaojishipin.sarrs.http.volley.HttpManager;
import com.chaojishipin.sarrs.http.volley.RequestListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyemin on 2015/10/20.
 */
public class UpgradeHelper {
    public static final String TAG="UpgradeHelper";
    public static final String UPGRADE_DATA = "upgrade_data";
    public static final String FROM_SPLASH = "from_splash";

    public static final String APP_NAME = "chaojishipin.apk";

    //upgrade
    private Handler mHandler;
    private ProgressDialog mProgressDlg;
    private boolean isCancelUpgrade;
    private String mCurVerName;
    private String mSerVerName; //服务器端版本名
    private String mDownUrl;// 下载url
    private String mUpgradeType; // 更新类型

    private Context mContext;
    private Resources mRes;

    public UpgradeHelper(Context mContext) {
        this.mContext = mContext;
        mRes = mContext.getResources();
        //--upgrade
        mCurVerName = Utils.getClientVersionName();
        isCancelUpgrade = false;
        mHandler = new Handler();
        mProgressDlg = new ProgressDialog(mContext);
        mProgressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置进度条是否不明确
        mProgressDlg.setIndeterminate(false);
        mProgressDlg.setCancelable(false);// 屏蔽返回和点击dialog外取消
        mProgressDlg.setProgressNumberFormat("%1d b/%2d b");
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, mRes.getString(R.string.cancel_upgrade_download),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressDlg.cancel();
                        isCancelUpgrade = true;
                        File apkFile = new File(getApkUpgradePath(), APP_NAME);
                        if (apkFile != null) {
                            apkFile.delete();
//                            ToastUtil.showShortToast(mContext, "delete sucess");
                        }
                    }
                });
    }

    public void setmSerVerName(String mSerVerName) {
        this.mSerVerName = mSerVerName;
    }

    public void setmDownUrl(String mDownUrl) {
        this.mDownUrl = mDownUrl;
    }

    public void setmUpgradeType(String mUpgradeType) {
        this.mUpgradeType = mUpgradeType;
    }

    public class CheckNewestVersionAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {

            if (!TextUtils.isEmpty(mUpgradeType)) {
                if (mUpgradeType.equalsIgnoreCase("0"))
                    return 0;
                else if (mUpgradeType.equalsIgnoreCase("1"))
                    return 1;
                else if (mUpgradeType.equalsIgnoreCase("2"))
                    return 2;
                else
                    return 0;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer type) {
            switch (type) {
                case 0:
                    notNewVersionDlgShow(); // 无需更新
                    break;
                case 1:
                    doNewVersionSuggestUpdatebyclick(); // 更新新版本
                    break;
                case 2:
                    doNewVersionForceUpdate(); // 强制升级
                    break;
                default:
                    notNewVersionDlgShow();
                    break;
            }
            super.onPostExecute(type);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    /**
     * 提示更新新版本
     */
    public void doNewVersionSuggestUpdate() {


        if(mSerVerName.compareTo(mCurVerName)>0) {
            String str = mRes.getString(R.string.upgrade_desc1) + mCurVerName + mRes.getString(R.string.upgrade_desc2) + mSerVerName +
                    mRes.getString(R.string.upgrade_desc3);
            Dialog dialog = new AlertDialog.Builder(mContext).setTitle(mRes.getString(R.string.upgrade_title)).setMessage(str)
                    // 设置内容
                    .setPositiveButton(mRes.getString(R.string.upgrade_ok),// 设置确定按钮
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mProgressDlg.setTitle(mRes.getString(R.string.upgrade_downloading_title));
                                    mProgressDlg.setMessage(mRes.getString(R.string.upgrade_downloading_message));
                                    Log.i("upgrade", "doNewVersionSuggestUpdate--->mSerVerName is " + mSerVerName + " mDownUrl is " + mDownUrl);
                                    downFile(mDownUrl);  //开始k下载
                                }
                            })
                    .setNegativeButton(mRes.getString(R.string.upgrade_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.cancel();
                                }
                            }).create();// 创建
            dialog.setCancelable(false);// 屏蔽返回和点击dialog外取消
            // 显示对话框
            dialog.show();
        }
//        else{
//            notNewVersionDlgShow();
//        }

    }

    /**
     * 设置界面点击
     */
    public void doNewVersionSuggestUpdatebyclick() {

        if(mSerVerName.compareTo(mCurVerName)>0) {
            String str = mRes.getString(R.string.upgrade_desc1) + mCurVerName + mRes.getString(R.string.upgrade_desc2) + mSerVerName +
                    mRes.getString(R.string.upgrade_desc3);
            Dialog dialog = new AlertDialog.Builder(mContext).setTitle(mRes.getString(R.string.upgrade_title)).setMessage(str)
                    // 设置内容
                    .setPositiveButton(mRes.getString(R.string.upgrade_ok),// 设置确定按钮
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mProgressDlg.setTitle(mRes.getString(R.string.upgrade_downloading_title));
                                    mProgressDlg.setMessage(mRes.getString(R.string.upgrade_downloading_message));
                                    Log.i("upgrade", "doNewVersionSuggestUpdate--->mSerVerName is " + mSerVerName + " mDownUrl is " + mDownUrl);
                                    downFile(mDownUrl);  //开始k下载
                                }
                            })
                    .setNegativeButton(mRes.getString(R.string.upgrade_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.cancel();
                                }
                            }).create();// 创建
            dialog.setCancelable(false);// 屏蔽返回和点击dialog外取消
            // 显示对话框
            dialog.show();
        }else{
            notNewVersionDlgShow();
        }

    }

    /**
     * 强制升级
     */
    public void doNewVersionForceUpdate() {

        if(mSerVerName.compareTo(mCurVerName)>0) {
            String str = mRes.getString(R.string.upgrade_desc1) + mCurVerName + mRes.getString(R.string.upgrade_desc2) + mSerVerName +
                    mRes.getString(R.string.upgrade_desc3);
            Dialog dialog = new AlertDialog.Builder(mContext).setTitle(mRes.getString(R.string.force_upgrade_title)).setMessage(str)
                    // 设置内容
                    .setPositiveButton(mRes.getString(R.string.force_upgrade_ok),// 设置确定按钮
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mProgressDlg.setTitle(mRes.getString(R.string.upgrade_downloading_title));
                                    mProgressDlg.setMessage(mRes.getString(R.string.upgrade_downloading_message));
                                    Log.i("upgrade", "doNewVersionSuggestUpdate--->mSerVerName is " + mSerVerName + " mDownUrl is " + mDownUrl);
                                    downFile(mDownUrl);  //开始k下载
                                }
                            })
                    .create();// 创建
            dialog.setCancelable(false);// 屏蔽返回和点击dialog外取消
            // 显示对话框
            dialog.show();
        }
    }

    /**
     * 提示当前为最新版本
     */
    private void notNewVersionDlgShow() {
        Log.i("upgrade", "mCurVerName is " + mCurVerName);
        String str = mRes.getString(R.string.no_upgrade_desc1) + mCurVerName + "\n" + mRes.getString(R.string.no_upgrade_desc2);
        Dialog dialog = new AlertDialog.Builder(mContext).setTitle(mRes.getString(R.string.upgrade_title))
                .setMessage(str)// 设置内容
                .setPositiveButton(mRes.getString(R.string.no_upgrade_ok),// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        }).create();// 创建
        dialog.setCancelable(false);// 屏蔽返回和点击dialog外取消
        dialog.show();
    }

    private void downFile(final String url) {
        isCancelUpgrade = false;
        if (url.length() == 0) {
            ToastUtil.showShortToast(mContext, "downUrl is null");
            return;
        }
        // 显示下载进度框
        mProgressDlg.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response = null;
                InputStream is = null;
                FileOutputStream fileOutputStream = null;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    mProgressDlg.setMax((int) length);//设置进度条的最大值
                    is = entity.getContent();
                    if (is != null) {
                        String path = getApkUpgradePath();
                        makeRootDirectory(path);
                        File apkFile = new File(path, APP_NAME);
                        fileOutputStream = new FileOutputStream(apkFile);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        long count = 0;
                        while (!isCancelUpgrade && (ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                                mProgressDlg.setProgress((int) count);
//                                float all = length / 1024 / 1024;
//                                float percent = count / 1024 / 1024;
//                                mProgressDlg.setProgressNumberFormat(String.format("%.2fM/%.2fM", percent, all));
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (!isCancelUpgrade)
                        down();// 下载完成，开始安装
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null)
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (is != null)
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }.start();
    }

    private void down() {
        mHandler.post(new Runnable() {
            public void run() {
                mProgressDlg.cancel();
                update();
            }
        });
    }
    //
    // 安装
    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//启动新的activity
        intent.setDataAndType(Uri.fromFile(new File(getApkUpgradePath(), APP_NAME)),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        System.exit(0);//正常退出App
        //android.os.Process.killProcess(android.os.Process.myPid());
    }


    private void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            Log.d("upgrade", " error is " + e.toString());
        }
    }

    // 版本升级包下载路径
    private String getApkUpgradePath() {
        StringBuilder sb = new StringBuilder(Utils.SDCARD_PATH + "/");
        sb.append("Android/data/")
                .append(ChaoJiShiPinApplication.getInstatnce().getPackageName()).append("/files/Apk").toString();
        return sb.toString();
    }

    public static void requestUpgradeData(RequestListener<UpgradeInfo> listener) {
        //请求版本号数据
        HttpManager.getInstance().cancelByTag(ConstantUtils.REQUEST_UPGRADE);
        HttpApi.
                getUpgradeRequest()
                .start(listener, ConstantUtils.REQUEST_UPGRADE);
    }
}
