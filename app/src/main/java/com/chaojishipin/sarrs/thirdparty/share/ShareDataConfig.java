package com.chaojishipin.sarrs.thirdparty.share;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.ibest.thirdparty.share.model.ShareData;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by vicky on 15/10/19.
 */
public class ShareDataConfig {
    //分享类型：1-专辑，2-视频，4-专题，5-排行榜，直播暂时没有分享功能
    public static final int ALBULM_SHARE = 1;
    public static final int VIDEO_SHARE = 2;
    public static final int TOPIC_SHARE = 4;
    public static final int RANKING_SHARE = 5;
    public int type = ALBULM_SHARE;

    public String id;
    public String title;
    public String desc;
    public String imageUrl;
    public Bitmap shareBitmap;
    private String source;
    private Activity mContext;
    ShareData shareData;
    /**
     * 分享图片最大尺寸，超过这个尺寸需要压缩
     */
    private static final int max_share_image_size = 32 * 1024;

    public ShareDataConfig(Activity activity)
    {
        this.mContext = activity;
        shareData = new ShareData();
    }

    /**
     *
     * @param id      排行、专题、专辑、单视频 id
     * @param title
     * @param imgUrl  图片地址
     * @param type    分享类型：1-专辑，2-视频，4-专题，5-排行榜，直播暂时没有分享功能
     * @param source  1:专辑 2：单视频
     * @return
     */
    public ShareData configShareData(String id, String title, String imgUrl, int type, String source)
    {
        this.title = (title == null ? "" : title);
        this.id = id;
        this.imageUrl = imgUrl;
        this.type = type;
        this.source = source;
        shareData.setShareTargetUrl(getTarget());
        shareData.setShareContentType(ShareData.SHARE_CONTENT_TYPE_WEBPAGE);
        String qqTitle = mContext.getResources().getString(R.string.app_name);
        String wxFriendTitle = "";
        String wxFrindsTitle = getWXContent();
        String sinaTitle = "";
        Map<String, String> titles = new HashMap<>();
        titles.put(""+ShareData.SHARE_PLATFORM_WX_FRIEND, wxFriendTitle);
        titles.put(""+ShareData.SHARE_PLATFORM_WX_FRIENDS, wxFrindsTitle);
        titles.put(""+ShareData.SHARE_PLATFORM_QQ, qqTitle);
        titles.put(""+ShareData.SHARE_PLATFORM_QQ_ZONE, qqTitle);
        titles.put("" + ShareData.SHARE_PLATFORM_SINA, sinaTitle);
        shareData.setShareTitle(titles);
        /**
         * 不同的平台分享内容可能不同，故我们使用了Map
         */
        String commonText = getWXContent();
        String sinaText = getSinaContent();
        String qqText = getQQContent();
        Map<String, String> content = new HashMap<>();
        content.put(""+ShareData.SHARE_PLATFORM_WX_FRIEND, commonText);
        content.put(""+ShareData.SHARE_PLATFORM_WX_FRIENDS, commonText);
        content.put(""+ShareData.SHARE_PLATFORM_QQ, qqText);
        content.put(""+ShareData.SHARE_PLATFORM_QQ_ZONE, qqText);
        content.put(""+ShareData.SHARE_PLATFORM_SINA, sinaText);
        content.put(""+ShareData.SHARE_PLATFORM_SMS, sinaText);
        content.put(""+ShareData.SHARE_PLATFORM_COPY_LINKING, getTarget());
        shareData.setShareText(content);
        loadShareLogo();
        shareData.setShareBitmap(shareBitmap);
        if (imgUrl != null && imgUrl.length() > 0) {
            shareData.setShareImageUrl(imgUrl);
            getBitmap();
        }


        return shareData;
    }

    public static Bitmap getShareBitmap(Drawable drawable)
    {
        if (drawable == null)
            return null;
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return scaleBitmap(bitmap);
    }

    private static Bitmap scaleBitmap(Bitmap bitmap)
    {
        try {
            int fileSize = bitmap.getByteCount();
            if (fileSize > max_share_image_size)
            {
                int scale = (int)Math.ceil((double)fileSize / (double)max_share_image_size);
                scale = (int)Math.ceil(Math.sqrt(scale));
                int width = bitmap.getWidth() / scale;
                int height = bitmap.getHeight() / scale;
                Bitmap tmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
                fileSize = tmp.getByteCount();
                return Bitmap.createScaledBitmap(bitmap, width, height, true);
            }else {
                return Bitmap.createBitmap(bitmap);
            }
        }catch (Exception e)
        {
            Log.e("exception", e.getMessage());
        }
    return null;
    }

    private void getBitmap()
    {
        if (imageUrl != null && imageUrl.length() > 0)
        {
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().loadImage(imageUrl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    loadShareLogo();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    try {
                        shareBitmap = scaleBitmap(bitmap);
                        shareData.setShareBitmap(shareBitmap);
                    }catch (Exception e)
                    {
                        Log.e("exception", e.getMessage());
                    }

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }else {
            loadShareLogo();
        }

    }

    private void loadShareLogo()
    {
        /**
         * decodeResource 会变成显存里面数据，这个数据尺寸和原始图片尺寸是不一样的。
         */
        Resources res = mContext.getResources();
        shareBitmap = BitmapFactory.decodeResource(res, R.drawable.share_logo);
        shareBitmap = scaleBitmap(shareBitmap);
    }

    private String getTarget()
    {
        //"http://share.chaojishipin.com/sarrs/share?id=25011&type=1&source=1&title=甄嬛传"
        if (source != null)  //视频，专辑
            return "http://share.chaojishipin.com/sarrs/share?id="+id+"&type="+type+"&source="+source+"&title="+title;
        return "http://share.chaojishipin.com/sarrs/share?id="+id+"&type="+type;
    }

    private String getSinaContent()
    {
        if (type == VIDEO_SHARE || type == ALBULM_SHARE)
            return "我正在使用超级视频免费观看#" + title + "#|快来点击“" + getTarget() + "” 观看。";
        return "我最近在超级视频的#" + title + "#里看到了好多精彩的内容|快来点击“" + getTarget() +"” 观看。";
    }

    private String getWXContent()
    {
        if (type == VIDEO_SHARE || type == ALBULM_SHARE)
            return "我正在使用超级视频免费观看《"+ title + "》，邀请你一起来看看！";
        return "我在超级视频《" + title + "》里看到了好多精彩内容，快来看吧 ！";
    }

    private String getQQContent()
    {
        if (type == VIDEO_SHARE || type == ALBULM_SHARE)
            return "我正在看《"+ title + "》，邀请你一起来看看！";
        return "我在《" + title + "》里看到了好多精彩内容，快来看吧 ！";
    }

    /**
     *
     * @param activity
     * @return ArrayList 第一个元素是id，第二个是cid
     */
    public static ArrayList jumpFromShare(Activity activity){
        // 从分享跳到跳到半屏页
        Uri uri = activity.getIntent().getData();
        String dataStr = activity.getIntent().getDataString();
        ArrayList<String> shareParams = new ArrayList<>();
        LogUtil.e("xll ", " share " + dataStr);
        if(uri!=null){
            String cid = uri.getQueryParameter("cid");
            //aid : share过来可以是单视频也可以是单视频
            String id=uri.getQueryParameter("id");
            shareParams.add(id);
            if (cid != null)
                shareParams.add(cid);
        }
        return shareParams;
    }

}
