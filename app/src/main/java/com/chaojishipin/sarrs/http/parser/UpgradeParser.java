package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.UpgradeInfo;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;

import org.json.JSONObject;

/**
 * Created by wangyemin on 2015/8/27.
 */
public class UpgradeParser extends ResponseBaseParser<UpgradeInfo> {

    @Override
    public UpgradeInfo parse(JSONObject data) throws Exception {
        UpgradeInfo upgradeInfo = null;
        if (data.has("status") && data.optString("status").equalsIgnoreCase("200")) {
            upgradeInfo = JsonUtil.parseObject(data.toString(), UpgradeInfo.class);
        }
        Log.i("upgrade", " " + upgradeInfo);
        return upgradeInfo;
    }
}
