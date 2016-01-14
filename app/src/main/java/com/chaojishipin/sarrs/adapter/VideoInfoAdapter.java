package com.chaojishipin.sarrs.adapter;

import android.text.TextUtils;

import com.chaojishipin.sarrs.bean.Episode;
import com.chaojishipin.sarrs.bean.VideoDetailItem;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.download.download.DownloadEntity;

/**
 * Created by vicky on 15/8/31.
 */
public class VideoInfoAdapter {
    public static Episode wrapVideoItem(VideoItem item)
    {
        Episode adapter = new Episode();
        adapter.setGlobaVid(item.getGvid());
        adapter.setVid(item.getGvid());
        adapter.setIntro(item.getDescription());
        if(!TextUtils.isEmpty(item.getFromMainContentType())){
            adapter.setDataType(Integer.parseInt(item.getFromMainContentType()));
        }
        if (item.getOrder() != null)
        {
            adapter.setPorder(item.getOrder());
        }else {

        }
        adapter.setName(item.getTitle());
//        adapter.setSerialid(item.getId() + item.getOrder());
        adapter.setSerialid(item.getGvid());
        adapter.setSrc(item.getSource());
        return adapter;
    }

    public static Episode wrapVideoDetail(VideoDetailItem item, int index)
    {
        VideoItem videoItem = item.getVideoItems().get(index);
        Episode adapter = new Episode();
        adapter.setGlobaVid(videoItem.getGvid());
        adapter.setVid(videoItem.getGvid());
        adapter.setIntro(item.getDescription());
        if(!TextUtils.isEmpty(item.getFromMainContentType())) {
            adapter.setDataType(Integer.parseInt(item.getFromMainContentType()));
        }
        if (videoItem.getOrder() != null)
        {
            adapter.setPorder(videoItem.getOrder());
            if (item.getId() != null)      //专辑
//                adapter.setSerialid(item.getId() + videoItem.getOrder());
                adapter.setSerialid(videoItem.getGvid());
            else                            //单视频
                adapter.setSerialid(videoItem.getGvid());

        }else {
            adapter.setPorder("" + (index + 1));
//            adapter.setSerialid(item.getId() + "" + (index + 1));
            adapter.setSerialid(videoItem.getGvid());
        }
        adapter.setName(item.getTitle());
        adapter.setSubName(videoItem.getTitle());
        adapter.setSrc(item.getSource());
        adapter.setCid(item.getCategory_id());
        if (videoItem.getImage() != null && videoItem.getImage().length() > 0)
        {
            adapter.setImage(videoItem.getImage());
        }else {
            adapter.setImage(item.getDetailImage());
        }
        if (item.getId() != null)
            adapter.setAid(item.getId());

        return adapter;
    }

    public static VideoItem wrapEpisode(Episode item)
    {
        VideoItem adapter = new VideoItem();
        adapter.setGvid(item.getGlobaVid());
        adapter.setDescription(item.getIntro());
        adapter.setFromMainContentType("" + item.getDataType());
        adapter.setOrder(item.getPorder());
        adapter.setTitle(item.getName());
        adapter.setSource(item.getSrc());
        return adapter;
    }
    public static VideoItem wrapDownloadEntity(DownloadEntity entity)
    {
        VideoItem adapter = new VideoItem();
        adapter.setGvid(entity.getGlobaVid());
        adapter.setDescription(entity.getDesc());
        adapter.setFromMainContentType("" + entity.getDataType());
        adapter.setOrder(entity.getPorder());
        adapter.setTitle(entity.getFolderName());
        adapter.setSource(entity.getSrc());
        //专辑才有aid，单视频id使用的vid，此处不设置aid，方便其他页面根据aid是否为空来判断是否是专辑
        if (!entity.getMid().equals(entity.getGlobaVid())) {
            adapter.setId(entity.getMid());
        }
        adapter.setCategory_id(entity.getCid());
        adapter.setImage(entity.getImage());
        return adapter;
    }
}
