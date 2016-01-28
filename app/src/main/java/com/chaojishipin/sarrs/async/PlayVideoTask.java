package com.chaojishipin.sarrs.async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.chaojishipin.sarrs.activity.ChaoJiShiPinVideoDetailActivity;
import com.chaojishipin.sarrs.bean.PlayData;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.bean.LocalVideoEpisode;
import com.chaojishipin.sarrs.fragment.videoplayer.PlayerUtils;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.component.utils.NetWorkTypeUtils;
import com.mylib.download.activity.DownloadJobActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhuo on 2016/1/28.
 */
public class PlayVideoTask extends AsyncTask<Object, android.R.integer, Object> {

    private Context mContext;
    private PlayData playData;
    private boolean isSendLocalHistory;

    public PlayVideoTask(Activity ac){
        mContext = ac;
    }

    @Override
    protected Object doInBackground(Object... params) {
        if (null != params[0] && params[0] instanceof Context) {
            mContext = (Context) params[0];
        }
        if (null != params[1] && params[1] instanceof PlayData) {
            playData = (PlayData) params[1];
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (isXIAOMI(mContext)) {
            LocalVideoEpisode localVideoEpisode = getCurrLocalEisode(playData);
            if (PlayerUtils.DOWNLOAD_M3U8.equalsIgnoreCase(localVideoEpisode.getDownType())) {
                jumpToSystemPlayer(mContext, localVideoEpisode);
            } else {
                jumpToSelfPlayer(mContext, playData);
            }
        } else {
            LocalVideoEpisode localVideoEpisode = getCurrLocalEisode(playData);
            //jumpToSystemPlayer(context, localVideoEpisode);
            if (!TextUtils.isEmpty(localVideoEpisode.getPorder())) {
                playData.setIndex(localVideoEpisode.getIndex());
            }
            jumpToSelfPlayer(mContext, playData);
        }
    }

    private boolean isXIAOMI(Context context) {
        String deviceMode = Utils.getDeviceMode();
        String deviceVersion = Utils.getDeviceVersion();
        return ((deviceMode.contains(PlayerUtils.MI) || deviceMode.contains(PlayerUtils.XIAOMI))
                && deviceVersion.contains(PlayerUtils.XIAOMI_LOCAL_VERSION) && !NetWorkTypeUtils
                .isNetAvailable(context));
    }

    /**
     * 当前播放的视频
     *
     * @param
     */
    private LocalVideoEpisode getCurrLocalEisode(PlayData playData) {
        ArrayList<LocalVideoEpisode> localDataLists = playData.getmLocalDataLists();
        String porder = playData.getPorder();
        if (!TextUtils.isEmpty(porder) && null != localDataLists && localDataLists.size() > 0) {
            int playSize = localDataLists.size();
            boolean isFindEpisode = false;
            // 查找当前影片位置
            for (int i = 0; i < playSize; i++) {
                LocalVideoEpisode localVideoEpisode = localDataLists.get(i);
                if (null != localVideoEpisode) {
                    if (porder.equals(localVideoEpisode.getPorder())) {
                        isFindEpisode = true;
                        return localVideoEpisode;
                    }
                }
            }
            // 如果没找到则取第一个
            if (!isFindEpisode) {
                return localDataLists.get(0);
            }
        }
        return null;
    }

    private void jumpToSystemPlayer(Context context, LocalVideoEpisode localVideoEpisode) {
        String playUrl = "file://" + localVideoEpisode.getPlay_url();
        if (!TextUtils.isEmpty(playUrl)) {
            playUrl.replace(" ", "%20");
            Uri uri = Uri.parse(playUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "video/mp4");
            context.startActivity(intent);
        }
    }

    /**
     * 播放本地视频
     */
    private void jumpToSelfPlayer(Context context, PlayData playData) {
        playData.setIsLocalVideo(true);
        Intent intent = new Intent(context, ChaoJiShiPinVideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Utils.PLAY_DATA, playData);
        LocalVideoEpisode episode=playData.getmLocalDataLists().get(0);
        VideoDetailItem videoDetailItem = new VideoDetailItem();
        List<VideoItem> items=new ArrayList<>();
        VideoItem item=new VideoItem();
        item.setGvid(episode.getGvid());
        items.add(item);
        videoDetailItem.setLocalVideoEpisodes(playData.getmLocalDataLists());
        videoDetailItem.setTitle(episode.getName());
        videoDetailItem.setPorder(episode.getPorder());
        videoDetailItem.setDescription(episode.getDes());
        videoDetailItem.setId(episode.getAid());
        videoDetailItem.setCategory_id(episode.getCid());
        videoDetailItem.setPlay_count(episode.getPlayCount());
        videoDetailItem.setVideoItems(items);
        videoDetailItem.setFromMainContentType(item.getFromMainContentType());
        // videoDetailItem.setDetailImage(item.getImage());
        intent.putExtra("videoDetailItem", videoDetailItem);
        intent.putExtra("ref", DownloadJobActivity.pageid);
        intent.putExtra(Utils.Medea_Mode, ConstantUtils.MediaMode.LOCAL);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 发送数据
        //EventBus.getDefault().post(playData);
        LogUtil.e("Local Media", "from downLoadManager " + playData);
        context.startActivity(intent);
    }
}
