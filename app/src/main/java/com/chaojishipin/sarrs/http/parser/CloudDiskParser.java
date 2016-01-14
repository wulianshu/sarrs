package com.chaojishipin.sarrs.http.parser;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Base64;

import com.chaojishipin.sarrs.bean.CloudDiskBean;
import com.chaojishipin.sarrs.download.download.DownloadEntity;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.Utils;

public class CloudDiskParser extends ResponseBaseParser<CloudDiskBean>{
    private DownloadEntity entity;
	@Override
    public JSONObject getData(String data) throws JSONException {
        byte[] decodeData = Base64.decode(data, Base64.DEFAULT);
        String result = Utils.AES256_decode(decodeData, ConstantUtils.AES_KEY);
        if (!TextUtils.isEmpty(result)) {
            return new JSONObject(result);
        }
        return null;
    }

    @Override
	public CloudDiskBean parse(JSONObject data) throws Exception {
		JSONObject dataObj = data.optJSONObject("data");
		JSONObject videoListObj = dataObj.optJSONObject("video_list");
		CloudDiskBean diskBean = new CloudDiskBean();
		diskBean.setUser_id(videoListObj.optString("user_id"));
		diskBean.setVideo_id(videoListObj.optString("video_id"));
		diskBean.setMedia_id(videoListObj.optString("media_id"));
		diskBean.setDefault_play(videoListObj.optString("default_play"));
		diskBean.setVideo_duration(videoListObj.optString("video_duration"));
		diskBean.setVideo_name(videoListObj.optString("video_name"));
		diskBean.setType(videoListObj.optString("type"));
		/**
		 '1'=>array('FLV_180', 'MP4_180'),
		 '2'=>array('FLV_350', 'MP4_350', 'MP4'),
		 '3'=>array('FLV_1000', 'MP4_800'),
		 '4'=>array('FLV_1300', 'MP4_1300'),
		 '5'=>array('FLV_720P', 'MP4_720P'),
		 '6'=>array('FLV_1080P3M', 'MP4_1080P3M'),
		 '7'=>array('FLV_1080P6M', 'MP4_1080P6M')
		 */
		String vtype = "";
		JSONObject videoObj = null;
		/**
		 * 下载需要先判断数据库是否存在，如果存在，需要使用上次下载使用的码流
		 */
		if (entity != null && entity.getCurrClarity() != null)
		{
			vtype = entity.getCurrClarity();
			int [] indexs = {3, 4, 1, 2, 5, 6, 7};
			int i = 0;
			while (i < indexs.length)
			{
				videoObj = videoListObj.optJSONObject("video_" + indexs[i]);
				if (videoObj.optString("vtype").equals(entity.getCurrClarity()))
					break;;
			}
		}else {
			//先取超清源没有取原画在没有取默认

			videoObj = videoListObj.optJSONObject("video_3");

			if(null == videoObj) {
				videoObj = videoListObj.optJSONObject("video_4");
			}

			if(null == videoObj) {
				videoObj = videoListObj.optJSONObject(diskBean.getDefault_play());
			}
		}


		ArrayList<String> playUrls = new ArrayList<String>(4);

		String mainUrl = Utils.getBase64Decode(videoObj.optString("main_url"));
		if (!TextUtils.isEmpty(mainUrl)) {
			playUrls.add(mainUrl);
//			playUrls.add("http://g3.letv.cn/vod/vasdsdsa2/NjgvMzYvasdsadasdasdsaODEvbGVEzOGFmMjBmLTE0MDI2NTEzMDYwNzkubXA0");
		}

		String backup_url1 = Utils.getBase64Decode(videoObj
				.optString("backup_url_1"));
		if (!TextUtils.isEmpty(backup_url1)) {
			playUrls.add(backup_url1);
//			playUrls.add("http://g3.letv.cn/vod/vasdsdsa2/NjgvMzYvasdsadasdasdsaODEvbGVEzOGFmMjBmLTE0MDI2NTEzMDYwNzkubXA0");
		}

		String backup_url2 = Utils.getBase64Decode(videoObj
				.optString("backup_url_2"));
		if (!TextUtils.isEmpty(backup_url2)) {
			playUrls.add(backup_url2);
//			playUrls.add("http://g3.letv.cn/vod/vasdsdsa2/NjgvMzYvasdsadasdasdsaODEvbGVEzOGFmMjBmLTE0MDI2NTEzMDYwNzkubXA0");
		}

		String backup_url3 = Utils.getBase64Decode(videoObj
				.optString("backup_url_3"));
		if (!TextUtils.isEmpty(backup_url3)) {
			playUrls.add(backup_url3);
		}

		diskBean.setmPlayUrls(playUrls);
		diskBean.setVwidth(videoObj.optString("vwidth"));
		diskBean.setVheight(videoObj.optString("vheight"));
		diskBean.setGbr(videoObj.optString("gbr"));
		diskBean.setStorePath(videoObj.optString("storePath"));
		diskBean.setVtype(videoObj.optString("vtype"));
		diskBean.setDefinition(videoObj.optString("definition"));
		return diskBean;
	}

	public void setEntity(DownloadEntity entity) {
		this.entity = entity;
	}
}
