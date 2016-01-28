package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.SarrsArrayList;
import com.chaojishipin.sarrs.bean.SlidingMenuLeft;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.FileCacheManager;
import com.chaojishipin.sarrs.utils.StringUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangshuo on 2015/6/6.
 */
public class SlidingMenuLeftParser extends ResponseBaseParser<SarrsArrayList> {

    @Override
    protected JSONObject getData(String data) throws JSONException {
        FileCacheManager fileCacheManager = FileCacheManager.getInstance();
        //进行数据的缓存
        fileCacheManager.writeDataToFile(ConstantUtils.FILECACHE_SLIDINGMENU_DATA, data);
        return super.getData(data);
    }

    @Override
    public SarrsArrayList parse(JSONObject data) throws Exception {
        //如果请求数据成功
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            SarrsArrayList menuList = new SarrsArrayList();
            JSONArray channelArray = data.optJSONArray("items");
            int channelSize = channelArray.length();
            for (int i = 0; i < channelSize; i++) {
                SlidingMenuLeft slidingMenuLeft = new SlidingMenuLeft();
                JSONObject channelObj = channelArray.optJSONObject(i);
                String content_type = channelObj.optString("content_type");
                slidingMenuLeft.setContent_type(content_type);
                slidingMenuLeft.setIcon(channelObj.optString("icon"));
                slidingMenuLeft.setIcon_select(channelObj.optString("icon_select"));
                slidingMenuLeft.setTitle(channelObj.optString("title"));
                slidingMenuLeft.setCid(channelObj.optString("cid"));
                // 直播需要解析构造 version字段
                if (!StringUtil.isEmpty(content_type) && ConstantUtils.LIVE_CONTENT_TYPE.equalsIgnoreCase(content_type)) {
                    slidingMenuLeft.setVersion(channelObj.optString("version"));
                }
                menuList.add(slidingMenuLeft);
            }
            return menuList;
        }
        return null;
    }
}
