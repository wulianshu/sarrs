package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.download.util.NetworkUtil;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.ImageCacheManager;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.SharePreferenceManager;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.http.bean.LetvBaseBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshuo on 2015/6/6.
 */
public class SlidingMenuLeftAdapter<LetvBaseBean> extends CommonAdapter<LetvBaseBean> {

    public final static int TYPE_COUNT = 2;

    public final static int TYPE_LINE = 0;

    public final static int TYPE_VIEW = 1;

    public List<Boolean> isSelectedList;

    public View lastview;

    public SlidingMenuLeft last_sliding;
    private LinearLayout main_fragment_user_icon_bg;

    public SlidingMenuLeftAdapter(Context context, SarrsArrayList mDatas) {
        super(context, mDatas);
    }

    /**
     * 返回当前一共有几种类型
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        SlidingMenuLeft menuLeft = (SlidingMenuLeft) super.getItem(position);
        //展示分隔线模板
        if (ConstantUtils.SLIDINGMENU_LINE.equals(menuLeft.getContent_type())) {
            return TYPE_LINE;
        }
        return TYPE_VIEW;
    }

    ;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        CommonViewHolder commonViewHolder = null;
        if (TYPE_VIEW == type) {
            commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.slidingmenu_left_view_mode, position);
            SlidingMenuLeft menuLeft = (SlidingMenuLeft) mDatas.get(position);
            TextView title = (TextView) commonViewHolder.getView(R.id.slidingmenu_lefe_view_title);
            title.setText(menuLeft.getTitle());
            ImageView imageView = (ImageView) commonViewHolder.getView(R.id.slidingmenu_left_view_icon);
            // 直播小黄点
            ImageView livePush = (ImageView) commonViewHolder.getView(R.id.live_push);
//          使用三级图片缓存加载ICON
//          ImageCacheManager.loadImage(menuLeft.getIcon(),imageView,null,null);
            if (isSelectedList.get(position)) {
                lastview = commonViewHolder.getmConvertView();
                last_sliding = (SlidingMenuLeft) getItem(position);
                title.setTextColor(mContext.getResources().getColor(R.color.color_F8E71C));
                DisplayImageOptions options1= new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE)
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),false))
                        .showImageForEmptyUri(R.color.color_e7e7e7)
                        .build();

                DisplayImageOptions options2= new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE)
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),true))
                        .showImageForEmptyUri(R.color.color_00000000)
                        .build();

               if(NetworkUtil.isNetworkAvailable(mContext)){
                   ImageLoader.getInstance().displayImage(menuLeft.getIcon(), imageView,options1);
                   ImageLoader.getInstance().displayImage(menuLeft.getIcon_select(), imageView,options2);
               }else{
                   imageView.setImageResource(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),true));
               }

               // imageView.setImageResource(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),true));
            }
            else {
                title.setTextColor(Color.WHITE);
                DisplayImageOptions options1= new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE)
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),true))
                        .showImageForEmptyUri(R.color.color_e7e7e7)
                        .build();

                DisplayImageOptions options2= new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE)
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),false))
                        .showImageForEmptyUri(R.color.color_00000000)
                        .build();

                if(NetworkUtil.isNetworkAvailable(mContext)){
                    ImageLoader.getInstance().displayImage(menuLeft.getIcon_select(), imageView,options1);
                    ImageLoader.getInstance().displayImage(menuLeft.getIcon(), imageView,options2);
                }else{
                    imageView.setImageResource(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),false));
                }

               // imageView.setImageResource(Utils.loadUrl(menuLeft.getContent_type(),menuLeft.getCid(),false));

            }
            // 更新消息推送ui
            refreshLivePushView(menuLeft, livePush);
        } else if (TYPE_LINE == type) {
            LogUtil.e("Line", "" + type);
            commonViewHolder = CommonViewHolder.get(mContext, convertView, parent, R.layout.slidingmenu_left_line_mode, position);
        }
        return commonViewHolder.getmConvertView();
    }

    /**
     *
     * @param position -1
     * @param view
     */
    public void setSelectItem(int position, View view) {
        if (position >= 0 && view != null) {
            int type = getItemViewType(position);
            if (TYPE_VIEW == type) {
                SlidingMenuLeft slidingMenuLeft = (SlidingMenuLeft) getItem(position);
                CommonViewHolder viewHolder = (CommonViewHolder) view.getTag();

                TextView now_tv = viewHolder.getView(R.id.slidingmenu_lefe_view_title);
                ImageView now_iv = viewHolder.getView(R.id.slidingmenu_left_view_icon);
                ImageView livePush = viewHolder.getView(R.id.live_push);
                DisplayImageOptions options1 = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE)
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(Utils.loadUrl(slidingMenuLeft.getContent_type(),slidingMenuLeft.getCid(),true))
                        .showImageForEmptyUri(R.color.color_00000000)
                        .build();
                DisplayImageOptions options2= new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE)
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(Utils.loadUrl(last_sliding.getContent_type(),last_sliding.getCid(),false))
                        .showImageForEmptyUri(R.color.color_00000000)
                        .build();
                if(lastview!=null) {
                    CommonViewHolder lastholder = (CommonViewHolder) lastview.getTag();
                    TextView last_tv = lastholder.getView(R.id.slidingmenu_lefe_view_title);
                    ImageView last_iv = lastholder.getView(R.id.slidingmenu_left_view_icon);
                   if(NetworkUtil.isNetworkAvailable(mContext)){
                        ImageLoader.getInstance().displayImage(last_sliding.getIcon(), last_iv,options2);
                    }else{
                        last_iv.setImageResource(Utils.loadUrl(last_sliding.getContent_type(),last_sliding.getCid(),false));
                    }

                    //last_iv.setImageResource(Utils.loadUrl(last_sliding.getContent_type(),last_sliding.getCid(),false));
                    last_tv.setTextColor(Color.WHITE);
                }

                //now_iv.setImageResource(Utils.loadUrl(slidingMenuLeft.getContent_type(),slidingMenuLeft.getCid(),true));
                if(NetworkUtil.isNetworkAvailable(mContext)){

                    ImageLoader.getInstance().displayImage(slidingMenuLeft.getIcon_select(), now_iv,options1);
                }else{
                    now_iv.setImageResource(Utils.loadUrl(slidingMenuLeft.getContent_type(),slidingMenuLeft.getCid(),true));
                }
                now_tv.setTextColor(mContext.getResources().getColor(R.color.color_F8E71C));
                for (int i = 0; i < isSelectedList.size(); i++) {
                    isSelectedList.set(i, false);
                }
                isSelectedList.set(position, true);
                lastview = view;
                last_sliding = (SlidingMenuLeft) getItem(position);
                // 更新消息推送ui
                refreshLivePushView(slidingMenuLeft, livePush);
            }
        } else if (position == -1) {
            if (lastview != null) {
                CommonViewHolder lastholder = (CommonViewHolder) lastview.getTag();
                TextView last_tv = lastholder.getView(R.id.slidingmenu_lefe_view_title);
                ImageView last_iv = lastholder.getView(R.id.slidingmenu_left_view_icon);
                DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(Utils.loadUrl(last_sliding.getContent_type(), last_sliding.getCid(), false))
                        .showImageForEmptyUri(R.color.color_e7e7e7)
                        .build();
                ImageLoader.getInstance().displayImage(last_sliding.getIcon(), last_iv, options1);
                last_tv.setTextColor(Color.WHITE);
                for (int i = 0; i < isSelectedList.size(); i++) {
                    isSelectedList.set(i, false);
                }
            }
            lastview = null;
            last_sliding = null;
        }
    }

    /**
     * 显示直播频道消息推送提示
     *
     * @param menuLeft
     * @param livePush
     */
    private void refreshLivePushView(SlidingMenuLeft menuLeft, ImageView livePush) {
        if (ConstantUtils.LIVE_CONTENT_TYPE.equalsIgnoreCase(menuLeft.getContent_type())) {
            String serverVersion = menuLeft.getVersion();
            String localVersion = SharePreferenceManager.getVaule(mContext, ConstantUtils.LIVE_PUSH_KEY, "0");
            LogUtil.e(Utils.LIVE_TAG, "##################refreshLivePushView called##################");
            LogUtil.e(Utils.LIVE_TAG, "###################serverVersion is " + serverVersion);
            LogUtil.e(Utils.LIVE_TAG, "###################localVersion is " + localVersion);
            // 本地版本小于服务器版本才显示
            if (!StringUtil.isEmpty(serverVersion) && !StringUtil.isEmpty(localVersion) && Integer.valueOf(localVersion) < Integer.valueOf(serverVersion)) {
                livePush.bringToFront();
                livePush.setVisibility(View.VISIBLE);
            } else
                livePush.setVisibility(View.GONE);
        } else
            livePush.setVisibility(View.GONE);
    }
}